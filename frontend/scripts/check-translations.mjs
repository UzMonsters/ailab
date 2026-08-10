import fs from 'node:fs';
import path from 'node:path';

const messagesDir = path.resolve('src/messages');
const locales = ['en', 'ru', 'uz'];

function load(locale) {
  return JSON.parse(fs.readFileSync(path.join(messagesDir, `${locale}.json`), 'utf8'));
}

function keys(value, prefix = '') {
  if (!value || typeof value !== 'object' || Array.isArray(value)) return [prefix];
  return Object.entries(value).flatMap(([key, child]) => keys(child, prefix ? `${prefix}.${key}` : key));
}

const source = load('en');
const sourceKeys = new Set(keys(source));
const missing = [];

for (const locale of locales.slice(1)) {
  const localeKeys = new Set(keys(load(locale)));
  for (const key of sourceKeys) if (!localeKeys.has(key)) missing.push(`${locale}:${key}`);
  for (const key of localeKeys) if (!sourceKeys.has(key)) missing.push(`en:${key}`);
}

if (missing.length) {
  console.error(`Translation key mismatch (${missing.length}):`);
  console.error(missing.join('\n'));
  process.exit(1);
}

console.log(`Translation keys are aligned for ${locales.join(', ')}.`);
