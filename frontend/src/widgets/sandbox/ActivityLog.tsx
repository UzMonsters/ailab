import { useTranslations } from "next-intl";
import type { Item } from "@/widgets/sandbox/types";

export function ActivityLog({ items, selected }: { items: Item[]; selected?: Item }) {
  const ts = useTranslations("sandbox");
  return (
    <section className="hidden min-h-[92px] border-t border-[var(--border)] bg-[var(--card)] px-4 py-3 lg:block">
      <h3 className="text-xs font-semibold uppercase tracking-wider">
        {ts("activityLog.title")}
      </h3>
      <div className="mt-3 grid grid-cols-4 gap-3 text-xs">
        <div>
          <span className="text-[var(--muted-foreground)]">{ts("activityLog.objects")}</span>
          <strong className="mt-1 block">{items.length}</strong>
        </div>
        <div>
          <span className="text-[var(--muted-foreground)]">{ts("activityLog.volume")}</span>
          <strong className="mt-1 block">{selected?.volumeMl || 0} mL</strong>
        </div>
        <div>
          <span className="text-[var(--muted-foreground)]">{ts("activityLog.temperature")}</span>
          <strong className="mt-1 block">
            {selected?.temperature.toFixed(1) || "24.5"} °C
          </strong>
        </div>
        <div>
          <span className="text-[var(--muted-foreground)]">{ts("activityLog.state")}</span>
          <strong className="mt-1 block capitalize">
            {selected?.operation || "idle"}
          </strong>
        </div>
      </div>
    </section>
  );
}
