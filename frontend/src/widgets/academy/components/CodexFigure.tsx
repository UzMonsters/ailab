import { renderEquipmentCanvas } from "@/entities/equipment/ui/EquipmentRendererRegistry";

type CodexFigureProps = {
  alt: string;
  caption?: string;
  figureNumber?: number;
  rendererType?: string;
  align?: "left" | "center" | "right";
  variant?: "small" | "medium" | "large" | "hero" | "diagram";
  className?: string;
};

const dimensions = {
  small: [130, 150],
  medium: [210, 240],
  large: [320, 350],
  hero: [460, 500],
  diagram: [180, 140],
} as const;

export function CodexFigure({ alt, caption, figureNumber, rendererType, align = "center", variant = "medium", className = "" }: CodexFigureProps) {
  const [width, height] = dimensions[variant];
  return <figure className={`codex-figure codex-figure--${variant} codex-figure--${align} ${className}`} role="group" aria-label={alt}>
    <div className="codex-figure__art" role="img" aria-label={alt}>
      {rendererType ? renderEquipmentCanvas(rendererType, { type: rendererType, width, height, size: width, liquidLevel: .42, volumeMl: 210, capacityMl: 500, temperature: 20 }) : <span className="codex-figure__placeholder"/>}
    </div>
    {caption && <figcaption>{figureNumber !== undefined && <b>Рис. {figureNumber}.</b>} {caption}</figcaption>}
  </figure>;
}
