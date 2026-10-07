// Hosted browser acceptance for the isolated v2 learner fixture. Never production.
import assert from 'node:assert/strict';
import { mkdtemp } from 'node:fs/promises';
import { tmpdir } from 'node:os';
import { join } from 'node:path';
import { fileURLToPath } from 'node:url';
import { spawn } from 'node:child_process';
import { createConnection } from 'node:net';
import { preview } from 'vite';
import { chromium } from 'playwright';

const url = 'http://127.0.0.1:5010/learner-preview/';
const profile = await mkdtemp(join(tmpdir(), 'gm-v2-a11y-'));
const errors = [];
let context;
let previewServer;
let apiProcess;

async function assertUnused(port) {
  await new Promise((resolve, reject) => {
    const socket = createConnection({ host: '127.0.0.1', port });
    socket.once('connect', () => {
      socket.destroy();
      reject(Error(`Fixture port ${port} already in use`));
    });
    socket.once('error', error =>
      error.code === 'ECONNREFUSED' ? resolve() : reject(error),
    );
  });
}

async function startServers() {
  await assertUnused(5010);
  await assertUnused(5011);
  apiProcess = spawn(
    process.execPath,
    [
      '--import',
      'tsx',
      fileURLToPath(
        new URL('../../../services/api/src/local.ts', import.meta.url),
      ),
    ],
    {
      cwd: fileURLToPath(new URL('../../../', import.meta.url)),
      env: { ...process.env, GM_DEMO_PORT: '5011' },
      stdio: ['ignore', 'ignore', 'inherit'],
    },
  );

  let ready = false;
  for (let i = 0; i < 100; i += 1) {
    if (apiProcess.exitCode !== null)
      throw Error('Fresh fixture API failed to start');
    try {
      await fetch('http://127.0.0.1:5011/v2/profile');
      ready = true;
      break;
    } catch {
      await new Promise(resolve => setTimeout(resolve, 100));
    }
  }
  if (!ready) throw Error('Fresh fixture API startup timed out');
  previewServer = await preview({
    configFile: fileURLToPath(
      new URL('../vite.learner-preview.config.ts', import.meta.url),
    ),
  });
}

function normalize(value) {
  return (value ?? '').replace(/\s+/g, ' ').trim();
}

async function activeDescriptor(page) {
  return page.evaluate(() => {
    const element = document.activeElement;
    if (!(element instanceof HTMLElement)) return null;
    return {
      tag: element.tagName.toLowerCase(),
      type: element instanceof HTMLInputElement ? element.type : null,
      text: (element.getAttribute('aria-label') || element.innerText || '').replace(/\s+/g, ' ').trim(),
      id: element.id || null,
    };
  });
}

async function tabToButton(page, name, limit = 80) {
  for (let i = 0; i < limit; i += 1) {
    await page.keyboard.press('Tab');
    const active = await activeDescriptor(page);
    if (active?.tag === 'button' && active.text === name) return active;
  }
  throw Error(`Keyboard could not reach button "${name}"`);
}

async function tabToTextInput(page, limit = 80) {
  for (let i = 0; i < limit; i += 1) {
    await page.keyboard.press('Tab');
    const active = await activeDescriptor(page);
    if (active?.tag === 'input' && active.type === 'text') return active;
  }
  throw Error('Keyboard could not reach a text answer input');
}

async function assertHeadingFocus(page, name) {
  const heading = page.getByRole('heading', { name, level: 1 });
  await heading.waitFor();
  try {
    await page.waitForFunction(
      expected => {
        const active = document.activeElement;
        return active instanceof HTMLHeadingElement &&
          active.tagName === 'H1' &&
          (active.textContent ?? '').replace(/\\s+/g, ' ').trim() === expected;
      },
      name,
      { timeout: 5000 },
    );
  } catch (error) {
    const active = await activeDescriptor(page);
    throw new Error(
      `Expected heading focus on "${name}", active element was ${JSON.stringify(active)}`,
      { cause: error },
    );
  }
  assert.equal(
    await heading.evaluate(element => document.activeElement === element),
    true,
    `Expected heading focus on "${name}"`,
  );
}

async function auditView(page, label) {
  const report = await page.evaluate(() => {
    const visible = element => {
      if (!(element instanceof HTMLElement)) return false;
      const style = getComputedStyle(element);
      const rect = element.getBoundingClientRect();
      return (
        style.display !== 'none' &&
        style.visibility !== 'hidden' &&
        rect.width > 0 &&
        rect.height > 0
      );
    };

    const accessibleName = element => {
      const aria = element.getAttribute('aria-label');
      if (aria?.trim()) return aria.trim();
      const labelledBy = element.getAttribute('aria-labelledby');
      if (labelledBy) {
        const value = labelledBy
          .split(/\s+/)
          .map(id => document.getElementById(id)?.textContent ?? '')
          .join(' ')
          .trim();
        if (value) return value;
      }
      if (
        element instanceof HTMLInputElement ||
        element instanceof HTMLSelectElement ||
        element instanceof HTMLTextAreaElement
      ) {
        const labels = [...(element.labels ?? [])]
          .map(item => item.textContent ?? '')
          .join(' ')
          .trim();
        if (labels) return labels;
      }
      return (element.textContent ?? '').trim();
    };

    const controls = [
      ...document.querySelectorAll(
        'button:not([disabled]), input:not([disabled]):not([type="hidden"]), select:not([disabled]), textarea:not([disabled]), summary, a[href]',
      ),
    ].filter(visible);

    const effectiveRect = element => {
      if (
        element instanceof HTMLInputElement &&
        ['radio', 'checkbox'].includes(element.type) &&
        element.labels?.[0]
      ) {
        return element.labels[0].getBoundingClientRect();
      }
      return element.getBoundingClientRect();
    };

    const smallTargets = controls
      .map(element => ({ element, rect: effectiveRect(element) }))
      .filter(({ rect }) => rect.width < 44 || rect.height < 44)
      .map(({ element, rect }) => ({
        name: accessibleName(element),
        tag: element.tagName.toLowerCase(),
        width: Math.round(rect.width * 10) / 10,
        height: Math.round(rect.height * 10) / 10,
      }));

    const offscreen = controls
      .map(element => ({ element, rect: element.getBoundingClientRect() }))
      .filter(
        ({ rect }) =>
          rect.left < -0.5 ||
          rect.right > window.innerWidth + 0.5,
      )
      .map(({ element }) => accessibleName(element));

    const ids = [...document.querySelectorAll('[id]')].map(
      element => element.id,
    );
    const duplicateIds = [...new Set(ids.filter((id, i) => ids.indexOf(id) !== i))];

    const headings = [...document.querySelectorAll('h1,h2,h3,h4,h5,h6')]
      .filter(visible)
      .map(element => ({
        level: Number(element.tagName.slice(1)),
        text: (element.textContent ?? '').trim(),
      }));

    const root = document.querySelector('.gm-foundation');
    const tokens = root ? getComputedStyle(root) : null;
    const token = name => tokens?.getPropertyValue(name).trim() ?? '';

    const rgb = value => {
      const hex = value.match(/^#([0-9a-f]{6})$/i);
      if (hex) {
        return [
          Number.parseInt(hex[1].slice(0, 2), 16),
          Number.parseInt(hex[1].slice(2, 4), 16),
          Number.parseInt(hex[1].slice(4, 6), 16),
        ];
      }
      const shortHex = value.match(/^#([0-9a-f]{3})$/i);
      if (shortHex) {
        return [...shortHex[1]].map(part => Number.parseInt(part + part, 16));
      }
      const match = value.match(/rgba?\(\s*([\d.]+)[, ]+\s*([\d.]+)[, ]+\s*([\d.]+)/i);
      return match ? [Number(match[1]), Number(match[2]), Number(match[3])] : null;
    };
    const luminance = color => {
      const channels = color.map(value => {
        const v = value / 255;
        return v <= 0.04045 ? v / 12.92 : ((v + 0.055) / 1.055) ** 2.4;
      });
      return 0.2126 * channels[0] + 0.7152 * channels[1] + 0.0722 * channels[2];
    };
    const contrast = (a, b) => {
      const first = rgb(a);
      const second = rgb(b);
      if (!first || !second) return null;
      const l1 = luminance(first);
      const l2 = luminance(second);
      return (Math.max(l1, l2) + 0.05) / (Math.min(l1, l2) + 0.05);
    };

    const background = token('--gm-background');
    const surface = token('--gm-surface');
    const text = token('--gm-text');
    const secondary = token('--gm-secondary');
    const primary = token('--gm-primary');
    const onPrimary = token('--gm-onPrimary');

    return {
      mainCount: document.querySelectorAll('main').length,
      language: document.querySelector('main')?.getAttribute('lang') ?? null,
      h1Count: document.querySelectorAll('main h1').length,
      headings,
      unnamedControls: controls
        .filter(element => !accessibleName(element))
        .map(element => element.tagName.toLowerCase()),
      smallTargets,
      offscreen,
      duplicateIds,
      horizontalOverflow:
        document.documentElement.scrollWidth > window.innerWidth + 1,
      contrast: {
        textOnBackground: contrast(text, background),
        secondaryOnBackground: contrast(secondary, background),
        textOnSurface: contrast(text, surface),
        onPrimary: contrast(onPrimary, primary),
      },
    };
  });

  assert.equal(report.mainCount, 1, `${label}: expected one main landmark`);
  assert.ok(
    report.language === 'en' || report.language === 'de',
    `${label}: main landmark needs an explicit language`,
  );
  assert.equal(report.h1Count, 1, `${label}: expected one visible page h1`);
  assert.ok(
    report.headings.every(heading => heading.text.length > 0),
    `${label}: empty heading`,
  );
  assert.deepEqual(report.unnamedControls, [], `${label}: unnamed control`);
  assert.deepEqual(report.smallTargets, [], `${label}: target smaller than 44 CSS px`);
  assert.deepEqual(report.offscreen, [], `${label}: off-screen interactive control`);
  assert.deepEqual(report.duplicateIds, [], `${label}: duplicate element id`);
  assert.equal(report.horizontalOverflow, false, `${label}: horizontal overflow`);
  for (const [pair, ratio] of Object.entries(report.contrast)) {
    assert.ok(
      ratio !== null && ratio >= 4.5,
      `${label}: ${pair} contrast ${ratio ?? 'unreadable'} is below 4.5:1`,
    );
  }

  return report;
}

async function assertKeyboardFocusVisible(page, label) {
  const focus = await page.evaluate(() => {
    const element = document.activeElement;
    if (!(element instanceof HTMLElement)) return null;
    const style = getComputedStyle(element);
    return {
      tag: element.tagName.toLowerCase(),
      outlineStyle: style.outlineStyle,
      outlineWidth: style.outlineWidth,
    };
  });
  assert.ok(focus, `${label}: no keyboard focus`);
  assert.notEqual(focus.outlineStyle, 'none', `${label}: focus outline hidden`);
  assert.notEqual(focus.outlineWidth, '0px', `${label}: focus outline has zero width`);
}

async function assertTwoHundredPercentText(page, label) {
  const style = await page.addStyleTag({
    content: '.gm-foundation{font-size:32px!important}',
  });
  const report = await auditView(page, `${label} at 200% text`);
  await style.evaluate(element => element.remove());
  return report;
}

try {
  await startServers();
  context = await chromium.launchPersistentContext(profile, {
    headless: true,
    viewport: { width: 320, height: 800 },
  });
  const page = context.pages()[0];
  page.on('pageerror', error => errors.push(error.message));
  await page.goto(url);
  await page
    .getByText('Local preview saved for offline launch.', { exact: true })
    .waitFor();

  await assertHeadingFocus(page, 'Set up your practice');
  const setup = await auditView(page, 'setup');
  await page.keyboard.press('Tab');
  await assertKeyboardFocusVisible(page, 'setup');
  await tabToButton(page, 'Save preferences');
  await page.keyboard.press('Enter');

  await assertHeadingFocus(page, 'Ready to practise?');
  const home = await auditView(page, 'home');
  const homeAria = await page.locator('main').ariaSnapshot();
  assert.match(homeAria, /navigation/);
  assert.match(homeAria, /heading "Ready to practise?"/);
  await assertTwoHundredPercentText(page, 'home');

  await tabToButton(page, 'Topics');
  await page.keyboard.press('Enter');
  await assertHeadingFocus(page, 'Topics');
  const topics = await auditView(page, 'topics');

  await tabToButton(page, 'Home');
  await page.keyboard.press('Enter');
  await assertHeadingFocus(page, 'Ready to practise?');
  await tabToButton(page, 'Start short practice');
  await page.keyboard.press('Enter');

  let textInputs = 0;
  for (let attempt = 0; attempt < 5; attempt += 1) {
    const questionHeading = page.locator('main h1[lang="de"]');
    await questionHeading.waitFor();
    try {
      await page.waitForFunction(
        () => document.activeElement instanceof HTMLHeadingElement &&
          document.activeElement.matches('main h1[lang="de"]'),
        undefined,
        { timeout: 5000 },
      );
    } catch (error) {
      const active = await activeDescriptor(page);
      throw new Error(
        `Practice question heading should receive focus, active element was ${JSON.stringify(active)}`,
        { cause: error },
      );
    }
    textInputs = await page.locator('fieldset.gm-answer-group input').evaluateAll(
      elements => elements.filter(element => element instanceof HTMLInputElement && element.type === 'text').length,
    );
    if (textInputs > 0) break;
    await tabToButton(page, 'Skip');
    await page.keyboard.press('Enter');
    await page.waitForTimeout(75);
  }
  assert.ok(textInputs > 0, 'Expected a text-based exercise within the fixture session');

  for (let index = 0; index < textInputs; index += 1) {
    await tabToTextInput(page);
    await page.keyboard.type('Test');
  }
  await page.keyboard.press('Enter');
  const feedback = page.locator('.gm-feedback');
  await feedback.waitFor();
  assert.equal(
    await feedback.evaluate(element => document.activeElement === element),
    true,
    'Provisional feedback should receive focus after keyboard submission',
  );
  assert.ok(
    (await feedback.getByRole('status').count()) > 0,
    'Feedback should expose a live status',
  );
  const practice = await auditView(page, 'practice feedback');
  await assertTwoHundredPercentText(page, 'practice feedback');

  assert.deepEqual(errors, []);
  console.log(
    JSON.stringify(
      {
        result: 'passed',
        browser: await context.browser().version(),
        viewport: '320x800 CSS px',
        evidence: [
          'single main landmark and page h1',
          'explicit page language',
          'named form and navigation controls',
          'keyboard setup save, navigation, practice start and answer submission',
          'visible keyboard focus indicator',
          'question and feedback focus management',
          'live feedback status',
          '44 CSS px effective control targets',
          'no horizontal overflow at 320 CSS px',
          'no horizontal overflow with 200% learner text',
          'foundation token contrast pairs >= 4.5:1',
          'no duplicate ids or horizontally clipped interactive controls',
        ],
        setup,
        home,
        topics,
        practice,
      },
      null,
      2,
    ),
  );
} finally {
  await context?.close();
  await new Promise(resolve =>
    previewServer ? previewServer.httpServer.close(resolve) : resolve(),
  );
  apiProcess?.kill();
}
