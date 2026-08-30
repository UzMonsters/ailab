import type { LaboratoryObject } from '../objects/LaboratoryObject';
import { defaultReactionRegistry, type ReactionRegistry } from './ReactionRegistry';

type Content = Record<string, unknown>;

export type MixResult = {
  reacted: boolean;
  reactionId?: string;
  events: string[];
};

const contentId = (content: Content) => String(content.materialId ?? content.formula ?? '');
const contentAmount = (content: Content) => Math.max(0, Number(content.amount ?? 0));

/**
 * Applies one deterministic registry reaction after a deliberate user mix.
 * It never invents products: only ReactionRegistry definitions can mutate the
 * composition. If no rule matches, the contents are merely marked homogeneous.
 */
export function mixObjectContents(
  object: LaboratoryObject,
  registry: ReactionRegistry = defaultReactionRegistry,
): MixResult {
  const events = ['Mixing started'];
  const componentLabels = object.contents
    .filter((content) => contentAmount(content) > 0)
    .map((content) => String(content.formula ?? content.materialId ?? content.name ?? 'component'));

  for (const label of componentLabels) events.push(`${label} dispersed`);

  const temperature = Number(object.properties.temperature ?? 24.5);
  const reaction = registry.reactions.find((candidate) => {
    if (candidate.activationTempC !== undefined && temperature < candidate.activationTempC) return false;
    const reactantsPresent = Object.entries(candidate.reactants).every(([id, ratio]) => {
      const content = object.contents.find((entry) => contentId(entry) === id);
      return contentAmount(content ?? {}) >= ratio;
    });
    const solventsPresent = candidate.requiredSolvents?.every((id) => {
      const content = object.contents.find((entry) => contentId(entry) === id);
      return contentAmount(content ?? {}) > 0.1;
    }) ?? true;
    return reactantsPresent && solventsPresent;
  });

  if (!reaction) {
    // A prepared aqueous CuSO₄ sample plus water is a dilution, not a new
    // reaction. Present it as one homogeneous solution so the inspector and
    // the level result reflect what the learner has actually made.
    const copperSulfateSolution = object.contents.find((content) => contentId(content) === 'CuSO4(aq)' && String(content.phase) === 'aqueous');
    const water = object.contents.find((content) => ['H2O', 'water', 'H₂O'].includes(contentId(content)));
    if (copperSulfateSolution && water && contentAmount(water) > 0) {
      copperSulfateSolution.amount = contentAmount(copperSulfateSolution) + contentAmount(water);
      copperSulfateSolution.metadata = {
        ...(copperSulfateSolution.metadata as Record<string, unknown> | undefined),
        homogeneous: true,
        dilutedWith: 'H2O',
      };
      object.contents = object.contents.filter((content) => content !== water);
      object.material = {
        id: 'CuSO4(aq)',
        name: String(copperSulfateSolution.name ?? 'Copper sulfate solution'),
        formula: String(copperSulfateSolution.formula ?? 'CuSO₄(aq)'),
        state: 'aqueous',
        color: String(copperSulfateSolution.color ?? '#3B82F6'),
      };
    }
    object.contents = object.contents.map((content) => ({
      ...content,
      metadata: { ...(content.metadata as Record<string, unknown> | undefined), homogeneous: true },
    }));
    object.properties.mixtureState = 'homogeneous';
    events.push('Mixture homogeneous', 'No new reaction detected');
    return { reacted: false, events };
  }

  const extent = Math.min(...Object.entries(reaction.reactants).map(([id, ratio]) => {
    const content = object.contents.find((entry) => contentId(entry) === id);
    return contentAmount(content ?? {}) / ratio;
  }));

  for (const [id, ratio] of Object.entries(reaction.reactants)) {
    const content = object.contents.find((entry) => contentId(entry) === id);
    if (!content) continue;
    content.amount = Math.max(0, contentAmount(content) - ratio * extent);
    if (content.molarAmount !== undefined) content.molarAmount = Math.max(0, Number(content.molarAmount) - ratio * extent);
  }

  for (const [id, ratio] of Object.entries(reaction.products)) {
    const amount = ratio * extent;
    let product = object.contents.find((entry) => contentId(entry) === id);
    if (!product) {
      const phase = reaction.productPhases?.[id] ?? 'aqueous';
      product = {
        materialId: id,
        name: id,
        formula: id,
        amount: 0,
        molarAmount: 0,
        unit: phase === 'solid' ? 'g' : 'mol',
        phase,
        color: reaction.productColors?.[id] ?? (phase === 'gas' ? '#e2e8f0' : '#94a3b8'),
      };
      object.contents.push(product);
    }
    product.amount = contentAmount(product) + amount;
    product.molarAmount = Number(product.molarAmount ?? 0) + amount;
    product.metadata = { ...(product.metadata as Record<string, unknown> | undefined), homogeneous: true, reactionId: reaction.id };
  }

  object.contents = object.contents.filter((content) => contentAmount(content) > 0.001);
  const visibleProduct = object.contents.find((content) => content.phase === 'aqueous' || content.phase === 'liquid');
  if (visibleProduct) {
    object.material = {
      id: contentId(visibleProduct),
      name: String(visibleProduct.name ?? visibleProduct.formula ?? contentId(visibleProduct)),
      formula: String(visibleProduct.formula ?? contentId(visibleProduct)),
      state: String(visibleProduct.phase ?? 'aqueous'),
      color: String(visibleProduct.color ?? '#94a3b8'),
    };
  }
  object.properties.temperature = temperature + Number(reaction.temperatureDeltaC ?? 0);
  object.properties.mixtureState = 'reacted';
  object.properties.gasGeneration = object.contents.some((content) => content.phase === 'gas');
  object.properties.precipitate = object.contents.some((content) => content.phase === 'solid');
  events.push('Mixture homogeneous', `Reaction detected: ${reaction.label}`);
  return { reacted: true, reactionId: reaction.id, events };
}
