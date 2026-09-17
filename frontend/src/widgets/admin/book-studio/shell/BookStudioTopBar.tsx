'use client';

import { useCallback, useState, useRef, useEffect } from 'react';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import {
  ArrowLeft, Undo2, Redo2, Eye, CheckCircle, Upload,
  ChevronDown, Save, Loader2, AlertCircle,
} from 'lucide-react';
import { useBookStudioStore, labelEntity } from '../store/useBookStudioStore';
import { ContextToolbar } from '../toolbar/ContextToolbar';
import { useTranslations } from 'next-intl';

export function BookStudioTopBar({ onCreateBook }: { onCreateBook?: () => void }) {
  const t = useTranslations('bookStudio');
  const pathname = usePathname();
  const locale = pathname.split('/')[1] || 'en';
  const {
    book, page, books, contentLocale, previewMode, dirty, busy, notice, error,
    history, future, undo, redo, save, publishBook, setContentLocale, setPreviewMode,
    setSelected, loadBook,
  } = useBookStudioStore();

  const [bookDropdownOpen, setBookDropdownOpen] = useState(false);
  const dropdownRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const handler = (e: MouseEvent) => {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target as Node)) {
        setBookDropdownOpen(false);
      }
    };
    document.addEventListener('mousedown', handler);
    return () => document.removeEventListener('mousedown', handler);
  }, []);

  const chapters = book ? ((book as any).chapters || []) : [];
  const chapterLabel = chapters.length > 0 ? labelEntity(chapters[0]) : '';
  const pageLabel = page ? labelEntity(page) : '';

  return (
    <header className="flex items-center gap-2 border-b border-white/10 bg-[#0c121e] px-3 py-1.5 text-sm text-slate-200" style={{ minHeight: 44 }}>
      <Link
        href={`/${locale}/admin/dashboard`}
        className="flex items-center gap-1 rounded px-2 py-1 text-slate-400 hover:text-white hover:bg-white/5 transition-colors"
        aria-label={t('back')}
      >
        <ArrowLeft size={16} />
      </Link>

      <div className="relative" ref={dropdownRef}>
        <button
          onClick={() => setBookDropdownOpen(v => !v)}
          className="flex items-center gap-1 rounded px-2 py-1 font-semibold hover:bg-white/5 transition-colors max-w-[200px] truncate"
        >
          <span className="truncate">{book ? labelEntity(book) : 'Book Studio'}</span>
          <ChevronDown size={14} className="shrink-0 text-slate-400" />
        </button>
        {bookDropdownOpen && (
          <div className="absolute left-0 top-full z-50 mt-1 w-64 rounded-lg border border-white/10 bg-[#141b2a] shadow-xl">
            <div className="p-2">
              <input
                className="w-full rounded border border-white/10 bg-[#080c14] px-2 py-1.5 text-xs text-white outline-none focus:border-violet-500"
                placeholder={t('search') + '...'}
                autoFocus
              />
            </div>
            <div className="max-h-60 overflow-y-auto p-1">
              {books.map(item => (
                <button
                  key={String(item.id)}
                  onClick={() => { void loadBook(String(item.id)); setBookDropdownOpen(false); }}
                  className={`w-full rounded px-3 py-2 text-left text-sm transition-colors ${
                    book?.id === item.id
                      ? 'bg-violet-600/20 text-violet-300'
                      : 'text-slate-300 hover:bg-white/5'
                  }`}
                >
                  {labelEntity(item)}
                </button>
              ))}
            </div>
            <div className="border-t border-white/10 p-1">
              <button
                onClick={() => {
                  onCreateBook?.();
                  setBookDropdownOpen(false);
                }}
                className="flex w-full items-center gap-2 rounded px-3 py-2 text-left text-sm text-violet-400 hover:bg-white/5 transition-colors"
              >
                + {t('createBook') || 'Create Book'}
              </button>
            </div>
          </div>
        )}
      </div>

      {page && (
        <span className="text-xs text-slate-500 truncate max-w-[160px]">
          {chapterLabel && <>{chapterLabel} / </>}{pageLabel}
        </span>
      )}

      <div className="flex items-center gap-1 ml-2 text-xs">
        {busy ? (
          <span className="flex items-center gap-1 text-slate-400"><Loader2 size={12} className="animate-spin" />{t('saving')}</span>
        ) : dirty ? (
          <span className="text-amber-400">{t('unsaved')}</span>
        ) : notice ? (
          <span className="flex items-center gap-1 text-emerald-400"><CheckCircle size={12} />{t('saved')}</span>
        ) : error ? (
          <span className="flex items-center gap-1 text-rose-400" title={error}>
            <AlertCircle size={12} />
            <span className="max-w-[300px] truncate">{error}</span>
          </span>
        ) : null}
      </div>

      <div className="flex-1" />

      <ContextToolbar />

      <div className="flex-1" />

      <div className="flex items-center gap-1">
        <button
          onClick={undo}
          disabled={!history.length}
          className="flex h-7 w-7 items-center justify-center rounded hover:bg-white/5 disabled:opacity-30 transition-colors"
          aria-label="Undo"
        >
          <Undo2 size={15} />
        </button>
        <button
          onClick={redo}
          disabled={!future.length}
          className="flex h-7 w-7 items-center justify-center rounded hover:bg-white/5 disabled:opacity-30 transition-colors"
          aria-label="Redo"
        >
          <Redo2 size={15} />
        </button>

        <div className="mx-1 h-4 w-px bg-white/10" />

        <select
          value={contentLocale}
          onChange={e => setContentLocale(e.target.value as any)}
          className="rounded border border-white/10 bg-[#080c14] px-1.5 py-0.5 text-xs text-white outline-none"
        >
          {(['ru', 'uz', 'en'] as const).map(l => (
            <option key={l} value={l}>{l.toUpperCase()}</option>
          ))}
        </select>

        <div className="mx-1 h-4 w-px bg-white/10" />

        <button
          onClick={() => setPreviewMode(previewMode === 'none' ? 'two-page' : 'none')}
          className={`flex items-center gap-1 rounded px-2 py-1 text-xs transition-colors ${
            previewMode !== 'none' ? 'bg-violet-600/20 text-violet-300' : 'hover:bg-white/5 text-slate-400'
          }`}
        >
          <Eye size={14} />
          <span>{t('preview')}</span>
        </button>

        <button
          onClick={() => void save()}
          disabled={busy || !page}
          className="flex items-center gap-1 rounded bg-violet-600 px-3 py-1 text-xs font-semibold text-white hover:bg-violet-500 disabled:opacity-40 transition-colors"
        >
          <Save size={13} />
          <span>{t('save')}</span>
        </button>
        <button
          onClick={() => void publishBook()}
          disabled={busy || !book}
          className="flex items-center gap-1 rounded border border-emerald-400/30 px-3 py-1 text-xs font-semibold text-emerald-300 hover:bg-emerald-400/10 disabled:opacity-40 transition-colors"
        >
          <Upload size={13} />
          <span>Publish</span>
        </button>
      </div>
    </header>
  );
}
