'use client';

import { useEffect, useState } from 'react';
import { EditorContent, useEditor } from '@tiptap/react';
import StarterKit from '@tiptap/starter-kit';
import Underline from '@tiptap/extension-underline';
import TextAlign from '@tiptap/extension-text-align';
import { Color, FontSize, LineHeight, TextStyle } from '@tiptap/extension-text-style';
import Highlight from '@tiptap/extension-highlight';
import Link from '@tiptap/extension-link';
import Subscript from '@tiptap/extension-subscript';
import Superscript from '@tiptap/extension-superscript';
import FontFamily from '@tiptap/extension-font-family';
import { TableKit } from '@tiptap/extension-table';

const control = 'grid h-8 min-w-8 place-items-center rounded-md border border-white/10 bg-white/[.04] px-2 text-xs font-semibold text-slate-200 transition hover:border-violet-400 disabled:opacity-40';
const extensions = [StarterKit.configure({ link: false }), Underline, TextAlign.configure({ types: ['heading', 'paragraph'] }), TextStyle, Color, Highlight.configure({ multicolor: true }), Link.configure({ openOnClick: false }), Subscript, Superscript, FontFamily, FontSize, LineHeight.configure({ types: ['heading', 'paragraph'] }), TableKit.configure({ table: { resizable: true } })];
type Props = { content: string; onChange: (html: string) => void; onEditStart?: () => void; onEditEnd?: () => void };

export function RichTextEditor({ content, onChange, onEditStart, onEditEnd }: Props) {
  const [linkOpen, setLinkOpen] = useState(false);
  const [linkUrl, setLinkUrl] = useState('https://');
  const editor = useEditor({ extensions, content, immediatelyRender: false, onUpdate: ({ editor: next }) => onChange(next.getHTML()), onFocus: onEditStart, onBlur: onEditEnd });
  useEffect(() => { if (editor && !editor.isFocused && editor.getHTML() !== content) editor.commands.setContent(content, { emitUpdate: false }); }, [content, editor]);
  if (!editor) return <div className="min-h-44 animate-pulse rounded-lg bg-white/5"/>;
  const openLink = () => { setLinkUrl(String(editor.getAttributes('link').href || 'https://')); setLinkOpen(true); };
  const applyLink = (href = linkUrl) => { const value = href.trim(); if (!value) editor.chain().focus().unsetLink().run(); else editor.chain().focus().extendMarkRange('link').setLink({ href: value }).run(); setLinkOpen(false); };
  return <div className="space-y-2">
    <div className="flex flex-wrap gap-1" role="toolbar" aria-label="Text formatting">
      <select aria-label="Text style" value={editor.isActive('heading', { level: 1 }) ? 'h1' : editor.isActive('heading', { level: 2 }) ? 'h2' : editor.isActive('heading', { level: 3 }) ? 'h3' : 'p'} onChange={event => { const value = event.target.value; if (value === 'p') editor.chain().focus().setParagraph().run(); else editor.chain().focus().toggleHeading({ level: Number(value.slice(1)) as 1 | 2 | 3 }).run(); }} className={`${control} w-28 bg-[#111827]`}><option value="p">Paragraph</option><option value="h1">Heading 1</option><option value="h2">Heading 2</option><option value="h3">Heading 3</option></select>
      <select aria-label="Font family" defaultValue="" onChange={event => event.target.value ? editor.chain().focus().setFontFamily(event.target.value).run() : editor.chain().focus().unsetFontFamily().run()} className={`${control} w-24 bg-[#111827]`}><option value="">Default</option><option>Georgia</option><option>Arial</option><option>Times New Roman</option><option>Courier New</option></select>
      <select aria-label="Font size" defaultValue="" onChange={event => event.target.value ? editor.chain().focus().setFontSize(event.target.value).run() : editor.chain().focus().unsetFontSize().run()} className={`${control} w-20 bg-[#111827]`}><option value="">Size</option>{['10px', '12px', '14px', '16px', '18px', '24px', '32px', '48px'].map(size => <option key={size}>{size}</option>)}</select>
      <select aria-label="Line height" defaultValue="" onChange={event => event.target.value ? editor.chain().focus().setLineHeight(event.target.value).run() : editor.chain().focus().unsetLineHeight().run()} className={`${control} w-20 bg-[#111827]`}><option value="">Leading</option>{['1', '1.2', '1.4', '1.6', '2'].map(value => <option key={value}>{value}</option>)}</select>
      <button type="button" className={control} onClick={() => editor.chain().focus().toggleBold().run()} aria-pressed={editor.isActive('bold')} aria-label="Bold"><b>B</b></button><button type="button" className={control} onClick={() => editor.chain().focus().toggleItalic().run()} aria-pressed={editor.isActive('italic')} aria-label="Italic"><i>I</i></button><button type="button" className={control} onClick={() => editor.chain().focus().toggleUnderline().run()} aria-pressed={editor.isActive('underline')} aria-label="Underline"><u>U</u></button><button type="button" className={control} onClick={() => editor.chain().focus().toggleStrike().run()} aria-pressed={editor.isActive('strike')} aria-label="Strike"><s>S</s></button>
      <button type="button" className={control} onClick={() => editor.chain().focus().toggleBulletList().run()} aria-label="Bullet list">• List</button><button type="button" className={control} onClick={() => editor.chain().focus().toggleOrderedList().run()} aria-label="Numbered list">1. List</button>
      <button type="button" className={control} onClick={() => editor.chain().focus().insertTable({ rows: 3, cols: 3, withHeaderRow: true }).run()} aria-label="Insert table">Table</button>
      {editor.isActive('table') && <><button type="button" className={control} onClick={() => editor.chain().focus().addRowAfter().run()} aria-label="Add table row">+ Row</button><button type="button" className={control} onClick={() => editor.chain().focus().deleteRow().run()} aria-label="Delete table row">− Row</button><button type="button" className={control} onClick={() => editor.chain().focus().addColumnAfter().run()} aria-label="Add table column">+ Column</button><button type="button" className={control} onClick={() => editor.chain().focus().deleteColumn().run()} aria-label="Delete table column">− Column</button><button type="button" className={control} onClick={() => editor.chain().focus().deleteTable().run()} aria-label="Delete table">Delete table</button></>}
      {(['left', 'center', 'right', 'justify'] as const).map(value => <button key={value} type="button" className={control} onClick={() => editor.chain().focus().setTextAlign(value).run()} aria-pressed={editor.isActive({ textAlign: value })} aria-label={`${value} align`}>{value[0].toUpperCase()}</button>)}
      <button type="button" className={control} onClick={openLink} aria-pressed={editor.isActive('link')} aria-label="Edit link">Link</button><button type="button" className={control} onClick={() => editor.chain().focus().toggleSubscript().run()} aria-label="Subscript">x₂</button><button type="button" className={control} onClick={() => editor.chain().focus().toggleSuperscript().run()} aria-label="Superscript">x²</button>
      <label className={`${control} cursor-pointer`}>A<input aria-label="Text color" type="color" className="h-4 w-4" onChange={event => editor.chain().focus().setColor(event.target.value).run()}/></label><label className={`${control} cursor-pointer`}>▰<input aria-label="Highlight color" type="color" className="h-4 w-4" onChange={event => editor.chain().focus().toggleHighlight({ color: event.target.value }).run()}/></label>
      <button type="button" className={control} onClick={() => editor.chain().focus().undo().run()} disabled={!editor.can().undo()} aria-label="Undo">↶</button><button type="button" className={control} onClick={() => editor.chain().focus().redo().run()} disabled={!editor.can().redo()} aria-label="Redo">↷</button>
    </div>
    {linkOpen && <form className="flex gap-2 rounded-lg border border-violet-400/30 bg-[#111827] p-2" onSubmit={event => { event.preventDefault(); applyLink(); }}><label className="sr-only" htmlFor="book-link-url">Link URL</label><input id="book-link-url" autoFocus value={linkUrl} onChange={event => setLinkUrl(event.target.value)} className="min-w-0 flex-1 rounded-md border border-white/10 bg-black/20 px-3 py-1.5 text-xs text-white" placeholder="https://example.com"/><button type="submit" className={control}>Apply</button><button type="button" className={control} onClick={() => applyLink('')}>Remove</button><button type="button" className={control} onClick={() => setLinkOpen(false)}>Cancel</button></form>}
    <EditorContent editor={editor} className="min-h-44 rounded-lg border border-white/10 bg-[#fffdf5] p-4 text-sm leading-6 text-slate-950 outline-none focus-within:border-violet-500 [&_.tiptap]:min-h-36 [&_.tiptap]:outline-none [&_h1]:text-3xl [&_h1]:font-bold [&_h2]:text-2xl [&_h2]:font-bold [&_h3]:text-xl [&_h3]:font-bold [&_ol]:list-decimal [&_ol]:pl-5 [&_ul]:list-disc [&_ul]:pl-5 [&_table]:w-full [&_table]:border-collapse [&_td]:border [&_td]:border-slate-300 [&_td]:p-1 [&_th]:border [&_th]:border-slate-400 [&_th]:bg-slate-100 [&_th]:p-1"/>
  </div>;
}
