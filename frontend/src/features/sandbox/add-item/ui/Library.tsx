import { useState, useEffect, useRef } from "react";
import { useLocale, useTranslations } from "next-intl";
import Image from "next/image";
import { Beaker, Sparkles, FlaskRound, FlaskConical, TestTube2, Pipette, Snowflake, Circle, Filter, Waves, Minus } from "lucide-react";
import { EquipmentThumbnail, hasEquipmentRenderer } from "@/entities/equipment/ui/EquipmentRendererRegistry";
import { catalogCache } from "@/shared/lib/catalogCache";
import { normalizeMaterial } from "@/entities/material/lib/MaterialNormalizer";
import type { EquipmentSummary, MaterialSummary } from "@/types";
import type {
  EquipmentType,
  MaterialState,
  Material,
  Item,
  LibraryItem,
  LibraryTab,
} from "@/widgets/sandbox/types";

export const liquids: Material[] = [
  { id: 'water', name: 'Water', formula: 'H₂O', color: 'rgba(34,211,238,0.5)', state: 'liquid' },
  { id: 'copper_sulfate', name: 'Copper(II) sulfate (aq)', formula: 'CuSO₄', color: 'rgba(59,130,246,0.85)', state: 'liquid' },
  { id: 'potassium_permanganate', name: 'Potassium permanganate (aq)', formula: 'KMnO₄', color: 'rgba(217,70,239,0.9)', state: 'liquid' },
];
export const compounds: Material[] = [];
export const elements: Material[] = [
  { id: 'sulfur', name: 'Sulfur', formula: 'S', color: '#FDE047', state: 'solid' },
  { id: 'iodine', name: 'Iodine (gas)', formula: 'I₂', color: 'rgba(168,85,247,0.7)', state: 'gas' },
];
export const featuredMaterials: Material[] = [
  { id: 'H2O', name: 'Water', formula: 'H₂O', color: 'rgba(34,211,238,0.5)', state: 'liquid', density: 1.0, molarMass: 18.015, meltingPointC: 0, boilingPointC: 100, description: 'Colorless liquid', safetyFlags: [], compatibleOperations: ['heating', 'mixing', 'cooling'] },
  { id: 'H2SO4', name: 'Sulfuric acid', formula: 'H₂SO₄', color: '#F59E0B', state: 'liquid', density: 1.84, molarMass: 98.079, meltingPointC: 10, boilingPointC: 337, description: 'Concentrated sulfuric acid', safetyFlags: ['corrosive'], compatibleOperations: ['heating', 'mixing'] },
  { id: 'ethanol', name: 'Ethanol', formula: 'C₂H₅OH', color: '#60A5FA', state: 'liquid', density: 0.789, molarMass: 46.07, meltingPointC: -114.1, boilingPointC: 78.37, description: 'Flammable alcohol', safetyFlags: ['flammable'], compatibleOperations: ['heating', 'mixing'] },
  { id: 'HCl', name: 'Hydrochloric acid', formula: 'HCl', color: '#A7F3D0', state: 'liquid', density: 1.18, molarMass: 36.46, meltingPointC: -27.32, boilingPointC: 48, description: 'Aqueous hydrogen chloride', safetyFlags: ['corrosive', 'toxic'], compatibleOperations: ['mixing'] },
  { id: 'H2O2', name: 'Hydrogen peroxide', formula: 'H₂O₂', color: '#BAE6FD', state: 'liquid', density: 1.11, molarMass: 34.014, meltingPointC: -0.43, boilingPointC: 150.2, description: 'Strong oxidizer', safetyFlags: ['oxidizer'], compatibleOperations: ['mixing'] },
  { id: 'CuSO4', name: 'Copper sulfate', formula: 'CuSO₄', color: '#3B82F6', state: 'solid', density: 2.28, molarMass: 159.6, meltingPointC: 110, boilingPointC: 560, description: 'Blue crystals', safetyFlags: ['toxic', 'irritant'], compatibleOperations: ['mixing', 'heating'] },
  { id: 'CuSO4(aq)', name: 'Copper sulfate solution', formula: 'CuSO₄(aq)', color: '#3B82F6', state: 'aqueous', density: 1.05, molarMass: 159.6, meltingPointC: 0, boilingPointC: 100, description: 'Blue solution', safetyFlags: ['toxic'], compatibleOperations: ['mixing'] },
  { id: 'NaOH', name: 'Sodium hydroxide', formula: 'NaOH', color: '#E2E8F0', state: 'solid', density: 2.13, molarMass: 39.997, meltingPointC: 318, boilingPointC: 1388, description: 'Caustic soda pellets', safetyFlags: ['corrosive'], compatibleOperations: ['mixing'] },
  { id: 'NaCl', name: 'Sodium chloride', formula: 'NaCl', color: '#CBD5E1', state: 'solid', density: 2.16, molarMass: 58.44, meltingPointC: 801, boilingPointC: 1413, description: 'Common salt', safetyFlags: [], compatibleOperations: ['mixing'] },
  { id: 'KMnO4', name: 'Potassium permanganate', formula: 'KMnO₄', color: '#331046', state: 'solid', density: 2.7, molarMass: 158.03, meltingPointC: 240, boilingPointC: 240, description: 'Purple crystals', safetyFlags: ['oxidizer', 'harmful'], compatibleOperations: ['mixing'] },
  { id: 'KMnO4(aq)', name: 'Potassium permanganate (aq)', formula: 'KMnO₄(aq)', color: '#D946EF', state: 'aqueous', density: 1.02, molarMass: 158.03, meltingPointC: 0, boilingPointC: 100, description: 'Purple solution', safetyFlags: ['oxidizer'], compatibleOperations: ['mixing'] },
  { id: 'Na2CO3', name: 'Sodium carbonate', formula: 'Na₂CO₃', color: '#F8FAFC', state: 'solid', density: 2.54, molarMass: 105.9888, meltingPointC: 851, boilingPointC: 1600, description: 'Soda ash', safetyFlags: ['irritant'], compatibleOperations: ['mixing'] },
  { id: 'ph_indicator', name: 'pH indicator', formula: 'pH', color: '#A855F7', state: 'aqueous', density: 1.0, molarMass: 100, meltingPointC: 0, boilingPointC: 100, description: 'Universal pH indicator', safetyFlags: [], compatibleOperations: ['mixing'] },
  { id: 'Zn', name: 'Zinc', formula: 'Zn', color: '#94A3B8', state: 'solid', density: 7.14, molarMass: 65.38, meltingPointC: 419.5, boilingPointC: 907, description: 'Zinc metal', safetyFlags: [], compatibleOperations: ['mixing', 'heating'] },
  { id: 'Cu', name: 'Copper', formula: 'Cu', color: '#B87333', state: 'solid', density: 8.96, molarMass: 63.546, meltingPointC: 1084.62, boilingPointC: 2562, description: 'Copper metal', safetyFlags: [], compatibleOperations: ['mixing', 'heating'] },
  { id: 'Au', name: 'Gold', formula: 'Au', color: '#F5C542', state: 'solid', density: 19.32, molarMass: 196.967, meltingPointC: 1064.18, boilingPointC: 2970, description: 'Gold metal', safetyFlags: [], compatibleOperations: ['mixing', 'heating'] },
  { id: 'sulfur', name: 'Sulfur', formula: 'S', color: '#FDE047', state: 'solid', density: 2.07, molarMass: 32.06, meltingPointC: 115.21, boilingPointC: 444.6, description: 'Yellow elemental sulfur', safetyFlags: ['irritant'], compatibleOperations: ['heating', 'mixing'] },
];
const featuredEquipment = new Set<EquipmentType>([
  'beaker', 'erlenmeyer', 'roundflask', 'testtube', 'funnel', 'separatory_funnel',
  'volumetric_flask', 'graduated_cylinder', 'burette', 'pipette', 'condenser',
  'petridish', 'watchglass', 'burner', 'hotplate', 'thermometer', 'phmeter',
  'scales', 'clampstand', 'stand',
]);
const formulaKey = (formula: string) => formula.replace(/[₀-₉]/g, (digit) => String('₀₁₂₃₄₅₆₇₈₉'.indexOf(digit))).replace(/\s/g, '').toUpperCase();
export const groups: { title: string; items: LibraryItem[] }[] = [
  {
    title: 'Основное',
    items: [
      { id: 'beaker', name: 'Beaker 250ml', w: 80, h: 100, icon: Beaker },
      { id: 'erlenmeyer', name: 'Erlenmeyer Flask', w: 80, h: 100, icon: Beaker },
      { id: 'roundflask', name: 'Round-Bottom Flask', w: 120, h: 128, icon: FlaskRound },
      { id: 'testtube', name: 'Test Tube', w: 30, h: 120, icon: Beaker },
      { id: 'graduated_cylinder', name: 'Graduated Cylinder', w: 60, h: 160, icon: TestTube2 },
      { id: 'separatory_funnel', name: 'Separatory Funnel', w: 70, h: 140, icon: Beaker },
      { id: 'volumetric_flask', name: 'Volumetric Flask', w: 100, h: 148, icon: FlaskConical },
      { id: 'burette', name: 'Burette', w: 50, h: 180, icon: Pipette },
      { id: 'pipette', name: 'Pipette', w: 68, h: 150, icon: Pipette },
      { id: 'petridish', name: 'Petri Dish', w: 110, h: 55, icon: Circle },
      { id: 'watchglass', name: 'Watch Glass', w: 90, h: 40, icon: Circle },
      { id: 'funnel', name: 'Funnel', w: 90, h: 100, icon: Filter },
    ]
  },
  {
    title: 'Нагрев',
    items: [
      { id: 'hotplate', name: 'Hotplate', w: 120, h: 60, icon: Beaker },
      { id: 'burner', name: 'Bunsen Burner', w: 60, h: 100, icon: Beaker },
      { id: 'icebath', name: 'Ice Bath', w: 160, h: 100, icon: Snowflake },
    ]
  },
  {
    title: 'Соединения',
    items: [
      { id: 'thermometer', name: 'Thermometer', w: 44, h: 150, icon: TestTube2 },
      { id: 'phmeter', name: 'pH Meter', w: 72, h: 128, icon: Waves },
      { id: 'stand', name: 'Ring Stand', w: 100, h: 160, icon: Minus },
      { id: 'condenser', name: 'Condenser', w: 180, h: 100, icon: FlaskConical },
    ]
  }
];

const equipmentDescription = (id: string) => ({
  beaker: 'Для смешивания и нагрева жидкостей',
  erlenmeyer: 'Для смешивания и проведения реакций',
  funnel: 'Для переливания жидкостей',
  thermometer: 'Измеряет температуру в точке установки',
  hotplate: 'Обеспечивает нагрев сосуда',
  burner: 'Источник направленного нагрева',
  testtube: 'Для малых объёмов и проб',
  graduated_cylinder: 'Точный отсчёт объёма жидкости',
}[id] ?? 'Лабораторное оборудование');

const scenarioFallbacks: Record<string, Record<'ru' | 'uz' | 'en', { name: string; desc: string }>> = {
  water_intro: {
    ru: { name: 'Вода в сосуде', desc: 'Добавьте воду в стакан и проверьте объём.' },
    uz: { name: 'Idishdagi suv', desc: 'Stakanga suv qo\'shing va hajmini tekshiring.' },
    en: { name: 'Water in a vessel', desc: 'Add water to a beaker and check its volume.' },
  },
  measure_water: {
    ru: { name: 'Измерение температуры', desc: 'Подсоедините термометр и снимите показание.' },
    uz: { name: 'Haroratni o\'lchash', desc: 'Termometrni ulang va ko\'rsatkichni o\'qing.' },
    en: { name: 'Measure temperature', desc: 'Connect a thermometer and take a reading.' },
  },
  heat_water: {
    ru: { name: 'Нагрев воды', desc: 'Подключите нагреватель и безопасно нагрейте воду.' },
    uz: { name: 'Suvni qizdirish', desc: 'Qizdirgichni ulab, suvni xavfsiz qizdiring.' },
    en: { name: 'Heat water', desc: 'Connect a heater and heat the water safely.' },
  },
  transfer_water: {
    ru: { name: 'Переливание воды', desc: 'Соедините два сосуда и перенесите жидкость.' },
    uz: { name: 'Suvni quyish', desc: 'Ikki idishni ulang va suyuqlikni o\'tkazing.' },
    en: { name: 'Transfer water', desc: 'Connect two vessels and move the liquid.' },
  },
  cuso4: {
    ru: { name: 'Раствор CuSO₄', desc: 'Растворите медный купорос в воде.' },
    uz: { name: 'CuSO₄ eritmasi', desc: 'Mis sulfatni suvda eritib ko\'ring.' },
    en: { name: 'CuSO₄ solution', desc: 'Dissolve copper sulfate in water.' },
  },
  kmno4: {
    ru: { name: 'Разбавление KMnO₄', desc: 'Разбавьте раствор перманганата калия водой.' },
    uz: { name: 'KMnO₄ suyultirish', desc: 'Kaliy permanganatni suv bilan suyultiring.' },
    en: { name: 'Dilute KMnO₄', desc: 'Dilute potassium permanganate with water.' },
  },
  hcl_naoh: {
    ru: { name: 'Нейтрализация HCl + NaOH', desc: 'Смешайте кислоту и щёлочь с образованием соли и воды.' },
    uz: { name: 'HCl + NaOH neytrallanishi', desc: 'Kislota va ishqorni aralashtirib, tuz va suv oling.' },
    en: { name: 'HCl + NaOH neutralization', desc: 'Mix acid and base to form salt and water.' },
  },
  zn_hcl: {
    ru: { name: 'Реакция Zn + HCl', desc: 'Проведите реакцию цинка с выделением водорода.' },
    uz: { name: 'Zn + HCl reaksiyasi', desc: 'Ruxni kislota bilan reaksiyaga kiritib, vodorodni kuzating.' },
    en: { name: 'Zn + HCl reaction', desc: 'React zinc with acid and observe hydrogen gas.' },
  },
  sulfur_heat: {
    ru: { name: 'Плавление серы', desc: 'Нагрейте серу выше 115 °C и наблюдайте смену фазы.' },
    uz: { name: 'Oltingugurtni eritish', desc: 'Oltingugurtni 115 °C dan yuqorida qizdiring.' },
    en: { name: 'Melt sulfur', desc: 'Heat sulfur above 115 °C and observe its phase change.' },
  },
  distillation: {
    ru: { name: 'Простая дистилляция', desc: 'Соберите установку из сосудов, нагревателя и конденсера.' },
    uz: { name: 'Oddiy distillatsiya', desc: 'Idishlar, qizdirgich va kondensordan qurilma yig\'ing.' },
    en: { name: 'Simple distillation', desc: 'Build a setup with vessels, a heater and a condenser.' },
  },
};

const equipmentTranslationKey = (id: string, field: 'name' | 'desc') => {
  const aliases: Record<string, string> = {
    petridish: 'petri_dish',
    watchglass: 'watch_glass',
    stand: 'stand',
  };
  return `equip.${aliases[id] ?? id}.${field}`;
};

function MaterialGlyph({ material }: { material: Material }) {
  const safeId = material.id.replace(/[^a-z0-9]/gi, '-');
  const isWater = material.id === 'H2O';
  const isMetal = ['Zn', 'Cu', 'Au'].includes(material.id);
  const isSulfur = material.id === 'sulfur';
  const isSolution = material.state === 'liquid' || material.state === 'aqueous';
  return (
    <svg width="46" height="46" viewBox="0 0 64 64" aria-hidden="true" className="drop-shadow-[0_3px_3px_rgba(15,23,42,.28)]">
      <defs>
        <linearGradient id={`material-fill-${safeId}`} x1="0" y1="0" x2="1" y2="1">
          <stop stopColor={isWater ? '#7dd3fc' : material.color} />
          <stop offset="1" stopColor={isWater ? '#2563eb' : material.color} stopOpacity=".72" />
        </linearGradient>
        <linearGradient id={`material-metal-${safeId}`} x1="0" y1="0" x2="1" y2="1">
          <stop stopColor="#f8fafc" /><stop offset=".35" stopColor={material.color} /><stop offset="1" stopColor="#475569" />
        </linearGradient>
      </defs>
      {isWater ? <>
        <path d="M32 7C27 16 16 25 16 38a16 16 0 0 0 32 0C48 25 37 16 32 7Z" fill={`url(#material-fill-${safeId})`} stroke="#dbeafe" strokeWidth="3" />
        <path d="M25 27c-3 4-4 7-4 11" fill="none" stroke="white" strokeWidth="4" strokeLinecap="round" opacity=".82" />
        <circle cx="38" cy="43" r="2.5" fill="#eff6ff" opacity=".85" />
      </> : isMetal ? <>
        <path d="m11 27 10-14 31 7 2 19-13 14-30-7Z" fill={`url(#material-metal-${safeId})`} stroke="#e2e8f0" strokeWidth="2.5" strokeLinejoin="round" />
        <path d="m21 13 31 7-13 13-28-6Z" fill={material.color} opacity=".72" />
        <path d="m21 17 22 5" stroke="white" strokeWidth="3" strokeLinecap="round" opacity=".7" />
      </> : isSulfur ? <>
        <path d="M14 40c-2-9 5-16 12-15 4-9 17-8 20 1 9 1 10 13 4 18 1 10-10 14-17 9-8 6-19 1-19-8Z" fill="#fde047" stroke="#fff7ad" strokeWidth="2.5" />
        <circle cx="26" cy="36" r="4" fill="#fff7ad" opacity=".9" /><circle cx="39" cy="43" r="3" fill="#eab308" opacity=".8" />
      </> : isSolution ? <>
        <path d="M21 10h22M25 10v12L15 47c-2 5 2 8 7 8h20c5 0 9-3 7-8L39 22V10" fill="#e0f2fe" fillOpacity=".3" stroke="#e2e8f0" strokeWidth="2.5" strokeLinejoin="round" />
        <path d="M18 39c8-4 20 3 30-1l2 9c1 5-3 7-8 7H24c-5 0-8-3-6-8Z" fill={`url(#material-fill-${safeId})`} stroke="#f8fafc" strokeWidth="1.5" />
        <path d="M25 14v17" stroke="white" strokeWidth="3" strokeLinecap="round" opacity=".8" />
      </> : <>
        <path d="m13 39 7-17 14-9 17 12-4 18-17 9Z" fill={`url(#material-fill-${safeId})`} stroke="#f8fafc" strokeWidth="2.5" strokeLinejoin="round" />
        <path d="m20 22 14 10 17-9M34 32v20" fill="none" stroke="white" strokeWidth="2" opacity=".65" />
      </>}
    </svg>
  );
}

type MaterialCopy = { name: string; description: string };
const materialPresentation = (material: Material, locale: 'ru' | 'uz' | 'en'): MaterialCopy => {
  const copy: Record<string, Record<'ru' | 'uz' | 'en', MaterialCopy>> = {
    H2O: { ru: { name: 'Вода', description: 'Бесцветная жидкость' }, en: { name: 'Water', description: 'Colorless liquid' }, uz: { name: 'Suv', description: 'Rangsiz suyuqlik' } },
    H2SO4: { ru: { name: 'Серная кислота', description: 'Концентрированная' }, en: { name: 'Sulfuric acid', description: 'Concentrated' }, uz: { name: 'Oltingugurt kislotasi', description: 'Konsentrlangan' } },
    ethanol: { ru: { name: 'Этанол', description: 'Бесцветная жидкость' }, en: { name: 'Ethanol', description: 'Colorless liquid' }, uz: { name: 'Etanol', description: 'Rangsiz suyuqlik' } },
    HCl: { ru: { name: 'Соляная кислота', description: 'Бесцветная жидкость' }, en: { name: 'Hydrochloric acid', description: 'Colorless liquid' }, uz: { name: 'Xlorid kislota', description: 'Rangsiz suyuqlik' } },
    H2O2: { ru: { name: 'Перекись водорода', description: 'Бесцветная жидкость' }, en: { name: 'Hydrogen peroxide', description: 'Colorless liquid' }, uz: { name: 'Vodorod peroksidi', description: 'Rangsiz suyuqlik' } },
    'CuSO4(aq)': { ru: { name: 'Раствор медного купороса', description: 'Синий раствор' }, en: { name: 'Copper sulfate solution', description: 'Blue solution' }, uz: { name: 'Mis kuporosi eritmasi', description: 'Ko‘k eritma' } },
    'KMnO4(aq)': { ru: { name: 'Раствор перманганата', description: 'Фиолетовый раствор' }, en: { name: 'Permanganate solution', description: 'Purple solution' }, uz: { name: 'Permanganat eritmasi', description: 'Binafsha eritma' } },
    CuSO4: { ru: { name: 'Медный купорос', description: 'Синие кристаллы' }, en: { name: 'Copper sulfate', description: 'Blue crystals' }, uz: { name: 'Mis kuporosi', description: 'Ko‘k kristallar' } },
    NaOH: { ru: { name: 'Натрий гидроксид', description: 'Белые кристаллы' }, en: { name: 'Sodium hydroxide', description: 'White crystals' }, uz: { name: 'Natriy gidroksid', description: 'Oq kristallar' } },
    NaCl: { ru: { name: 'Натрий хлорид', description: 'Белые кристаллы' }, en: { name: 'Sodium chloride', description: 'White crystals' }, uz: { name: 'Natriy xlorid', description: 'Oq kristallar' } },
    KMnO4: { ru: { name: 'Перманганат калия', description: 'Фиолетовые кристаллы' }, en: { name: 'Potassium permanganate', description: 'Purple crystals' }, uz: { name: 'Kaliy permanganat', description: 'Binafsha kristallar' } },
    Na2CO3: { ru: { name: 'Карбонат натрия', description: 'Белые кристаллы' }, en: { name: 'Sodium carbonate', description: 'White crystals' }, uz: { name: 'Natriy karbonat', description: 'Oq kristallar' } },
    ph_indicator: { ru: { name: 'Индикатор pH', description: 'Раствор-индикатор' }, en: { name: 'pH indicator', description: 'Indicator solution' }, uz: { name: 'pH indikatori', description: 'Indikator eritmasi' } },
    Zn: { ru: { name: 'Цинк', description: 'Серый металл' }, en: { name: 'Zinc', description: 'Gray metal' }, uz: { name: 'Rux', description: 'Kulrang metall' } },
    Cu: { ru: { name: 'Медь', description: 'Металл' }, en: { name: 'Copper', description: 'Metal' }, uz: { name: 'Mis', description: 'Metall' } },
    Au: { ru: { name: 'Золото', description: 'Металл' }, en: { name: 'Gold', description: 'Metal' }, uz: { name: 'Oltin', description: 'Metall' } },
    sulfur: { ru: { name: 'Сера', description: 'Желтый порошок' }, en: { name: 'Sulfur', description: 'Yellow powder' }, uz: { name: 'Oltingugurt', description: 'Sariq kukun' } },
  };
  return copy[material.id]?.[locale] ?? { name: material.name, description: material.description ?? (material.state === 'solid' ? 'Solid substance' : material.state === 'aqueous' ? 'Solution' : 'Liquid') };
};

export const materialFromSummary = (material: MaterialSummary): Material => {
  const normalized = normalizeMaterial(material);
  let color = normalized.state === "gas" ? "#A78BFA" : normalized.state === "solid" ? "#E2E8F0" : "#22D3EE";
  
  const formula = material.formula || "";
  const name = (material.name || "").toLowerCase();
  
  if (formula.includes("Cu") && !formula.includes("Cu2")) color = "#3B82F6";
  else if ((formula.includes("Fe") && formula.includes("Cl3")) || formula.includes("Fe2(SO4)3")) color = "#D97706";
  else if (formula.includes("KMnO4") || name.includes("permanganate")) color = "#D946EF";
  else if (formula === "S" || name.includes("sulfur")) color = "#FDE047";
  else if (formula === "I2" || name.includes("iodine")) color = "#A855F7";
  else if (formula === "Cl2" || name.includes("chlorine")) color = "#84CC16";
  else if (formula.includes("Cr2O7") || name.includes("dichromate")) color = "#F97316";
  else if (formula.includes("Ni")) color = "#10B981";

  return {
    id: material.materialId,
    name: material.name,
    formula: material.formula,
    color,
    molarMass: material.molarMass,
    state: normalized.state as MaterialState,
  };
};
export const equipmentTypeFromProfile = (profile: EquipmentSummary): EquipmentType => {
  const value = `${profile.profileId} ${profile.type}`.toLowerCase();
  if (value.includes("hotplate") || value.includes("hot plate")) return "hotplate";
  if (value.includes("ph") && value.includes("meter")) return "phmeter";
  if (value.includes("analytical") || value.includes("balance") || value.includes("scales")) return "scales";
  if (value.includes("magnetic")) return "magnetic_stirrer";
  if (value.includes("round")) return "roundflask";
  if (value.includes("distill")) return "distillation_flask";
  if (value.includes("ice")) return "icebath";
  if (value.includes("burette")) return "burette";
  if (value.includes("pipette")) return "pipette";
  if (value.includes("petri")) return "petridish";
  if (value.includes("watch")) return "watchglass";
  if (value.includes("funnel") && !value.includes("separatory")) return "funnel";
  if (value.includes("separatory")) return "separatory_funnel";
  if (value.includes("clamp") || value.includes("stand")) return "clampstand";
  if (value.includes("crucible")) return "crucible";
  if (value.includes("burner")) return "burner";
  if (value.includes("beaker")) return "beaker";
  if (value.includes("erlenmeyer")) return "erlenmeyer";
  if (value.includes("test") && value.includes("tube")) return "testtube";
  if (value.includes("thermometer")) return "thermometer";
  if (value.includes("condenser")) return "condenser";
  if (value.includes("graduated")) return "graduated_cylinder";
  if (value.includes("volumetric")) return "volumetric_flask";
  return "unsupported";
};

export function LegacyLibrary({
  tab,
  setTab,
  addItem,
  addMaterial,
  selected,
}: {
  tab: "equipment" | "liquids" | "compounds" | "elements";
  setTab: (tab: "equipment" | "liquids" | "compounds" | "elements") => void;
  addItem: (item: LibraryItem) => void;
  addMaterial: (material: Material) => void;
  selected?: Item;
}) {
  const list =
    tab === "liquids" ? liquids : tab === "compounds" ? compounds : elements;
  return (
    <div className="flex min-h-0 flex-1 flex-col">
      <div className="grid grid-cols-4 gap-1 border-b border-[var(--border)] p-2">
        <button
          className={`min-h-11 rounded-lg text-[10px] font-semibold ${tab === "equipment" ? "bg-[var(--primary)] text-white" : "hover:bg-[var(--accent)]"}`}
          onClick={() => setTab("equipment")}
        >
          Equipment
        </button>
        <button
          className={`min-h-11 rounded-lg text-[10px] font-semibold ${tab === "liquids" ? "bg-[var(--primary)] text-white" : "hover:bg-[var(--accent)]"}`}
          onClick={() => setTab("liquids")}
        >
          Materials
        </button>
        <button
          className={`min-h-11 rounded-lg text-[10px] font-semibold ${tab === "compounds" ? "bg-[var(--primary)] text-white" : "hover:bg-[var(--accent)]"}`}
          onClick={() => setTab("compounds")}
        >
          Compounds
        </button>
        <button
          className={`min-h-11 rounded-lg text-[10px] font-semibold ${tab === "elements" ? "bg-[var(--primary)] text-white" : "hover:bg-[var(--accent)]"}`}
          onClick={() => setTab("elements")}
        >
          Elements
        </button>
      </div>
      <div className="flex-1 overflow-y-auto p-4">
        {tab === "equipment" ? (
          groups.map((group) => (
            <section key={group.title} className="mb-5">
              <h3 className="mb-3 text-xs font-bold uppercase tracking-wider text-[var(--muted-foreground)]">
                {group.title}
              </h3>
              <div className="grid grid-cols-2 gap-3">
                {group.items.map((item) => (
                  <button
                    key={item.id}
                    className="min-h-24 rounded-xl border border-border/50 bg-[rgba(255,255,255,0.02)] p-3 text-center transition-all hover:-translate-y-0.5 hover:border-[var(--primary-bright)] hover:shadow-[0_0_15px_rgba(139,92,246,0.3)]"
                    onClick={() => addItem(item)}
                  >
                    <item.icon
                      size={24}
                      className="mx-auto mb-2 text-[var(--primary)]"
                    />
                    <span className="block text-xs font-medium">
                      {item.name}
                    </span>
                    <span className="mt-1 block text-[10px] font-bold uppercase text-[var(--primary)]">
                      Add
                    </span>
                  </button>
                ))}
              </div>
            </section>
          ))
        ) : (
          <div className="space-y-3">
            {list.map((material) => (
              <button
                key={material.id}
                onClick={() => addMaterial(material)}
                className="group relative flex min-h-16 w-full items-center gap-3 overflow-hidden rounded-xl border border-border/50 bg-card/50 p-3 text-left transition-all hover:-translate-y-0.5 hover:border-[var(--primary-bright)] hover:shadow-[0_0_15px_rgba(139,92,246,0.3)]"
              >
                <div className="absolute inset-0 opacity-[0.03] transition-opacity group-hover:opacity-[0.08]" style={{ backgroundColor: material.color }} />
                <span
                  className="relative grid h-10 w-10 shrink-0 place-items-center rounded-xl border border-border text-[10px] font-extrabold tracking-wider shadow-sm"
                  style={{ color: material.color, backgroundColor: 'rgba(0,0,0,0.4)' }}
                >
                  {material.state === "gas"
                    ? "GAS"
                    : material.state === "solid"
                      ? "SOL"
                      : material.state === "aqueous"
                        ? "AQ"
                      : "LIQ"}
                </span>
                <span className="relative min-w-0">
                  <span className="block truncate text-sm font-semibold leading-tight text-foreground/90 group-hover:text-foreground">
                    {material.name}
                  </span>
                  <span className="mt-0.5 block truncate text-[11px] font-medium" style={{ color: material.color }}>
                    <span className="font-mono text-muted-foreground">{material.formula}</span> <span className="opacity-50">·</span> {material.state}
                  </span>
                </span>
              </button>
            ))}
            {!selected && (
              <p className="mt-4 text-xs text-[var(--muted-foreground)]">
                Select a vessel before adding a sample.
              </p>
            )}
          </div>
        )}
      </div>
    </div>
  );
}

export interface LibraryProps {
  tab: LibraryTab;
  setTab: (tab: LibraryTab) => void;
  addItem: (item: LibraryItem) => void;
  addMaterial: (material: Material) => void;
  selected?: Item;
  onAskAi?: () => void;
  onStartScenario?: (id: string) => void;
  helpActive?: boolean;
  helpTab?: LibraryTab;
  helpTargets?: string[];
  levelMode?: boolean;
  allowedEquipment?: EquipmentType[];
  allowedMaterials?: string[];
  levelLabel?: string;
}

function ScenarioCardGlyph({ accent }: { accent: string }) {
  return (
    <svg viewBox="0 0 96 64" className="h-full w-full" aria-hidden="true">
      <path d="M4 48h18l8-10h14l8-12h18l10-10" fill="none" stroke={accent} strokeWidth="1.5" strokeLinecap="round" strokeDasharray="3 4" opacity=".8" />
      <circle cx="22" cy="48" r="3" fill={accent} />
      <circle cx="52" cy="26" r="3" fill={accent} opacity=".85" />
      <circle cx="82" cy="16" r="3" fill={accent} opacity=".7" />
      <path d="M44 9v14l-8 19c-1 3 1 6 5 6h14c4 0 6-3 5-6l-8-19V9" fill="rgba(255,255,255,.05)" stroke={accent} strokeWidth="1.5" />
      <path d="M38 38h20l2 4c1 3-1 6-5 6H41c-4 0-6-3-5-6l2-4Z" fill={accent} opacity=".28" />
      <path d="M41 9h10" stroke={accent} strokeWidth="1.5" strokeLinecap="round" />
    </svg>
  );
}

export function Library({
  tab,
  setTab,
  addItem,
  addMaterial,
  selected,
  onAskAi,
  onStartScenario,
  helpActive,
  helpTab,
  helpTargets = [],
  levelMode = false,
  allowedEquipment,
  allowedMaterials,
  levelLabel,
}: LibraryProps) {
  const ts = useTranslations("sandbox");
  const locale = useLocale() as 'ru' | 'uz' | 'en';
  const [query, setQuery] = useState("");
  const libraryScrollRef = useRef<HTMLDivElement>(null);
  const [remoteMaterials, setRemoteMaterials] = useState<MaterialSummary[]>([]);
  useEffect(() => {
    let cancelled = false;
    void catalogCache.getMaterials()
      .then((materials) => {
        if (cancelled) return;
        setRemoteMaterials(materials);
      })
      .catch(() => undefined);
    return () => { cancelled = true; };
  }, []);
  const [open, setOpen] = useState<Record<string, boolean>>({
    Основное: true,
    Нагрев: true,
    Соединения: true,
  });
  const normalized = query.trim().toLowerCase();
  const equipmentCopy = (item: LibraryItem, field: 'name' | 'desc') => {
    const key = equipmentTranslationKey(item.id, field);
    return ts.has(key) ? ts(key) : field === 'name' ? item.name : equipmentDescription(item.id);
  };
  const scenarioCopy = (id: string, field: 'name' | 'desc') => {
    const key = `scenarios.${id}.${field}`;
    if (ts.has(key)) return ts(key);
    return scenarioFallbacks[id]?.[locale]?.[field] ?? scenarioFallbacks[id]?.en?.[field] ?? key;
  };
  const scenarioLaunchLabel = ts.has('scenarios.launch') ? ts('scenarios.launch') : ({ ru: 'Запуск', uz: 'Boshlash', en: 'Launch' }[locale] ?? 'Launch');
  const groupTitle = (title: string) => ({
    'Основное': ts('groupContainers'),
    'Нагрев': ts('groupHeating'),
    'Соединения': ts('groupMeasurement'),
  }[title] ?? title);
  const visibleGroups = [
    ...groups.map(group => ({
      ...group,
      items: group.items.filter(item => featuredEquipment.has(item.id) && hasEquipmentRenderer(item.id) && (!allowedEquipment || allowedEquipment.includes(item.id)) && (!normalized || `${equipmentCopy(item, 'name')} ${equipmentCopy(item, 'desc')}`.toLowerCase().includes(normalized)))
    })).filter(group => group.items.length > 0),
  ];
  const remoteFeatured = remoteMaterials.map(materialFromSummary).filter((material) => featuredMaterials.some((featured) => formulaKey(featured.formula) === formulaKey(material.formula)));
  const allMaterials = [...featuredMaterials, ...remoteFeatured];
  const materials = allMaterials
    .filter(
      (material, index, list) =>
        list.findIndex(
          (candidate) =>
            candidate.id === material.id ||
            (candidate.formula && candidate.formula === material.formula),
        ) === index,
    )
    .filter(
      (material) =>
        (!allowedMaterials || allowedMaterials.includes(material.id)) && (!normalized ||
        `${materialPresentation(material, locale).name} ${materialPresentation(material, locale).description} ${material.formula}`
          .toLowerCase()
          .includes(normalized)),
    )
    .slice(0, featuredMaterials.length);
  const visibleMaterials = materials.filter((material) => !normalized || `${materialPresentation(material, locale).name} ${materialPresentation(material, locale).description} ${material.formula}`.toLowerCase().includes(normalized));
  const materialGroups = [
    { title: 'Жидкости', ids: new Set(['H2O', 'H2SO4', 'ethanol', 'HCl', 'H2O2', 'CuSO4(aq)', 'KMnO4(aq)', 'ph_indicator']) },
    { title: 'Соли', ids: new Set(['CuSO4', 'NaOH', 'NaCl', 'KMnO4', 'Na2CO3']) },
    { title: 'Металлы', ids: new Set(['Zn', 'Cu', 'Au']) },
    { title: 'Прочие', ids: new Set(['sulfur']) },
  ].map((group) => ({ ...group, items: visibleMaterials.filter((material) => group.ids.has(material.id) || (group.title === 'Прочие' && ![...new Set(['H2O', 'H2SO4', 'ethanol', 'HCl', 'H2O2', 'CuSO4(aq)', 'KMnO4(aq)', 'ph_indicator', 'CuSO4', 'NaOH', 'NaCl', 'KMnO4', 'Na2CO3', 'Zn', 'Cu'])].includes(material.id))) })).filter((group) => group.items.length > 0);
  const helpTargetId = helpTargets[0];
  const helpTargetGroupTitle = visibleGroups.find((group) => group.items.some((item) => item.id === helpTargetId))?.title;
  useEffect(() => {
    if (!helpActive || !helpTargetId) return;

    const scrollTimer = window.setTimeout(() => {
      if (helpTargetGroupTitle) {
        setOpen((current) => current[helpTargetGroupTitle] ? current : { ...current, [helpTargetGroupTitle]: true });
      }
      window.setTimeout(() => {
        const target = Array.from(libraryScrollRef.current?.querySelectorAll<HTMLElement>('[data-help-target]') ?? [])
          .find((node) => node.dataset.helpTarget === helpTargetId);
        target?.scrollIntoView({ behavior: 'smooth', block: 'center', inline: 'nearest' });
      }, 80);
    }, 120);

    return () => window.clearTimeout(scrollTimer);
  }, [helpActive, helpTargetGroupTitle, helpTargetId, tab]);

  const tabs: Array<{ id: LibraryTab; label: string }> = [
    { id: "equipment", label: ts("equipment") },
    { id: "materials", label: ts("materials") },
    ...(!levelMode ? [{ id: "scenarios" as LibraryTab, label: ts("scenarios.tabTitle") || "Scenarios" }] : []),
  ];
  return (
    <div className="flex min-h-0 flex-1 flex-col">
      <div className="border-b border-[var(--border)] p-2">
        {levelMode && levelLabel && <div className="mb-2 rounded-lg border border-[var(--primary)]/25 bg-[var(--primary)]/10 px-3 py-2 text-[11px] font-bold uppercase tracking-[.12em] text-[var(--primary-bright)]">{levelLabel}</div>}
        <div
          className="grid gap-1 rounded-xl border border-border bg-card/20 p-1 shadow-inner"
          style={{ gridTemplateColumns: `repeat(${tabs.length}, minmax(0, 1fr))` }}
          role="tablist"
        >
          {tabs.map((item) => (
            <button
              key={item.id}
              role="tab"
              aria-selected={tab === item.id}
              data-help-tab={item.id}
              className={`relative min-h-10 min-w-0 rounded-lg px-2 text-[13px] font-semibold transition-all ${tab === item.id ? "bg-[var(--primary)] text-white shadow-[0_4px_14px_rgba(139,92,246,.28)]" : "text-[var(--muted-foreground)] hover:bg-foreground/5 hover:text-foreground"} ${helpActive && helpTab === item.id ? 'help-arrow-target ring-2 ring-cyan-300 ring-offset-2 ring-offset-background shadow-[0_0_24px_rgba(34,211,238,.75)] animate-pulse' : ''}`}
              onClick={() => setTab(item.id)}
            >
              <span className="flex min-w-0 items-center justify-center gap-1.5">
                <span className="sm:hidden" aria-hidden="true">{item.id === 'equipment' ? '⚗' : item.id === 'materials' ? '◈' : '✦'}</span>
                <span className="hidden text-[9px] opacity-60 md:inline">{tabs.indexOf(item) + 1}</span>
                <span className="hidden truncate sm:inline">{item.label}</span>
              </span>
            </button>
          ))}
        </div>
        <label className="mt-2 block">
          <span className="sr-only">{ts.has('search') ? ts('search') : ts('searchMaterials')}</span>
          <input
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder={ts.has('search') ? ts('search') : ts('searchMaterials')}
            className="h-10 w-full rounded-xl border border-[var(--border)] bg-[var(--background)] px-3 text-[13px] outline-none focus:border-[var(--primary)]"
          />
        </label>
      </div>
      <div ref={libraryScrollRef} className="flex-1 overflow-y-auto p-3">
        {tab === "equipment" &&
          visibleGroups.map((group) => (
            <section key={group.title} className="mb-4">
              {!levelMode && <button
                className="mb-1 flex min-h-9 w-full items-center justify-between px-1 text-left text-[11px] font-bold tracking-[.12em] text-[var(--muted-foreground)]"
                onClick={() =>
                  setOpen((current) => ({
                    ...current,
                    [group.title]: !current[group.title],
                  }))
                }
              >
                <span>{groupTitle(group.title)}</span>
                <span aria-hidden="true">{open[group.title] ? "−" : "+"}</span>
              </button>}
              {(levelMode || open[group.title]) && (
                <div className="grid gap-2">
                  {group.items.map((item, index) => {
                    const isHelpTarget = helpActive && helpTargets.includes(item.id);
                    return (
                      <button
                        key={`${item.id}-${item.name}-${index}`}
                        title={equipmentCopy(item, 'name')}
                        data-help-target={item.id}
                        className={`group relative flex min-h-[64px] items-center gap-2.5 rounded-xl border border-border bg-card/95 p-2 text-left font-sans shadow-sm transition-all hover:-translate-y-0.5 hover:border-[var(--primary-bright)] hover:bg-accent hover:shadow-[0_0_15px_rgba(139,92,246,0.3)] ${
                          isHelpTarget ? "help-arrow-target ring-2 ring-violet-300 shadow-[0_0_20px_rgba(156,107,255,.55)] animate-pulse" : ""
                        }`}
                        onClick={() => addItem(item)}
                      >
                      <span className="equipment-thumbnail grid h-11 w-11 shrink-0 place-items-center overflow-hidden rounded-lg border border-border/70 bg-muted/55 p-0.5"><EquipmentThumbnail type={item.id} size={40} /></span>
                      <span className="min-w-0 pr-5">
                        <span className="block truncate text-[14px] font-semibold leading-[1.15] text-foreground/90">{equipmentCopy(item, 'name')}</span>
                        <span className="mt-1 block text-[11px] leading-[1.2] text-[var(--muted-foreground)]">{equipmentCopy(item, 'desc')}</span>
                      </span>
                      <div className="pointer-events-none absolute inset-0 flex items-center justify-center rounded-xl bg-black/40 opacity-0 transition-opacity backdrop-blur-[2px] group-hover:opacity-100">
                        <span className="grid h-7 w-7 place-items-center rounded-full border border-white/80 bg-[#2563eb] text-lg font-bold leading-none text-white shadow-[0_0_16px_rgba(37,99,235,.8)]">
                          +
                        </span>
                      </div>
                    </button>
                    );
                  })}
                </div>
              )}
            </section>
          ))}
        {tab === "materials" && (
          <div className="space-y-3">
          {materialGroups.map((group) => (
              <section key={group.title}>
                {!levelMode && <div className="mb-1 flex items-center justify-between px-1 text-[11px] font-bold uppercase tracking-[.12em] text-[var(--muted-foreground)]">
                  <span>{ts(`library.${group.title === 'Жидкости' ? 'liquids' : group.title === 'Соли' ? 'salts' : group.title === 'Металлы' ? 'metals' : 'others'}`)}</span><span aria-hidden="true">⌄</span>
                </div>}
                <div className="space-y-1.5">
                  {group.items.map((material) => (
                    <button key={material.id} data-help-target={material.id} onClick={() => addMaterial(material)} title={`${materialPresentation(material, locale).name}\n${material.formula}`} className={`group relative flex min-h-[76px] w-full items-center gap-3 overflow-visible rounded-xl border border-border bg-card/80 px-3 py-2 text-left transition hover:-translate-y-0.5 hover:border-[var(--primary-bright)] hover:bg-primary/10 hover:shadow-[0_0_15px_rgba(139,92,246,.22)] ${helpActive && helpTargets.includes(material.id) ? 'help-arrow-target ring-2 ring-violet-300 shadow-[0_0_20px_rgba(156,107,255,.55)] animate-pulse' : ''}`}>
                      <span className="material-glyph-container grid h-12 w-12 shrink-0 place-items-center rounded-xl border border-border bg-card" style={{ boxShadow: `inset 0 0 18px ${material.color}22` }}>
                        <MaterialGlyph material={material} />
                      </span>
                      <span className="min-w-0 flex-1">
                        <span className="block truncate text-[13px] font-bold text-foreground/90">{materialPresentation(material, locale).name}</span>
                        <span className="mt-0.5 block truncate text-[11px] text-[var(--muted-foreground)]"><span className="font-mono text-muted-foreground">{material.formula}</span></span>
                        <span className="mt-0.5 block truncate text-[10px] text-[var(--muted-foreground)]">{materialPresentation(material, locale).description}</span>
                      </span>
                      <span className="h-2.5 w-2.5 shrink-0 rounded-full border border-white/30 shadow-[0_0_8px_currentColor]" style={{ color: material.color, backgroundColor: material.color }} />
                    </button>
                  ))}
                </div>
              </section>
            ))}
            {!selected && (
              <p className="mt-4 text-xs text-[var(--muted-foreground)]">
                Select a vessel before adding a sample.
              </p>
            )}
          </div>
        )}
        {tab === "elements" && (
          <div className="grid grid-cols-2 gap-2">
            {materials
              .filter((item) => item.id.startsWith("ELEM-"))
              .map((element) => (
                <button
                  key={element.id}
                  onClick={() => addMaterial(element)}
                  className="rounded-xl border border-border/50 bg-[rgba(255,255,255,0.02)] p-3 text-left transition-all hover:-translate-y-0.5 hover:border-[var(--primary-bright)] hover:shadow-[0_0_15px_rgba(139,92,246,0.3)]"
                >
                  <span
                    className="text-xl font-bold"
                    style={{ color: element.color }}
                  >
                    {element.formula}
                  </span>
                  <span className="mt-1 block text-xs font-semibold">
                    {element.name}
                  </span>
                  <span className="text-[10px] text-[var(--muted-foreground)]">
                    Solid sample
                  </span>
                </button>
              ))}
          </div>
        )}
        {tab === "ai" && (
          <div className="rounded-2xl border border-[var(--primary)]/30 bg-[var(--primary)]/10 p-4">
            <Sparkles className="text-[var(--primary)]" />
            <h3 className="mt-3 font-semibold">AI Lab Assistant</h3>
            <p className="mt-2 text-xs leading-relaxed text-[var(--muted-foreground)]">
              Describe an experiment and the assistant will prepare equipment,
              materials and a safe action sequence.
            </p>
            <button
              className="mt-4 min-h-11 w-full rounded-xl bg-[var(--primary)] px-3 text-sm font-semibold text-foreground"
              onClick={() => onAskAi?.()}
            >
              Open assistant
            </button>
          </div>
        )}
        {tab === "scenarios" && (
          <div className="scenario-list space-y-3">
            {[
              { id: 'water_intro', image: '/water-droplet-transparent.png', accent: '#22d3ee' },
              { id: 'measure_water', image: '/icon-physics.png', accent: '#a78bfa' },
              { id: 'heat_water', image: '/icon-chemistry.png', accent: '#fb923c' },
              { id: 'transfer_water', image: '/mol-water.png', accent: '#c084fc' },
              { id: 'cuso4', image: '/material-icons/copper.png', accent: '#f97316' },
              { id: 'kmno4', image: '/material-icons/potassium-permanganate.png', accent: '#e879f9' },
              { id: 'hcl_naoh', image: '/icon-chemistry.png', accent: '#c4b5fd' },
              { id: 'zn_hcl', image: '/material-icons/zinc.png', accent: '#93c5fd' },
              { id: 'sulfur_heat', image: '/material-icons/sulfur.png', accent: '#fde047' },
              { id: 'distillation', image: '/chemistry.png', accent: '#67e8f9' },
            ].map((scenario) => (
              <button key={scenario.id} type="button" onClick={() => onStartScenario?.(scenario.id)} className="scenario-icon group relative flex min-h-[92px] w-full items-center gap-3 overflow-hidden rounded-2xl border border-white/10 bg-[#0b1018] p-3 text-left shadow-[0_8px_22px_rgba(2,6,23,.22),inset_0_1px_0_rgba(255,255,255,.05)] transition-all hover:-translate-y-0.5 hover:border-white/25 hover:shadow-[0_10px_28px_rgba(2,6,23,.4)]">
                <span aria-hidden="true" className="pointer-events-none absolute inset-y-0 right-0 w-32 opacity-25 transition-opacity duration-300 group-hover:opacity-55" style={{ background: `radial-gradient(circle at 75% 50%, ${scenario.accent}66, transparent 68%)` }} />
                <span aria-hidden="true" className="scenario-card-glyph pointer-events-none absolute right-1 top-1/2 h-16 w-24 -translate-y-1/2 opacity-25 transition-all duration-300 group-hover:scale-110 group-hover:opacity-70"><ScenarioCardGlyph accent={scenario.accent} /></span>
                <span className="scenario-thumb relative z-10 grid h-12 w-12 shrink-0 place-items-center overflow-hidden rounded-xl border border-white/15 bg-transparent p-1.5 shadow-inner">
                  <Image src={scenario.image} alt="" width={48} height={48} className="h-full w-full object-contain transition-transform duration-300 group-hover:scale-110" />
                </span>
                <span className="relative z-10 min-w-0 flex-1">
                  <span className="scenario-title block truncate text-[13px] font-semibold leading-tight text-foreground">{scenarioCopy(scenario.id, 'name')}</span>
                  <span className="scenario-description mt-1 block text-[11px] leading-[1.25] text-muted-foreground">{scenarioCopy(scenario.id, 'desc')}</span>
                </span>
                <span className="scenario-launch relative z-10 shrink-0 rounded-full border border-emerald-400/20 bg-[#064e3b] px-2 py-1 text-[9px] font-bold uppercase text-[#a7f3d0] shadow-sm">{scenarioLaunchLabel}</span>
              </button>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
