'use client';

import { useBookStudioStore } from '../store/useBookStudioStore';
import { BookPageRenderer, hydrateBookPageBlocks, BookBlockRenderer } from '../BookPageRenderer';
import { BookSelectionOverlay } from './BookSelectionOverlay';
import { BookAlignmentGuides } from './BookAlignmentGuides';
import { labelEntity } from '../store/useBookStudioStore';
import { BookBlockFrame } from './BookBlockFrame';

export function BookPageCanvas() {
  const {
    blocks, selectedBlockId, setSelected, scenarios, showGrid,
    previewMode, pages, page, contentLocale, zoom, interactionMode
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

  // Theme check - we could read theme from book settings if available
  const { book } = useBookStudioStore();
  const defaultBg = '#fff9e9';
  const bgColor = (book?.settings as any)?.backgroundColor || defaultBg;

  return (
    <div className={`flex gap-2 ${previewMode === 'two-page' ? '' : ''}`} style={{ transform: `scale(${zoom})`, transformOrigin: 'center top' }}>
      <div
        className={`relative mx-auto overflow-hidden text-slate-950 shadow-2xl ${previewMode === 'two-page' ? 'ring-4 ring-violet-500 ring-offset-4 ring-offset-[#090d16]' : ''}`}
        style={{ width: 590, height: 760, backgroundColor: bgColor }}
        onPointerDown={e => {
          if (e.target === e.currentTarget || (e.target as HTMLElement).getAttribute('aria-label') === 'Book page canvas') {
            setSelected('');
          }
        }}
        aria-label="Book page canvas"
      >
        <div className="pointer-events-none absolute bottom-4 left-0 right-0 z-0 text-center font-serif text-xs text-slate-400">
          {pages.findIndex(p => p.id === page?.id) + 1}
        </div>
        {showGrid && (
          <div
            className="pointer-events-none absolute inset-0 z-0 opacity-10"
            style={{
              backgroundImage: 'linear-gradient(rgba(0,0,0,.1) 1px, transparent 1px), linear-gradient(90deg, rgba(0,0,0,.1) 1px, transparent 1px)',
              backgroundSize: '20px 20px',
            }}
          />
        )}
        {sortedBlocks.length === 0 && !previewMode && (
          <div className="pointer-events-none absolute inset-0 z-0 flex flex-col items-center justify-center text-sm text-slate-400/50">
            <p>Drag elements here</p>
            <p className="mt-1 text-xs">or use the left panel to add content</p>
          </div>
        )}
        
        {/* Render Editable Blocks */}
        {sortedBlocks.filter(b => b.visible !== false).map(block => (
          <BookBlockFrame
            key={block.id}
            block={block}
            selected={block.id === selectedBlockId}
            onSelect={setSelected}
          >
            <BookBlockRenderer 
              block={block} 
              scenarioLabel={block.scenarioId ? scenarioName(block.scenarioId) : undefined}
              interactive={false}
              isEditingText={block.id === selectedBlockId && interactionMode === 'text-edit'}
              onTextChange={(html) => useBookStudioStore.getState().patchBlock(block.id, { text: html })}
              onInteract={block.scenarioId ? () => handleInteract(block.scenarioId!) : undefined}
            />
          </BookBlockFrame>
        ))}

        {selectedBlockId && <BookAlignmentGuides />}
        {selectedBlockId && <BookSelectionOverlay />}
      </div>
      
      {/* Two-page Preview */}
      {previewMode === 'two-page' && (
        <div
          className="relative mx-auto overflow-hidden text-slate-950 shadow-2xl opacity-80"
          style={{ width: 590, height: 760, backgroundColor: bgColor }}
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
