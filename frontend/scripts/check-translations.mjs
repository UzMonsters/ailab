import fs from 'node:fs';import path from 'node:path';
const locales=['en','ru','uz'],dir=path.resolve('src/messages');
const keys=(value,prefix='')=>!value||typeof value!=='object'||Array.isArray(value)?[prefix]:Object.entries(value).flatMap(([key,child])=>keys(child,prefix?`${prefix}.${key}`:key));
const sets=Object.fromEntries(locales.map(locale=>[locale,new Set(keys(JSON.parse(fs.readFileSync(path.join(dir,`${locale}.json`),'utf8'))))]));const source=sets.en,missing=[];
for(const locale of locales.slice(1)){for(const key of source)if(!sets[locale].has(key))missing.push(`${locale}:${key}`);for(const key of sets[locale])if(!source.has(key))missing.push(`en:${key}`)}
if(missing.length){console.error(`Translation key mismatch (${missing.length}):\n${missing.join('\n')}`);process.exit(1)}console.log(`Translation keys are aligned for ${locales.join(', ')}.`);
