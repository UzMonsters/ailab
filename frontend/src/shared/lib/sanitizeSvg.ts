const BLOCKED_ELEMENTS = ['script', 'foreignObject', 'iframe', 'object', 'embed', 'audio', 'video'];

export function sanitizeSvgMarkup(markup: string): string {
  if (!markup.trim() || typeof DOMParser === 'undefined') return '';
  const document = new DOMParser().parseFromString(markup, 'image/svg+xml');
  if (document.querySelector('parsererror') || document.documentElement.tagName.toLowerCase() !== 'svg') return '';
  document.querySelectorAll(BLOCKED_ELEMENTS.join(',')).forEach((element) => element.remove());
  document.querySelectorAll('*').forEach((element) => {
    for (const attribute of Array.from(element.attributes)) {
      const name = attribute.name.toLowerCase();
      const value = attribute.value.trim().toLowerCase();
      if (name.startsWith('on') || ((name === 'href' || name === 'xlink:href' || name === 'src') && !value.startsWith('#') && !value.startsWith('data:image/'))) element.removeAttribute(attribute.name);
    }
  });
  return new XMLSerializer().serializeToString(document.documentElement);
}
