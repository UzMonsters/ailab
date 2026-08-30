import { Workspace } from '../workspace/Workspace';
import type { EngineSystem } from './EngineSystem';
import { defaultReactionRegistry } from './ReactionRegistry';

type Content = Record<string, unknown>;
const contentId = (content: Content) => String(content.materialId ?? content.formula ?? '');
const contentAmount = (content: Content) => Number(content.amount ?? content.amountMl ?? 0);

export class ReactionSystem implements EngineSystem {
  constructor(
    private readonly workspace: Workspace,
    private readonly registry = defaultReactionRegistry
  ) {}

  update(deltaSeconds: number): boolean {
    if (!this.workspace.simulation.running) return false;
    let changed = false;

    for (const object of this.workspace.scene.objects.values()) {
      if (!object.contents || object.contents.length === 0) continue;

      const currentTemp = Number(object.properties.temperature ?? 24.5);
      
      for (const reaction of this.registry.reactions) {
        if (reaction.activationTempC !== undefined && currentTemp < reaction.activationTempC) {
          continue;
        }

        // Check required solvents
        if (reaction.requiredSolvents) {
          const hasSolvents = reaction.requiredSolvents.every(solvent => {
            const c = object.contents.find(content => contentId(content) === solvent);
            return c && contentAmount(c) > 0.1;
          });
          if (!hasSolvents) continue;
        }

        let maxReactionAmount = Infinity;
        
        for (const [reactantId, stoichiometry] of Object.entries(reaction.reactants)) {
          const contentItem = object.contents.find((content) => contentId(content) === reactantId);
          const currentAmount = contentItem ? contentAmount(contentItem) : 0;
          
          if (currentAmount <= 0) {
            maxReactionAmount = 0;
            break;
          }
          
          const possibleReactions = currentAmount / stoichiometry;
          if (possibleReactions < maxReactionAmount) {
            maxReactionAmount = possibleReactions;
          }
        }
        
        if (maxReactionAmount <= 0) continue;

        const ratePerSecond = 5;
        const actualReactionAmount = Math.min(maxReactionAmount, ratePerSecond * deltaSeconds);

        if (actualReactionAmount <= 0) continue;

        for (const [reactantId, stoichiometry] of Object.entries(reaction.reactants)) {
          const contentItem = object.contents.find((content) => contentId(content) === reactantId);
          if (contentItem) {
            contentItem.amount = contentAmount(contentItem) - (stoichiometry * actualReactionAmount);
          }
        }
        
        for (const [productId, stoichiometry] of Object.entries(reaction.products)) {
          let contentItem = object.contents.find((content) => contentId(content) === productId);
          if (!contentItem) {
            const productPhase = reaction.productPhases?.[productId];
            const productColor = reaction.productColors?.[productId];
            if (!productPhase) continue;
            contentItem = { materialId: productId, formula: productId, name: productId, amount: 0, unit: 'mol', molarAmount: 0, phase: productPhase, color: productColor, metadata: { source: 'local-reaction', phaseValidated: true } };
            object.contents.push(contentItem);
          }
          contentItem.amount = contentAmount(contentItem) + (stoichiometry * actualReactionAmount);
          contentItem.molarAmount = contentAmount(contentItem);
        }
        
        object.contents = object.contents.filter((content) => contentAmount(content) > 0.001);
        
        if (reaction.enthalpyJ) {
          const heatGeneratedJ = -(reaction.enthalpyJ * actualReactionAmount);
          const currentHeat = Number(object.properties.heatEnergyJ ?? 0);
          object.properties.heatEnergyJ = currentHeat + heatGeneratedJ;
        }
        
        changed = true;
      }
    }

    return changed;
  }
}
