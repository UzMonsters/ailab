export interface Command { label: string; execute(): void; undo(): void; }
export class CommandHistory {
  private past: Command[] = [];
  private future: Command[] = [];
  constructor(private readonly resolveLabel: (label: string) => string = (label) => label) {}
  execute(command: Command) { command.execute(); this.past.push(command); this.future = []; }
  undo() { const command = this.past.pop(); if (!command) return; command.undo(); this.future.push(command); }
  redo() { const command = this.future.pop(); if (!command) return; command.execute(); this.past.push(command); }
  clear() { this.past = []; this.future = []; }
  get entries() { return this.past.map(({ label }) => this.resolveLabel(label)); }
  get canUndo() { return this.past.length > 0; }
  get canRedo() { return this.future.length > 0; }
}
