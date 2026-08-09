'use client';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { useTranslations } from 'next-intl';
import { useRef, useEffect, useState } from 'react';
import { Atom, FlaskConical, Microscope, BrainCircuit, Route, Users, Sparkles, ArrowRight, CheckCircle2, Cpu, ShieldCheck, Network } from 'lucide-react';
import ScienceBackground, { BackgroundGlow } from '@/components/common/ScienceBackground';

function Reveal({ children, className = '', delay = 0 }: { children: React.ReactNode; className?: string; delay?: number }) {
  const ref = useRef<HTMLDivElement>(null);
  const [visible, setVisible] = useState(false);
  useEffect(() => {
    const node = ref.current;
    if (!node) return;
    const observer = new IntersectionObserver(([entry]) => {
      if (entry.isIntersecting) { setVisible(true); observer.disconnect(); }
    }, { threshold: 0.1 });
    observer.observe(node);
    return () => observer.disconnect();
  }, []);
  return <div ref={ref} className={`transition-all duration-700 ${visible ? 'opacity-100 translate-y-0' : 'opacity-0 translate-y-6'} ${className}`} style={{ transitionDelay: `${delay}ms` }}>{children}</div>;
}

export default function AboutPage() {
  const pathname = usePathname();
  const t = useTranslations('about');
  const locale = pathname.split('/')[1] || 'en';
  const [robotMsg, setRobotMsg] = useState<string>(t('copilotMsg1'));

  const timeline = [
    { year: '2026.07', title: t('t2024'), desc: t('d2024') },
    { year: '2026.08', title: t('t2025'), desc: t('d2025') },
    { year: '2026.10', title: t('t2026'), desc: t('d2026') },
  ];

  const team = [
    { name: 'Alexey Gromov', role: t('role1'), bio: t('bio1'), img: 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150&auto=format&fit=crop&q=80', accent: '#8B5CF6' },
    { name: 'Dmitry Orlov', role: t('role2'), bio: t('bio2'), img: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&auto=format&fit=crop&q=80', accent: '#14F195' },
    { name: 'Elena Belova', role: t('role3'), bio: t('bio3'), img: 'https://images.unsplash.com/photo-1517841905240-472988babdf9?w=150&auto=format&fit=crop&q=80', accent: '#F59E0B' },
  ];

  return (
    <div className="relative min-h-screen flex items-center justify-center p-4 md:p-8" style={{ backgroundColor: '#050508' }}>
      <BackgroundGlow />
      <ScienceBackground />

      <div className="relative z-10 w-full max-w-[1200px] bg-[var(--card)]/90 backdrop-blur-2xl border border-white/10 rounded-[var(--radius-lg)] shadow-[0_30px_60px_rgba(0,0,0,.6),0_0_80px_rgba(139,92,246,.08)] overflow-hidden">
        <div className="grid grid-cols-1 lg:grid-cols-[1.1fr_1fr]">
          {/* LEFT PANEL */}
          <section className="p-6 md:p-11 flex flex-col justify-between gap-8 bg-gradient-to-br from-white/[0.02] to-[#8b5cf6]/[0.06] border-r border-white/[0.07]">
            <div>
              <Link href={`/${locale}`} className="inline-flex items-center gap-3 no-underline text-[var(--foreground)] mb-8">
                <div className="w-[42px] h-[42px] bg-gradient-to-br from-[#8b5cf6] to-[#A855F7] rounded-[12px] flex items-center justify-center shadow-[0_0_20px_rgba(139,92,246,.4)]">
                  <Atom size={20} className="text-white" />
                </div>
                <span className="font-bold text-xl">jas<span className="text-[#8b5cf6]">Core</span></span>
              </Link>

              <div className="inline-flex items-center gap-2 px-3.5 py-1.5 bg-[#8b5cf6]/10 border border-[#8b5cf6]/30 rounded-full text-[11px] font-mono text-[#C084FC] mb-6 tracking-wider uppercase">
                <Sparkles size={11} /> {t('badge')}
              </div>

              <h1 className="text-4xl md:text-[38px] font-extrabold leading-[1.15] tracking-tight mb-4" style={{ background: 'linear-gradient(180deg, #FFFFFF 0%, #CBD5E1 100%)', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent' }}>
                {t('title1')}<br />{t('title2')} <span style={{ background: 'linear-gradient(135deg, #8b5cf6, #14F195)', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent' }}>{t('title3')}</span>
              </h1>

              <div className="space-y-3.5 text-[13.5px] leading-relaxed text-[var(--muted-foreground)]">
                <p><strong className="text-[var(--foreground)]">jasCore</strong> {t('p1')}</p>
                <p>{t('p2')}</p>
              </div>
            </div>

            <div>
              <div className="flex items-center gap-2.5 mb-4 font-bold text-[15px]"><Route size={16} className="text-[#8b5cf6]" /> {t('path')}</div>
              <div className="relative pl-5 flex flex-col gap-4">
                <span className="absolute left-[5px] top-2 bottom-2 w-[2px] bg-gradient-to-b from-[#8b5cf6] to-[#14F195] opacity-40" />
                {timeline.map((item) => (
                  <div key={item.year} className="relative">
                    <span className="absolute -left-5 top-1.5 w-3 h-3 rounded-full bg-[#050508] border-2 border-[#8b5cf6] shadow-[0_0_10px_rgba(139,92,246,.4)]" />
                    <div className="font-mono text-[11px] font-bold text-[#14F195]">{item.year}</div>
                    <div className="text-[13px] font-semibold text-[var(--foreground)]">{item.title}</div>
                    <div className="text-[12px] text-[var(--dim)] leading-snug">{item.desc}</div>
                  </div>
                ))}
              </div>
            </div>

            <div className="flex items-center justify-between pt-4 border-t border-white/5 text-xs text-[var(--dim)]">
              <div className="flex items-center gap-2 text-[#14F195] font-mono"><span className="w-2 h-2 rounded-full bg-[#14F195] shadow-[0_0_10px_#14F195] animate-pulse" /> {t('status')}</div>
              <span>&copy; 2026 jasCore Inc.</span>
            </div>
          </section>

          {/* RIGHT PANEL */}
          <section className="p-6 md:p-11 flex flex-col justify-between gap-8">
            <div>
              <div className="flex items-center gap-2.5 mb-4 font-bold text-[15px]"><Users size={16} className="text-[#8b5cf6]" /> {t('team')}</div>
              <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
                {team.map((member) => (
                  <div key={member.name} className="group bg-white/[0.03] border border-white/[0.08] rounded-[var(--radius-md)] p-3 flex flex-col items-center text-center transition-all hover:border-[#8b5cf6]/60 hover:-translate-y-1 hover:bg-white/[0.05] hover:shadow-[0_10px_20px_rgba(0,0,0,.4)]">
                    <div className="w-[72px] h-[72px] rounded-full p-[2px] mb-2.5" style={{ background: `linear-gradient(135deg, #8b5cf6, ${member.accent})` }}>
                      <img src={member.img} alt={member.name} className="w-full h-full rounded-full object-cover" style={{ backgroundColor: '#151622' }} />
                    </div>
                    <div className="text-[13px] font-bold text-[var(--foreground)]">{member.name}</div>
                    <div className="text-[10px] font-mono text-[#8b5cf6] mb-1.5">{member.role}</div>
                    <div className="text-[11px] text-[var(--dim)] leading-snug">{member.bio}</div>
                  </div>
                ))}
              </div>

              <div className="space-y-3.5 text-[13.5px] leading-relaxed text-[var(--muted-foreground)] my-5">
                <p>{t('teamDesc')}</p>
              </div>

            </div>

            <div className="flex flex-col gap-3">
              <div className="grid grid-cols-3 gap-2">
                <div className="flex items-center gap-2 text-[11px] text-[var(--muted-foreground)]"><CheckCircle2 size={13} className="text-[#14F195] flex-shrink-0" /> {t('feature1')}</div>
                <div className="flex items-center gap-2 text-[11px] text-[var(--muted-foreground)]"><ShieldCheck size={13} className="text-[#14F195] flex-shrink-0" /> {t('feature2')}</div>
                <div className="flex items-center gap-2 text-[11px] text-[var(--muted-foreground)]"><Cpu size={13} className="text-[#14F195] flex-shrink-0" /> {t('feature3')}</div>
              </div>
              <Link href={`/${locale}/auth`} className="w-full py-3.5 bg-gradient-to-br from-[#8b5cf6] to-[#A855F7] text-white rounded-[var(--radius-md)] text-[13px] font-semibold no-underline flex items-center justify-center gap-2.5 shadow-[0_10px_25px_rgba(139,92,246,.4)] transition-all hover:-translate-y-0.5 hover:shadow-[0_15px_35px_rgba(139,92,246,.6)]">
                {t('cta')} <ArrowRight size={14} />
              </Link>
            </div>
          </section>
        </div>
      </div>
    </div>
  );
}
