import React, { useEffect, useRef } from 'react';
import type { Engine } from '@/engine/core/Engine';

export interface ParticleCanvasProps {
  engine: Engine | null;
}

export const ParticleCanvas: React.FC<ParticleCanvasProps> = ({ engine }) => {
  const canvasRef = useRef<HTMLCanvasElement>(null);

  useEffect(() => {
    if (!engine) return;
    const canvas = canvasRef.current;
    if (!canvas) return;

    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    let animationFrameId: number;

    const resize = () => {
      const parent = canvas.parentElement;
      if (!parent) return;
      const rect = parent.getBoundingClientRect();
      const dpr = window.devicePixelRatio || 1;
      
      canvas.width = rect.width * dpr;
      canvas.height = rect.height * dpr;
      canvas.style.width = `${rect.width}px`;
      canvas.style.height = `${rect.height}px`;
      
      ctx.scale(dpr, dpr);
    };

    window.addEventListener('resize', resize);
    resize();

    const render = () => {
      // We might need to know the camera pan and zoom if we want the particles to move with the scene.
      // But for this simulation, we'll draw based on the absolute scene coordinates.
      // We can grab the pan and zoom from the engine workspace camera if we need,
      // but let's assume `ParticleSystem` gives absolute scene coordinates.
      
      // Clear canvas
      ctx.clearRect(0, 0, canvas.width, canvas.height);

      const camera = engine.workspace.scene.camera;
      const panX = camera?.position.x ?? 0;
      const panY = camera?.position.y ?? 0;
      const zoom = camera?.zoom ?? 1;

      ctx.save();
      ctx.translate(panX, panY);
      ctx.scale(zoom, zoom);

      // Draw particles
      const buffer = engine.particles.buffer;
      canvas.dataset.particleCount = String(buffer.length);
      canvas.dataset.shatterCount = String(buffer.filter((particle) => particle.state === 'shatter').length);
      for (const p of buffer) {
        ctx.save();
        ctx.beginPath();
        
        const radius = p.radius ?? 2;
        ctx.arc(p.x, p.y, radius, 0, Math.PI * 2);
        
        if (p.state === 'gas') {
          ctx.globalCompositeOperation = 'screen';
          ctx.fillStyle = p.color;
          ctx.fill();
        } else if (p.state === 'bubble') {
          ctx.fillStyle = 'white';
          ctx.fill();
          ctx.strokeStyle = 'rgba(255, 255, 255, 0.5)';
          ctx.lineWidth = 1;
          ctx.stroke();
        } else {
          ctx.fillStyle = p.color;
          ctx.fill();
        }
        
        ctx.restore();
      }
      
      ctx.restore();
      animationFrameId = window.requestAnimationFrame(render);
    };

    render();

    return () => {
      window.removeEventListener('resize', resize);
      window.cancelAnimationFrame(animationFrameId);
    };
  }, [engine]);

  return (
    <canvas
      ref={canvasRef}
      data-testid="particle-canvas"
      data-particle-count="0"
      data-shatter-count="0"
      aria-label="Simulation particles and fracture fragments"
      className="absolute inset-0 pointer-events-none z-10"
    />
  );
};
