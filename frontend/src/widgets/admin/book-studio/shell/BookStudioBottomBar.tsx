'use client';

import { Grid3X3, Compass, ZoomIn, ZoomOut, Maximize, Monitor } from 'lucide-react';
import { useBookStudioStore } from '../store/useBookStudioStore';
import { useTranslations } from 'next-intl';

export function BookStudioBottomBar() {
  const t = useTranslations('bookStudio');
  const {
    page, pages, blocks, zoom, setZoom, showGrid, setShowGrid, showGuides, setShowGuides,
    setPan,
  } = useBookStudioStore();

  const pageIndex = pages.findIndex(p => p.id === page?.id);
  const totalPages = pages.length;

  const zoomPresets = [0.25, 0.5, 0.75, 1, 1.25, 1.5, 2];

  return (
    <footer className="flex items-center justify-between border-t border-white/10 bg-[#0c121e] px-3 py-1 text-xs text-slate-400" style={{ minHeight: 32 }}>
      <div className="flex items-center gap-3">
        <span>
          {t('pageIndicator', { current: pageIndex + 1, total: totalPages })}
        </span>
        {blocks.length > 0 && (
          <span className="text-slate-500">
            {blocks.length} {blocks.length === 1 ? 'block' : 'blocks'}
          </span>
        )}
      </div>

      <div className="flex items-center gap-1">
        <button
          onClick={() => setShowGrid(!showGrid)}
          className={`flex h-6 w-6 items-center justify-center rounded transition-colors ${
            showGrid ? 'bg-violet-600/20 text-violet-300' : 'hover:bg-white/5'
          }`}
          title={t('grid')}
          aria-label={t('grid')}
          aria-pressed={showGrid}
        >
          <Grid3X3 size={13} />
        </button>
        <button
          onClick={() => setShowGuides(!showGuides)}
          className={`flex h-6 w-6 items-center justify-center rounded transition-colors ${
            showGuides ? 'bg-violet-600/20 text-violet-300' : 'hover:bg-white/5'
          }`}
          title={t('guides')}
          aria-label={t('guides')}
          aria-pressed={showGuides}
        >
          <Compass size={13} />
        </button>
      </div>

      <div className="flex items-center gap-1">
        <button
          onClick={() => setZoom(zoom - 0.1)}
          className="flex h-6 w-6 items-center justify-center rounded hover:bg-white/5 transition-colors"
          title={t('zoomOut')}
          aria-label={t('zoomOut')}
        >
          <ZoomOut size={13} />
        </button>
        <button
          onClick={() => setZoom(0.75)}
          className="min-w-[40px] rounded px-1 py-0.5 text-center text-xs hover:bg-white/5 transition-colors"
        >
          {Math.round(zoom * 100)}%
        </button>
        <button
          onClick={() => setZoom(zoom + 0.1)}
          className="flex h-6 w-6 items-center justify-center rounded hover:bg-white/5 transition-colors"
          title={t('zoomIn')}
          aria-label={t('zoomIn')}
        >
          <ZoomIn size={13} />
        </button>
        <div className="mx-1 h-3 w-px bg-white/10" />
        <button
          onClick={() => window.dispatchEvent(new Event('fit-book-page'))}
          className="flex items-center gap-1 rounded px-1.5 py-0.5 hover:bg-white/5 transition-colors"
          title={t('fitPage')}
          aria-label={t('fitPage')}
        >
          <Maximize size={12} />
          <span>{t('fitPage')}</span>
        </button>
      </div>
    </footer>
  );
}
