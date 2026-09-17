'use client';

import { useBookStudioStore } from '../store/useBookStudioStore';
import { BookPageRenderer, hydrateBookPageBlocks } from '../BookPageRenderer';
import { BookSelectionOverlay } from './BookSelectionOverlay';
import { BookAlignmentGuides } from './BookAlignmentGuides';
import { labelEntity } from '../store/useBookStudioStore';

export function BookPageCanvas() {
  const {
    blocks, selectedBlockId, setSelected, scenarios, showGrid,
    previewMode, pages, page, contentLocale,
  } = useBookStudioStore();

  const nextPage = pages[pages.findIndex(p => p.id === page?.id) + 1];
  const sortedBlocks = [...blocks].sort((a, b) => a.z - b.z);

  const scenarioName = (id: string) => {
    const s = scenarios.find(sc => String(sc.id) === id);
    return s ? labelEntity(s) : id;
  };

  const handleInteract = (scenarioId: string) => {
    const { setInteractiveDraft, setInteractionMode } = useBookStudioStore.getState();
    import('../../scenario/scenario.model').then(({ scenarioDraft }) => {
      import('@/entities/admin/api/platform-admin.api').then(({ adminPlatformApi }) => {
        void adminPlatformApi.scenarios.get(scenarioId).then(data => {
          setInteractiveDraft(scenarioDraft(data));
          setInteractionMode('interactive');
        });
      });
    });
  };

  const nextPageBlocks = nextPage
    ? hydrateBookPageBlocks(nextPage.blocks, contentLocale)
    : [];

  return (
    <div className={`flex gap-2 ${previewMode === 'two-page' ? '' : ''}`}>
      <div
        className="relative mx-auto overflow-hidden bg-[#fff9e9] text-slate-950 shadow-2xl"
        style={{ width: 590, height: 760 }}
        onPointerDown={e => {
          if (e.target === e.currentTarget || (e.target as HTMLElement).getAttribute('aria-label') === 'Book page canvas') {
            setSelected('');
          }
        }}
        aria-label="Book page canvas"
      >
        <div className="pointer-events-none absolute bottom-4 left-0 right-0 text-center text-xs text-slate-400 font-serif">
          {pages.findIndex(p => p.id === page?.id) + 1}
        </div>
        {showGrid && (
          <div
            className="pointer-events-none absolute inset-0 opacity-10"
            style={{
              backgroundImage: 'linear-gradient(rgba(0,0,0,.1) 1px, transparent 1px), linear-gradient(90deg, rgba(0,0,0,.1) 1px, transparent 1px)',
              backgroundSize: '20px 20px',
            }}
          />
        )}
        {sortedBlocks.length === 0 && !previewMode && (
          <div className="pointer-events-none absolute inset-0 flex flex-col items-center justify-center text-sm text-slate-400/50">
            <p>Drag elements here</p>
            <p className="mt-1 text-xs">or use the left panel to add content</p>
          </div>
        )}
        <BookPageRenderer blocks={sortedBlocks} scenarioName={scenarioName} onInteract={handleInteract} onSelectBlock={setSelected} />
        {selectedBlockId && <BookAlignmentGuides />}
        {selectedBlockId && <BookSelectionOverlay />}
      </div>
      {previewMode === 'two-page' && (
        <div
          className="relative mx-auto overflow-hidden bg-[#fff9e9] text-slate-950 shadow-2xl"
          style={{ width: 590, height: 760 }}
        >
          {nextPage ? (
            <BookPageRenderer
              blocks={nextPageBlocks}
              scenarioName={scenarioName}
              onInteract={handleInteract}
            />
          ) : (
            <div className="absolute inset-0 grid place-items-center text-sm text-slate-400">End of book</div>
          )}
        </div>
      )}
    </div>
  );
}
