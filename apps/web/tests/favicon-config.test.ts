import { existsSync, readFileSync, statSync } from "node:fs";
import path from "node:path";
import { fileURLToPath } from "node:url";

import { describe, expect, it } from "vitest";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

describe("favicon configuration", () => {
  it.each(['client', 'learner-product'])("wires usable browser icons in %s/index.html", (entry) => {
    const indexPath = path.resolve(__dirname, "..", entry, "index.html");
    const indexHtml = readFileSync(indexPath, "utf8");

    expect(indexHtml).toContain('rel="icon"');
    expect(indexHtml).toMatch(/href="\/favicon\.png(?:\?[^\"]*)?"/);
    expect(indexHtml).toMatch(/href="\/favicon\.svg(?:\?[^\"]*)?"/);
    expect(existsSync(path.resolve(__dirname, '..', entry, 'public', 'favicon.svg'))).toBe(true);
  });

  it('ships the production install icons at their declared dimensions', () => {
    const base=path.resolve(__dirname, '..', 'learner-product', 'public');
    const manifest=JSON.parse(readFileSync(path.join(base,'manifest.webmanifest'),'utf8'));
    expect(manifest.id).toBe('/');
    expect(manifest.icons.some((icon:{purpose:string})=>icon.purpose==='maskable')).toBe(true);
    for(const icon of manifest.icons) {
      const bytes=readFileSync(path.join(base,icon.src));
      expect(bytes.subarray(1,4).toString()).toBe('PNG');
      expect(`${bytes.readUInt32BE(16)}x${bytes.readUInt32BE(20)}`).toBe(icon.sizes);
    }
  });

  it("ships the favicon asset in public/", () => {
    const faviconPath = path.resolve(__dirname, "..", "client", "public", "favicon.png");

    expect(existsSync(faviconPath)).toBe(true);
    expect(statSync(faviconPath).size).toBeGreaterThan(0);
  });
});

describe("search indexing metadata", () => {
  const publicDir = path.resolve(__dirname, "..", "client", "public");
  const clientDir = path.resolve(__dirname, "..", "client");
  const canonicalOrigin = "https://germanmaster.qortxai.com";

  it("ships a sitemap with canonical public URLs", () => {
    const sitemapPath = path.join(publicDir, "sitemap.xml");
    const sitemap = readFileSync(sitemapPath, "utf8");

    expect(sitemap).toContain('xmlns="http://www.sitemaps.org/schemas/sitemap/0.9"');
    expect(sitemap).toContain(`<loc>${canonicalOrigin}/</loc>`);
    expect(sitemap).toContain(`<loc>${canonicalOrigin}/privacy</loc>`);
    expect(sitemap).toContain(`<loc>${canonicalOrigin}/delete-account</loc>`);
    expect(sitemap).not.toContain("germanverbmaster.com");
  });

  it("points crawlers at the sitemap from robots.txt", () => {
    const robotsPath = path.join(publicDir, "robots.txt");
    const robots = readFileSync(robotsPath, "utf8");

    expect(robots).toContain("User-agent: *");
    expect(robots).toContain("Allow: /");
    expect(robots).toContain(`Sitemap: ${canonicalOrigin}/sitemap.xml`);
    expect(robots).not.toContain("germanverbmaster.com");
  });

  it("defines canonical and social metadata for public pages", () => {
    const pages = [
      { file: path.join(clientDir, "index.html"), url: `${canonicalOrigin}/` },
      { file: path.join(publicDir, "privacy.html"), url: `${canonicalOrigin}/privacy` },
      { file: path.join(publicDir, "delete-account.html"), url: `${canonicalOrigin}/delete-account` },
    ];

    for (const page of pages) {
      const html = readFileSync(page.file, "utf8");

      expect(html).toContain(`<link rel="canonical" href="${page.url}"`);
      expect(html).toContain(`property="og:url" content="${page.url}"`);
      expect(html).toContain('property="og:site_name" content="German Master"');
      expect(html).toContain('property="og:image" content="https://germanmaster.qortxai.com/icons/icon-512.png"');
      expect(html).toContain('name="twitter:card" content="summary"');
      expect(html).not.toContain("germanverbmaster.com");
    }
  });
});
