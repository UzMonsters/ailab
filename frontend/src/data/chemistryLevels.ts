import type { EquipmentType } from "@/widgets/sandbox/types";

export type SupportedLocale = "ru" | "en" | "uz";
export type LocalizedText = Record<SupportedLocale, string>;

export type ChemistryLevelDefinition = {
  id: number;
  scenarioId: string;
  title: LocalizedText;
  objective: LocalizedText;
  learningPoints: LocalizedText[];
  duration: LocalizedText;
  allowedEquipment: EquipmentType[];
  allowedMaterials: string[];
};

export const chemistryLevels: ChemistryLevelDefinition[] = [
  {
    id: 1,
    scenarioId: "water_intro",
    title: { ru: "Первая капля", en: "First Drop", uz: "Birinchi tomchi" },
    objective: { ru: "Разместить сосуд и добавить в него воду.", en: "Place a vessel and add water to it.", uz: "Idishni joylashtirib, unga suv qo'shing." },
    learningPoints: [
      { ru: "размещать оборудование", en: "place equipment", uz: "uskunani joylashtirish" },
      { ru: "добавлять вещество в сосуд", en: "add a material to a vessel", uz: "moddani idishga qo'shish" },
      { ru: "проверять содержимое", en: "inspect contents", uz: "tarkibni tekshirish" },
    ],
    duration: { ru: "~2 минуты", en: "~2 minutes", uz: "~2 daqiqa" },
    allowedEquipment: ["beaker"],
    allowedMaterials: ["H2O"],
  },
  {
    id: 2,
    scenarioId: "measure_water",
    title: { ru: "Точная работа", en: "Precision Work", uz: "Aniq ish" },
    objective: { ru: "Подключить термометр и измерить температуру воды.", en: "Connect a thermometer and measure water temperature.", uz: "Termometrni ulab, suv haroratini o'lchang." },
    learningPoints: [
      { ru: "добавлять воду", en: "add water", uz: "suv qo'shish" },
      { ru: "использовать порт датчика", en: "use a sensor port", uz: "datchik portidan foydalanish" },
      { ru: "считывать измерение", en: "read a measurement", uz: "o'lchovni o'qish" },
    ],
    duration: { ru: "~3 минуты", en: "~3 minutes", uz: "~3 daqiqa" },
    allowedEquipment: ["beaker", "thermometer"],
    allowedMaterials: ["H2O"],
  },
  {
    id: 3,
    scenarioId: "heat_water",
    title: { ru: "Первое нагревание", en: "First Heating", uz: "Birinchi isitish" },
    objective: { ru: "Нагреть воду и наблюдать изменение температуры.", en: "Heat water and observe the temperature change.", uz: "Suvni qizdirib, harorat o'zgarishini kuzating." },
    learningPoints: [
      { ru: "размещать нагреватель", en: "place a heater", uz: "qizdirgichni joylashtirish" },
      { ru: "подключать измерение", en: "connect a measurement", uz: "o'lchovni ulash" },
      { ru: "наблюдать нагрев", en: "observe heating", uz: "qizdirishni kuzatish" },
    ],
    duration: { ru: "~4 минуты", en: "~4 minutes", uz: "~4 daqiqa" },
    allowedEquipment: ["beaker", "hotplate", "thermometer"],
    allowedMaterials: ["H2O"],
  },
  {
    id: 4,
    scenarioId: "transfer_water",
    title: { ru: "Переливание", en: "Pouring", uz: "Quyish" },
    objective: { ru: "Перелить воду из одного сосуда в другой.", en: "Pour water from one vessel into another.", uz: "Suvni bir idishdan boshqasiga quying." },
    learningPoints: [
      { ru: "работать с двумя сосудами", en: "work with two vessels", uz: "ikki idish bilan ishlash" },
      { ru: "выбирать источник и приёмник", en: "select source and destination", uz: "manba va qabul qiluvchini tanlash" },
      { ru: "наблюдать поток жидкости", en: "observe liquid flow", uz: "suyuqlik oqimini kuzatish" },
    ],
    duration: { ru: "~3 минуты", en: "~3 minutes", uz: "~3 daqiqa" },
    allowedEquipment: ["beaker"],
    allowedMaterials: ["H2O"],
  },
  {
    id: 5,
    scenarioId: "cuso4",
    title: { ru: "Первая смесь", en: "First Mixture", uz: "Birinchi aralashma" },
    objective: { ru: "Смешать воду и раствор сульфата меди.", en: "Mix water with copper sulfate solution.", uz: "Suvni mis sulfat eritmasi bilan aralashtiring." },
    learningPoints: [
      { ru: "добавлять два вещества", en: "add two materials", uz: "ikki modda qo'shish" },
      { ru: "запускать смешивание", en: "start mixing", uz: "aralashtirishni boshlash" },
      { ru: "наблюдать изменение раствора", en: "observe solution change", uz: "eritma o'zgarishini kuzatish" },
    ],
    duration: { ru: "~4 минуты", en: "~4 minutes", uz: "~4 daqiqa" },
    allowedEquipment: ["beaker", "pipette"],
    allowedMaterials: ["H2O", "CuSO4(aq)"],
  },
];

export const getChemistryLevel = (level: number | undefined) => chemistryLevels.find((entry) => entry.id === level);
