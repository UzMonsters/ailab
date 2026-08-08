'use client';
import Link from 'next/link';
import { useTranslations } from 'next-intl';
import { usePathname } from 'next/navigation';
import { useState } from 'react';

export default function AuthPage() {
  const t = useTranslations('auth');
  const tn = useTranslations('common');
  const pathname = usePathname();
  const locale = pathname.split('/')[1] || 'en';
  const [mode, setMode] = useState<'login' | 'register'>('login');
  const [showPassword, setShowPassword] = useState(false);

  return (
    <div className="relative z-10 min-h-screen flex items-center justify-center p-4 md:p-8">
      <div className="glass-card-lg w-full max-w-[1100px] min-h-[680px] grid grid-cols-1 lg:grid-cols-[1.1fr_1fr] overflow-hidden animate-fade-in-up">
        <div className="hidden lg:flex flex-col justify-between p-10 relative bg-gradient-to-br from-white/[0.02] to-[#8b5cf6]/5 border-r border-[var(--border)] overflow-hidden">
          <Link href={`/${locale}`} className="flex items-center gap-3 no-underline text-[var(--foreground)]">
            <div className="w-[42px] h-[42px] rounded-xl bg-gradient-to-br from-[#8b5cf6] to-[#A855F7] flex items-center justify-center text-white text-lg animate-pulse-glow">⚗</div>
            <span className="font-bold text-xl">{t('brandTitle')}</span>
          </Link>
          <div className="my-10">
            <span className="badge mb-6 inline-flex"><span className="w-2 h-2 rounded-full bg-[#14F195] animate-pulse" />{t('brandSubtitle')}</span>
            <h1 className="text-[42px] font-bold leading-[1.15] tracking-tight mb-4">
              {mode === 'login' ? t('welcomeBack') : t('createAccount')}
            </h1>
            <p className="text-[var(--muted-foreground)] text-base leading-relaxed">
              {mode === 'login' ? t('signInDesc') : t('registerDesc')}
            </p>
          </div>
          <div className="space-y-4">
            {[{ icon: '🔬', title: t('feature1Title'), desc: t('feature1Desc') }, { icon: '🤖', title: t('feature2Title'), desc: t('feature2Desc') }, { icon: '👥', title: t('feature3Title'), desc: t('feature3Desc') }].map((f, i) => (
              <div key={i} className="flex items-start gap-3"><span className="text-xl mt-0.5">{f.icon}</span><div><div className="text-sm font-semibold text-[var(--foreground)]">{f.title}</div><div className="text-xs text-[var(--muted-foreground)]">{f.desc}</div></div></div>
            ))}
          </div>
        </div>
        <div className="p-8 md:p-10 flex flex-col justify-center">
          <div className="lg:hidden flex items-center gap-3 mb-8">
            <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-[#8b5cf6] to-[#A855F7] flex items-center justify-center text-white font-bold animate-pulse-glow">⚗</div>
            <span className="font-bold text-lg">{t('brandTitle')}</span>
          </div>
          <div className="flex gap-1 p-1 bg-[var(--input)] rounded-lg border border-[var(--border)] mb-8">
            <button onClick={() => setMode('login')} className={`flex-1 py-2.5 px-4 rounded-md text-sm font-medium transition-all ${mode === 'login' ? 'bg-[#8b5cf6]/15 text-[#8b5cf6] border border-[#8b5cf6]/30' : 'text-[var(--muted-foreground)] hover:text-[var(--foreground)]'}`}>{tn('login')}</button>
            <button onClick={() => setMode('register')} className={`flex-1 py-2.5 px-4 rounded-md text-sm font-medium transition-all ${mode === 'register' ? 'bg-[#8b5cf6]/15 text-[#8b5cf6] border border-[#8b5cf6]/30' : 'text-[var(--muted-foreground)] hover:text-[var(--foreground)]'}`}>{tn('register')}</button>
          </div>
          {mode === 'login' ? (
            <div className="space-y-5">
              <div><label className="block text-xs font-medium text-[var(--muted-foreground)] mb-2">{t('email')}</label><div className="relative"><input type="email" className="w-full bg-[var(--input)] border border-[var(--border)] rounded-[var(--radius-md)] px-4 py-3 pl-10 text-sm text-[var(--foreground)] outline-none focus:border-[var(--ring)]" placeholder="researcher@lab.com" /><svg className="absolute left-3 top-1/2 -translate-y-1/2 text-[var(--muted-foreground)]" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><rect width="20" height="16" x="2" y="4" rx="2"/><path d="m22 7-8.97 5.7a1.94 1.94 0 0 1-2.06 0L2 7"/></svg></div></div>
              <div><label className="block text-xs font-medium text-[var(--muted-foreground)] mb-2">{t('password')}</label><div className="relative"><input type={showPassword ? 'text' : 'password'} className="w-full bg-[var(--input)] border border-[var(--border)] rounded-[var(--radius-md)] px-4 py-3 pl-10 pr-10 text-sm text-[var(--foreground)] outline-none focus:border-[var(--ring)]" placeholder="••••••••" /><button type="button" onClick={() => setShowPassword(!showPassword)} className="absolute right-3 top-1/2 -translate-y-1/2 text-[var(--muted-foreground)] hover:text-[var(--foreground)]">{showPassword ? '🙈' : '👁'}</button></div></div>
              <div className="flex items-center justify-between"><label className="flex items-center gap-2 text-sm text-[var(--muted-foreground)] cursor-pointer"><input type="checkbox" className="w-4 h-4 rounded border-[var(--border)] bg-[var(--input)] accent-[#8b5cf6]" />{t('rememberMe')}</label><a href="#" className="text-xs text-[#8b5cf6] hover:underline">{t('forgotPassword')}</a></div>
              <button className="w-full py-3 bg-gradient-to-br from-[#8b5cf6] to-[#A855F7] text-white rounded-[var(--radius-md)] text-sm font-semibold shadow-[0_10px_25px_rgba(139,92,246,0.4)] hover:-translate-y-0.5 transition-all">{t('signInButton')}</button>
              <div className="relative my-6"><div className="absolute inset-0 flex items-center"><div className="w-full border-t border-[var(--border)]" /></div><div className="relative flex justify-center text-xs"><span className="bg-[#090909] px-3 text-[var(--muted-foreground)]">{t('orContinueWith')}</span></div></div>
              <div className="grid grid-cols-3 gap-3">
                {['ORCID', 'GitHub', 'Google'].map((p) => (<button key={p} className="py-2.5 px-4 bg-[var(--input)] border border-[var(--border)] rounded-[var(--radius-md)] text-sm text-[var(--foreground)] hover:bg-white/[0.08] hover:border-white/20 transition-all">{p}</button>))}
              </div>
              <p className="text-center text-sm text-[var(--muted-foreground)] mt-6">{t('noAccount')}{' '}<button onClick={() => setMode('register')} className="text-[#8b5cf6] hover:underline font-medium bg-transparent border-none cursor-pointer text-sm">{tn('register')}</button></p>
            </div>
          ) : (
            <div className="space-y-5">
              <div><label className="block text-xs font-medium text-[var(--muted-foreground)] mb-2">{t('fullName')}</label><input type="text" className="w-full bg-[var(--input)] border border-[var(--border)] rounded-[var(--radius-md)] px-4 py-3 text-sm text-[var(--foreground)] outline-none focus:border-[var(--ring)]" placeholder="Dr. Jane Smith" /></div>
              <div><label className="block text-xs font-medium text-[var(--muted-foreground)] mb-2">{t('email')}</label><input type="email" className="w-full bg-[var(--input)] border border-[var(--border)] rounded-[var(--radius-md)] px-4 py-3 text-sm text-[var(--foreground)] outline-none focus:border-[var(--ring)]" placeholder="researcher@lab.com" /></div>
              <div><label className="block text-xs font-medium text-[var(--muted-foreground)] mb-2">{t('researchDiscipline')}</label><select className="w-full bg-[var(--input)] border border-[var(--border)] rounded-[var(--radius-md)] px-4 py-3 text-sm text-[var(--foreground)] outline-none focus:border-[var(--ring)] cursor-pointer appearance-none"><option value="">Select discipline...</option><option value="chemistry">{t('disciplines.chemistry')}</option><option value="physics">{t('disciplines.physics')}</option><option value="biology">{t('disciplines.biology')}</option></select></div>
              <div><label className="block text-xs font-medium text-[var(--muted-foreground)] mb-2">{t('password')}</label><input type="password" className="w-full bg-[var(--input)] border border-[var(--border)] rounded-[var(--radius-md)] px-4 py-3 text-sm text-[var(--foreground)] outline-none focus:border-[var(--ring)]" placeholder="••••••••" /></div>
              <div><label className="block text-xs font-medium text-[var(--muted-foreground)] mb-2">{t('confirmPassword')}</label><input type="password" className="w-full bg-[var(--input)] border border-[var(--border)] rounded-[var(--radius-md)] px-4 py-3 text-sm text-[var(--foreground)] outline-none focus:border-[var(--ring)]" placeholder="••••••••" /></div>
              <button className="w-full py-3 bg-gradient-to-br from-[#8b5cf6] to-[#A855F7] text-white rounded-[var(--radius-md)] text-sm font-semibold shadow-[0_10px_25px_rgba(139,92,246,0.4)] hover:-translate-y-0.5 transition-all">{t('createAccountButton')}</button>
              <p className="text-center text-sm text-[var(--muted-foreground)] mt-6">{t('hasAccount')}{' '}<button onClick={() => setMode('login')} className="text-[#8b5cf6] hover:underline font-medium bg-transparent border-none cursor-pointer text-sm">{tn('login')}</button></p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
