'use client';
import Link from 'next/link';
import { useTranslations } from 'next-intl';
import { usePathname, useRouter } from 'next/navigation';
import { useState } from 'react';
import { Atom, FlaskConical, Brain, Users, Mail, Lock, User, Eye, EyeOff, ArrowRight, Loader2, AlertCircle, XCircle } from 'lucide-react';
import { useAuthStore } from '@/stores/auth.store';
import ScienceBackground, { BackgroundGlow } from '@/shared/ui/ScienceBackground';
import { normalizeError } from '@/shared/lib/errors';

interface FieldErrors {
  username?: string;
  email?: string;
  password?: string;
  confirmPassword?: string;
}

export default function AuthPage() {
  const t = useTranslations('auth');
  const tn = useTranslations('common');
  const pathname = usePathname();
  const router = useRouter();
  const locale = pathname.split('/')[1] || 'en';
  const { login, register, isLoading, error, clearError } = useAuthStore();

  const [mode, setMode] = useState<'login' | 'register'>('login');
  const [showPassword, setShowPassword] = useState(false);

  const [form, setForm] = useState({ username: '', email: '', password: '', confirmPassword: '' });
  const [fieldErrors, setFieldErrors] = useState<FieldErrors>({});

  const validateLogin = (): boolean => {
    const errs: FieldErrors = {};
    if (!form.email.trim()) errs.email = 'Email/username is required';
    if (!form.password) errs.password = 'Password is required';
    setFieldErrors(errs);
    return Object.keys(errs).length === 0;
  };

  const validateRegister = (): boolean => {
    const errs: FieldErrors = {};
    if (!form.username.trim() || form.username.length < 3) errs.username = 'Username must be at least 3 characters';
    if (!form.email.trim() || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) errs.email = 'Valid email is required';
    if (!form.password || form.password.length < 8) errs.password = 'Password must be at least 8 characters';
    if (form.password !== form.confirmPassword) errs.confirmPassword = 'Passwords do not match';
    setFieldErrors(errs);
    return Object.keys(errs).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    clearError();
    if (mode === 'login') {
      if (!validateLogin()) return;
      try {
        await login(form.email, form.password);
        const user = useAuthStore.getState().user;
        if (user?.role === 'ROLE_ADMIN') {
          router.push(`/${locale}/admin`);
        } else {
          router.push(`/${locale}/dashboard`);
        }
      } catch {
        // error handled by store
      }
    } else {
      if (!validateRegister()) return;
      try {
        await register(form.username, form.email, form.password);
        router.push(`/${locale}/dashboard`);
      } catch (err: unknown) {
        const normalized = normalizeError(err);
        if (normalized.status === 409) {
          const msg = normalized.message.toLowerCase();
          if (msg.includes('username')) setFieldErrors({ username: 'Username already taken' });
          else if (msg.includes('email')) setFieldErrors({ email: 'Email already registered' });
        }
      }
    }
  };

  const updateField = (field: string) => (e: React.ChangeEvent<HTMLInputElement>) => {
    setForm((p) => ({ ...p, [field]: e.target.value }));
    if (fieldErrors[field as keyof FieldErrors]) {
      setFieldErrors((p) => ({ ...p, [field]: undefined }));
    }
  };

  const switchMode = (m: 'login' | 'register') => {
    setMode(m);
    clearError();
    setFieldErrors({});
  };

  return (
    <div className="relative min-h-screen flex items-center justify-center p-4 md:p-8" style={{ backgroundColor: 'var(--background)' }}>
      <BackgroundGlow />
      <ScienceBackground />

      <div className="relative z-10 w-full max-w-[1100px] min-h-[680px] bg-[var(--card)]/95 backdrop-blur-xl border border-[var(--border)] rounded-[var(--radius-lg)] shadow-[0_30px_60px_rgba(0,0,0,.6)] grid grid-cols-1 lg:grid-cols-[1.1fr_1fr] overflow-hidden">
        {/* Left Brand Panel */}
        <div className="hidden lg:flex flex-col justify-between p-10 relative bg-gradient-to-br from-white/[0.02] to-[#8b5cf6]/5 border-r border-[var(--border)]">
          <Link href={`/${locale}`} className="flex items-center gap-3 no-underline text-[var(--foreground)]">
            <div className="w-[42px] h-[42px] rounded-xl bg-gradient-to-br from-[#8b5cf6] to-[#A855F7] flex items-center justify-center shadow-[0_0_20px_rgba(139,92,246,.4)]"><Atom size={20} className="text-white" /></div>
            <span className="font-bold text-xl">AI <span className="text-[#8b5cf6]">Laboratory</span></span>
          </Link>
          <div className="my-10">
            <div className="inline-flex items-center gap-2 px-3 py-1.5 bg-[#8b5cf6]/10 border border-[#8b5cf6]/30 rounded-full text-xs font-mono text-[#C084FC] mb-6 tracking-wider uppercase"><Atom size={10} /> The Scientific OS</div>
            <h1 className="text-[42px] font-bold leading-[1.15] tracking-tight mb-4" style={{ background: 'linear-gradient(180deg, #FFFFFF 0%, #CBD5E1 100%)', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent' }}>
              Experiment.<br/>Simulate. <span style={{ background: 'linear-gradient(135deg, #8b5cf6, #14F195)', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent' }}>Discover.</span>
            </h1>
            <p className="text-[var(--muted-foreground)] text-sm leading-relaxed max-w-[420px]">{t('signInDesc')}</p>
            <div className="flex flex-col gap-3 mt-8">
              {[{ icon: FlaskConical, text: 'Real-time molecular dynamics & simulations' }, { icon: Brain, text: 'AI-powered chemical equation solver' }, { icon: Users, text: 'Secure research data with role-based access' }].map((f, i) => (
                <div key={i} className="flex items-center gap-3 text-sm text-[var(--muted-foreground)]"><div className="w-7 h-7 rounded-full bg-[#14F195]/10 flex items-center justify-center flex-shrink-0"><f.icon size={14} className="text-[#14F195]" /></div>{f.text}</div>
              ))}
            </div>
          </div>
          <div className="flex items-center justify-between pt-5 border-t border-white/5 text-xs text-[var(--dim)]">
            <div className="flex items-center gap-2 text-[#14F195] font-mono"><div className="w-2 h-2 rounded-full bg-[#14F195] shadow-[0_0_10px_#14F195] animate-pulse" />Science v4.8 Active</div>
            <span>&copy; 2026 jasScience Inc.</span>
          </div>
        </div>

        {/* Right Form Panel */}
        <div className="p-8 md:p-10 flex flex-col justify-center">
          <div className="lg:hidden flex items-center gap-3 mb-8">
            <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-[#8b5cf6] to-[#A855F7] flex items-center justify-center"><Atom size={18} className="text-white" /></div>
            <span className="font-bold text-lg">jas<span className="text-[#8b5cf6]">Science</span></span>
          </div>
          {/* Tab Switcher */}
          <div className="flex gap-1 p-1 bg-[var(--input)] rounded-[var(--radius-md)] border border-[var(--border)] mb-6 relative">
            <div className="absolute top-1 left-1 w-[calc(50%-4px)] h-[calc(100%-8px)] bg-gradient-to-br from-[#8b5cf6]/80 to-[#A855F7]/80 rounded-[10px] shadow-[0_4px_15px_rgba(139,92,246,.4)] transition-transform duration-300" style={{ transform: mode === 'register' ? 'translateX(100%)' : 'translateX(0)' }} />
            <button type="button" onClick={() => switchMode('login')} className={`flex-1 py-3 rounded-[10px] text-sm font-semibold transition-all relative z-10 ${mode === 'login' ? 'text-white' : 'text-[var(--muted-foreground)]'}`}>{tn('login')}</button>
            <button type="button" onClick={() => switchMode('register')} className={`flex-1 py-3 rounded-[10px] text-sm font-semibold transition-all relative z-10 ${mode === 'register' ? 'text-white' : 'text-[var(--muted-foreground)]'}`}>{tn('register')}</button>
          </div>

          {/* API Error */}
          {error && (
            <div className="flex items-start gap-2 p-3 mb-4 bg-[#F43F5E]/10 border border-[#F43F5E]/30 rounded-[var(--radius-sm)] text-sm text-[#F43F5E]">
              <AlertCircle size={16} className="flex-shrink-0 mt-0.5" />
              <span>{error}</span>
              <button onClick={clearError} className="ml-auto flex-shrink-0 text-[#F43F5E]/60 hover:text-[#F43F5E]"><XCircle size={14} /></button>
            </div>
          )}

          {mode === 'login' ? (
            <form className="space-y-5" onSubmit={handleSubmit}>
              <div>
                <label className="block text-[13px] font-medium text-[var(--muted-foreground)] mb-2">{t('email')}</label>
                <div className="relative">
                  <input type="text" value={form.email} onChange={updateField('email')} className={`w-full bg-[var(--input)] border rounded-[var(--radius-md)] px-4 py-3.5 pl-11 text-sm text-[var(--foreground)] outline-none transition-all ${fieldErrors.email ? 'border-[#F43F5E]' : 'border-[var(--border)] focus:border-[#8B5CF6]'}`} placeholder="username or researcher@email.com" autoComplete="username" />
                  <Mail size={16} className="absolute left-4 top-1/2 -translate-y-1/2 text-[var(--dim)]" />
                </div>
                {fieldErrors.email && <p className="text-[#F43F5E] text-xs mt-1.5">{fieldErrors.email}</p>}
              </div>
              <div>
                <label className="block text-[13px] font-medium text-[var(--muted-foreground)] mb-2">{t('password')}</label>
                <div className="relative">
                  <input type={showPassword ? 'text' : 'password'} value={form.password} onChange={updateField('password')} className={`w-full bg-[var(--input)] border rounded-[var(--radius-md)] px-4 py-3.5 pl-11 pr-11 text-sm text-[var(--foreground)] outline-none transition-all ${fieldErrors.password ? 'border-[#F43F5E]' : 'border-[var(--border)] focus:border-[#8B5CF6]'}`} placeholder="••••••••" autoComplete="current-password" />
                  <Lock size={16} className="absolute left-4 top-1/2 -translate-y-1/2 text-[var(--dim)]" />
                  <button type="button" onClick={() => setShowPassword(!showPassword)} className="absolute right-4 top-1/2 -translate-y-1/2 text-[var(--dim)] hover:text-[var(--foreground)] transition-colors">{showPassword ? <EyeOff size={16} /> : <Eye size={16} />}</button>
                </div>
                {fieldErrors.password && <p className="text-[#F43F5E] text-xs mt-1.5">{fieldErrors.password}</p>}
              </div>
              <button type="submit" disabled={isLoading} className="w-full py-3.5 bg-gradient-to-br from-[#8b5cf6] to-[#A855F7] text-white rounded-[var(--radius-md)] text-[15px] font-semibold shadow-[0_10px_25px_rgba(139,92,246,.4)] hover:shadow-[0_15px_35px_rgba(139,92,246,.6)] hover:-translate-y-0.5 transition-all flex items-center justify-center gap-2 disabled:opacity-60 disabled:cursor-not-allowed disabled:hover:translate-y-0">
                {isLoading ? <Loader2 size={16} className="animate-spin" /> : <ArrowRight size={16} />}
                {isLoading ? 'Signing in...' : t('signInButton')}
              </button>
              <div className="relative my-4"><div className="absolute inset-0 flex items-center"><div className="w-full border-t border-[var(--border)]" /></div><div className="relative flex justify-center text-xs"><span className="bg-[var(--card)] px-3 text-[var(--dim)]">{t('orContinueWith')}</span></div></div>
              <div className="grid grid-cols-3 gap-3">
                {['ORCID', 'GitHub', 'Google'].map((p) => (
                  <button key={p} type="button" disabled title="Coming soon" className="py-2.5 px-4 bg-[var(--input)] border border-[var(--border)] rounded-[var(--radius-md)] text-sm text-[var(--foreground)] opacity-50 cursor-not-allowed">{p}</button>
                ))}
              </div>
              <p className="text-center text-sm text-[var(--muted-foreground)] mt-4">{t('noAccount')} <button type="button" onClick={() => switchMode('register')} className="text-[#C084FC] hover:text-[var(--foreground)] font-semibold bg-transparent border-none cursor-pointer">{tn('register')}</button></p>
            </form>
          ) : (
            <form className="space-y-4" onSubmit={handleSubmit}>
              <div>
                <label className="block text-[13px] font-medium text-[var(--muted-foreground)] mb-2">Username</label>
                <div className="relative">
                  <input type="text" value={form.username} onChange={updateField('username')} className={`w-full bg-[var(--input)] border rounded-[var(--radius-md)] px-4 py-3 pl-11 text-sm text-[var(--foreground)] outline-none transition-all ${fieldErrors.username ? 'border-[#F43F5E]' : 'border-[var(--border)] focus:border-[#8B5CF6]'}`} placeholder="researcher" autoComplete="username" />
                  <User size={16} className="absolute left-4 top-1/2 -translate-y-1/2 text-[var(--dim)]" />
                </div>
                {fieldErrors.username && <p className="text-[#F43F5E] text-xs mt-1.5">{fieldErrors.username}</p>}
              </div>
              <div>
                <label className="block text-[13px] font-medium text-[var(--muted-foreground)] mb-2">{t('email')}</label>
                <div className="relative">
                  <input type="email" value={form.email} onChange={updateField('email')} className={`w-full bg-[var(--input)] border rounded-[var(--radius-md)] px-4 py-3 pl-11 text-sm text-[var(--foreground)] outline-none transition-all ${fieldErrors.email ? 'border-[#F43F5E]' : 'border-[var(--border)] focus:border-[#8B5CF6]'}`} placeholder="researcher@email.com" autoComplete="email" />
                  <Mail size={16} className="absolute left-4 top-1/2 -translate-y-1/2 text-[var(--dim)]" />
                </div>
                {fieldErrors.email && <p className="text-[#F43F5E] text-xs mt-1.5">{fieldErrors.email}</p>}
              </div>
              <div>
                <label className="block text-[13px] font-medium text-[var(--muted-foreground)] mb-2">{t('password')}</label>
                <div className="relative">
                  <input type={showPassword ? 'text' : 'password'} value={form.password} onChange={updateField('password')} className={`w-full bg-[var(--input)] border rounded-[var(--radius-md)] px-4 py-3 pl-11 pr-11 text-sm text-[var(--foreground)] outline-none transition-all ${fieldErrors.password ? 'border-[#F43F5E]' : 'border-[var(--border)] focus:border-[#8B5CF6]'}`} placeholder="Min. 8 characters" autoComplete="new-password" />
                  <Lock size={16} className="absolute left-4 top-1/2 -translate-y-1/2 text-[var(--dim)]" />
                  <button type="button" onClick={() => setShowPassword(!showPassword)} className="absolute right-4 top-1/2 -translate-y-1/2 text-[var(--dim)] hover:text-[var(--foreground)]">{showPassword ? <EyeOff size={16} /> : <Eye size={16} />}</button>
                </div>
                {fieldErrors.password && <p className="text-[#F43F5E] text-xs mt-1.5">{fieldErrors.password}</p>}
              </div>
              <div>
                <label className="block text-[13px] font-medium text-[var(--muted-foreground)] mb-2">Confirm Password</label>
                <div className="relative">
                  <input type="password" value={form.confirmPassword} onChange={updateField('confirmPassword')} className={`w-full bg-[var(--input)] border rounded-[var(--radius-md)] px-4 py-3 pl-11 text-sm text-[var(--foreground)] outline-none transition-all ${fieldErrors.confirmPassword ? 'border-[#F43F5E]' : 'border-[var(--border)] focus:border-[#8B5CF6]'}`} placeholder="Repeat password" autoComplete="new-password" />
                  <Lock size={16} className="absolute left-4 top-1/2 -translate-y-1/2 text-[var(--dim)]" />
                </div>
                {fieldErrors.confirmPassword && <p className="text-[#F43F5E] text-xs mt-1.5">{fieldErrors.confirmPassword}</p>}
              </div>
              <button type="submit" disabled={isLoading} className="w-full py-3.5 bg-gradient-to-br from-[#8b5cf6] to-[#A855F7] text-white rounded-[var(--radius-md)] text-[15px] font-semibold shadow-[0_10px_25px_rgba(139,92,246,.4)] hover:shadow-[0_15px_35px_rgba(139,92,246,.6)] hover:-translate-y-0.5 transition-all flex items-center justify-center gap-2 disabled:opacity-60 disabled:cursor-not-allowed disabled:hover:translate-y-0">
                {isLoading ? <Loader2 size={16} className="animate-spin" /> : <ArrowRight size={16} />}
                {isLoading ? 'Creating account...' : t('createAccountButton')}
              </button>
              <p className="text-center text-xs text-[var(--dim)] mt-3">By registering, you agree to our <Link href={`/${locale}/terms`} className="text-[var(--muted-foreground)] underline">Terms</Link>.</p>
              <p className="text-center text-sm text-[var(--muted-foreground)] mt-2">{t('hasAccount')} <button type="button" onClick={() => switchMode('login')} className="text-[#C084FC] hover:text-[var(--foreground)] font-semibold bg-transparent border-none cursor-pointer">{tn('login')}</button></p>
            </form>
          )}
        </div>
      </div>
    </div>
  );
}
