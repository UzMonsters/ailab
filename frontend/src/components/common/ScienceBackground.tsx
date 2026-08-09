'use client';

import { useEffect, useRef, useCallback } from 'react';

interface ScienceBackgroundProps {
  className?: string;
}

export default function ScienceBackground({ className }: ScienceBackgroundProps) {
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const particlesRef = useRef<Array<{ x: number; y: number; radius: number; vx: number; vy: number; color: string; alpha: number }>>([]);
  const mouseRef = useRef({ x: 0, y: 0, radius: 140 });

  const initParticles = useCallback((width: number, height: number) => {
    const count = Math.min(Math.floor(width / 16), 70);
    particlesRef.current = Array.from({ length: count }, () => ({
      x: Math.random() * width,
      y: Math.random() * height,
      radius: Math.random() * 2 + 1,
      vx: (Math.random() - 0.5) * 0.7,
      vy: (Math.random() - 0.5) * 0.7,
      color: Math.random() > 0.4 ? 'rgba(139, 92, 246, ' : 'rgba(20, 241, 149, ',
      alpha: Math.random() * 0.5 + 0.2,
    }));
  }, []);

  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    const resize = () => {
      canvas.width = window.innerWidth;
      canvas.height = window.innerHeight;
      initParticles(canvas.width, canvas.height);
    };
    resize();

    const handleMouse = (e: MouseEvent) => {
      mouseRef.current = { ...mouseRef.current, x: e.clientX, y: e.clientY };
    };
    window.addEventListener('mousemove', handleMouse);
    window.addEventListener('resize', resize);

    let animId: number;
    const animate = () => {
      ctx.clearRect(0, 0, canvas.width, canvas.height);
      const particles = particlesRef.current;
      const mouse = mouseRef.current;

      for (let a = 0; a < particles.length; a++) {
        for (let b = a + 1; b < particles.length; b++) {
          const dx = particles[a].x - particles[b].x;
          const dy = particles[a].y - particles[b].y;
          const dist = Math.sqrt(dx * dx + dy * dy);
          if (dist < 120) {
            const opacity = (1 - dist / 120) * 0.22;
            ctx.beginPath();
            ctx.strokeStyle = `rgba(139, 92, 246, ${opacity})`;
            ctx.lineWidth = 1;
            ctx.moveTo(particles[a].x, particles[a].y);
            ctx.lineTo(particles[b].x, particles[b].y);
            ctx.stroke();
          }
        }
      }

      particles.forEach((p) => {
        p.x += p.vx;
        p.y += p.vy;
        if (p.x < 0 || p.x > canvas.width) p.vx *= -1;
        if (p.y < 0 || p.y > canvas.height) p.vy *= -1;

        if (mouse.x && mouse.y) {
          const dx = mouse.x - p.x;
          const dy = mouse.y - p.y;
          const dist = Math.sqrt(dx * dx + dy * dy);
          if (dist < mouse.radius) {
            const angle = Math.atan2(dy, dx);
            const force = (mouse.radius - dist) / mouse.radius;
            p.x -= Math.cos(angle) * force * 2.5;
            p.y -= Math.sin(angle) * force * 2.5;
          }
        }

        ctx.beginPath();
        ctx.arc(p.x, p.y, p.radius, 0, Math.PI * 2);
        ctx.fillStyle = p.color + p.alpha + ')';
        ctx.shadowBlur = 8;
        ctx.shadowColor = 'rgba(139, 92, 246, 0.4)';
        ctx.fill();
      });
      animId = requestAnimationFrame(animate);
    };
    animate();

    return () => {
      window.removeEventListener('mousemove', handleMouse);
      window.removeEventListener('resize', resize);
      cancelAnimationFrame(animId);
    };
  }, [initParticles]);

  return (
    <canvas
      ref={canvasRef}
      className={`fixed inset-0 w-full h-full pointer-events-none ${className || ''}`}
      style={{ zIndex: 1 }}
    />
  );
}

export function BackgroundGlow() {
  return (
    <div
      className="fixed inset-0 pointer-events-none"
      style={{
        zIndex: 0,
        background: 'radial-gradient(circle at 10% 15%, rgba(139,92,246,0.18) 0%, transparent 45%), radial-gradient(circle at 85% 80%, rgba(168,85,247,0.15) 0%, transparent 50%), radial-gradient(circle at 50% 40%, rgba(20,241,149,0.04) 0%, transparent 60%)',
      }}
    />
  );
}
