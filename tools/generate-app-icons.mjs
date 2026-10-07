import { readFile, writeFile, mkdir } from 'node:fs/promises';
import { resolve, join } from 'node:path';
import { pathToFileURL } from 'node:url';

// Authoring-only dependency; normal builds consume checked-in assets.
const sharp = (await import(process.argv[2] ? pathToFileURL(resolve(process.argv[2])).href : 'sharp')).default;
const root = resolve(import.meta.dirname, '..');
const source = await readFile(join(root, 'design/app-icon.svg'), 'utf8');
const maskable = source.replace('rx="112"', 'rx="0"');
const round = source.replace('<rect width="512" height="512" rx="112" fill="#2457C5"/>', '<circle cx="256" cy="256" r="256" fill="#2457C5"/>');
const foreground = source.replace(/  <rect[^>]+\/>\n/, '').replace('<g fill=', '<g transform="translate(38.4 38.4) scale(.85)" fill=').replace('  <path', '  <g transform="translate(38.4 38.4) scale(.85)"><path').replace('/>\n</svg>', '/></g>\n</svg>');
async function raster(svg, size, path, format='png') {
  await mkdir(resolve(path, '..'), { recursive:true });
  await sharp(Buffer.from(svg)).resize(size,size).toFormat(format, {lossless:true}).toFile(path);
}
for (const directory of ['apps/web/client/public','apps/web/learner-product/public']) {
  const base=join(root,directory);
  await mkdir(base,{recursive:true});
  await writeFile(join(base,'favicon.svg'),source);
  await raster(source,64,join(base,'favicon.png'));
  await raster(source,180,join(base,'icons/apple-touch-icon.png'));
  for (const size of [192,512]) await raster(source,size,join(base,`icons/icon-${size}.png`));
  await raster(maskable,512,join(base,'icons/icon-maskable-512.png'));
}
for (const [density,size] of [['mdpi',48],['hdpi',72],['xhdpi',96],['xxhdpi',144],['xxxhdpi',192]]) {
  const base=join(root,`apps/android/app/src/main/res/mipmap-${density}`);
  await raster(source,size,join(base,'ic_launcher.webp'),'webp');
  await raster(round,size,join(base,'ic_launcher_round.webp'),'webp');
  await raster(foreground,Math.round(size*108/48),join(base,'ic_launcher_foreground.webp'),'webp');
}
await raster(maskable,512,join(root,'apps/android/play-store-listing/app-icon.png'));
console.log('Generated website, Android launcher and store-listing icons from design/app-icon.svg.');
