import type { Item } from "@/widgets/sandbox/types";

export function ActivityLog({ items, selected }: { items: Item[]; selected?: Item }) {
  return (
    <section className="hidden min-h-[92px] border-t border-[var(--border)] bg-[var(--card)] px-4 py-3 lg:block">
      <h3 className="text-xs font-semibold uppercase tracking-wider">
        Experiment log
      </h3>
      <div className="mt-3 grid grid-cols-4 gap-3 text-xs">
        <div>
          <span className="text-[var(--muted-foreground)]">Objects</span>
          <strong className="mt-1 block">{items.length}</strong>
        </div>
        <div>
          <span className="text-[var(--muted-foreground)]">Volume</span>
          <strong className="mt-1 block">{selected?.volumeMl || 0} mL</strong>
        </div>
        <div>
          <span className="text-[var(--muted-foreground)]">Temperature</span>
          <strong className="mt-1 block">
            {selected?.temperature.toFixed(1) || "24.5"} °C
          </strong>
        </div>
        <div>
          <span className="text-[var(--muted-foreground)]">State</span>
          <strong className="mt-1 block capitalize">
            {selected?.operation || "idle"}
          </strong>
        </div>
      </div>
    </section>
  );
}
