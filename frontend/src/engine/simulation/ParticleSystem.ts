import { EngineSystem } from './EngineSystem';
import { Workspace } from '../workspace/Workspace';

export interface Particle {
  x: number;
  y: number;
  vx: number;
  vy: number;
  type: string;
  color: string;
  life: number;
  parentId: string;
  radius: number;
  maxRadius: number;
  state: 'gas' | 'bubble' | 'solid' | 'steam' | 'shatter';
}

export class ParticleSystem implements EngineSystem {
  public buffer: Particle[] = [];

  constructor(private readonly workspace: Workspace) {}

  update(deltaSeconds: number): boolean | void {
    let changed = false;
    for (let i = this.buffer.length - 1; i >= 0; i--) {
      const p = this.buffer[i];
      p.life -= deltaSeconds;
      
      const parent = this.workspace.scene.objects.get(p.parentId);
      
      if (p.state === 'solid') {
        p.radius = Math.max(0, p.radius - 0.5 * deltaSeconds);
        if (p.radius <= 0) p.life = 0;
      }
      
      if (p.life <= 0) {
        this.buffer.splice(i, 1);
        changed = true;
        continue;
      }
      
      if (p.state === 'gas' || p.state === 'bubble') {
        p.vy -= 50 * deltaSeconds;
      } else if (p.state === 'solid') {
        p.vy += 98 * deltaSeconds;
      }

      let nx = p.x + p.vx * deltaSeconds;
      let ny = p.y + p.vy * deltaSeconds;

      if (parent) {
        const bounds = parent.boundingBox;
        const left = parent.position.x;
        const right = parent.position.x + bounds.width;
        const top = parent.position.y;
        const bottom = parent.position.y + bounds.height;
        
        const capacity = Number(parent.properties.capacityMl ?? parent.metadata?.capacity ?? 100);
        const volume = Number(parent.properties.volumeMl ?? 0);
        const fillRatio = capacity > 0 ? Math.min(1, Math.max(0, volume / capacity)) : 0;
        const meniscusY = bottom - (fillRatio * bounds.height);

        if (p.state === 'bubble') {
          p.radius = Math.min(p.maxRadius, p.radius + 10 * deltaSeconds);
          if (ny <= meniscusY) {
            this.buffer.splice(i, 1);
            changed = true;
            continue;
          }
        } else if (p.state === 'gas') {
          const temp = Number(parent.properties.temperature ?? 24.5);
          const speedMultiplier = Math.max(0.1, temp / 24.5);
          nx = p.x + p.vx * speedMultiplier * deltaSeconds;
          ny = p.y + p.vy * speedMultiplier * deltaSeconds;

          if (nx < left || nx > right) {
            p.vx *= -1;
            nx = Math.max(left, Math.min(nx, right));
          }
          if (ny < top || ny > bottom) {
            p.vy *= -1;
            ny = Math.max(top, Math.min(ny, bottom));
          }
        } else if (p.state === 'solid') {
          if (ny > bottom - p.radius) {
            ny = bottom - p.radius;
            p.vy = 0;
            p.vx = 0;
          }
        }
      }

      p.x = nx;
      p.y = ny;
      changed = true;
    }
    return changed;
  }

  emit(particle: Particle) {
    if (particle.state === 'bubble' && particle.parentId) {
      const parent = this.workspace.scene.objects.get(particle.parentId);
      if (parent) {
        particle.y = parent.position.y + parent.boundingBox.height;
      }
    }
    this.buffer.push(particle);
  }
}
