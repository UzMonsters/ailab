'use client';
import Link from 'next/link';
import { useTranslations } from 'next-intl';
import { usePathname, useRouter } from 'next/navigation';
import { useEffect, useState } from 'react';
import {
  Atom, FlaskConical, Brain, Users, Mail, Lock, User,
  Eye, EyeOff, ArrowRight, Loader2, AlertCircle, XCircle, CheckCircle2,
} from 'lucide-react';
import { useAuthStore } from '@/stores/auth.store';
import { getApiBaseUrl, ApiError } from '@/shared/api/client';
import ScienceBackground, { BackgroundGlow } from '@/shared/ui/ScienceBackground';

interface FieldErrors {
  username?: string;
  email?: string;
  password?: string;
  confirmPassword?: string;
}

function GoogleIcon({ className }: { className?: string }) {
  return (
    <svg className={className} viewBox="0 0 24 24" fill="none">
      <path d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z" fill="#4285F4" />
      <path d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z" fill="#34A853" />
      <path d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.06H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.94l2.85-2.22.81-.63z" fill="#FBBC05" />
      <path d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.06l3.66 2.84c.87-2.6 3.3-4.52 6.16-4.52z" fill="#EA4335" />
    </svg>
  );
}

function GithubIcon({ className }: { className?: string }) {
  return (
    <svg className={className} viewBox="0 0 24 24" fill="currentColor">
      <path fillRule="evenodd" clipRule="evenodd" d="M12 2C6.477 2 2 6.484 2 12.017c0 4.425 2.865 8.18 6.839 9.504.5.092.682-.217.682-.483 0-.237-.008-.868-.013-1.703-2.782.605-3.369-1.343-3.369-1.343-.454-1.158-1.11-1.466-1.11-1.466-.908-.62.069-.608.069-.608 1.003.07 1.53 1.032 1.53 1.032.892 1.53 2.341 1.088 2.91.832.092-.647.35-1.088.636-1.338-2.22-.253-4.555-1.113-4.555-4.951 0-1.093.39-1.988 1.029-2.688-.103-.253-.446-1.272.098-2.65 0 0 .84-.27 2.75 1.026A9.564 9.564 0 0112 6.844c.85.004 1.705.115 2.504.337 1.909-1.296 2.747-1.027 2.747-1.027.546 1.379.202 2.398.1 2.651.64.7 1.028 1.595 1.028 2.688 0 3.848-2.339 4.695-4.566 4.943.359.309.678.92.678 1.855 0 1.338-.012 2.419-.012 2.747 0 .268.18.58.688.482A10.019 10.019 0 0022 12.017C22 6.484 17.522 2 12 2z" />
    </svg>
  );
}

export default function AuthPage() {
  const t = useTranslations('auth');
  const tn = useTranslations('common');
  const pathname = usePathname();
  const router = useRouter();
  const locale = pathname.split('/')[1] || 'en';
  const {
    isLoading,
    error,
    clearError,
    login,
    register,
    isAuthenticated,
    verificationEmail,
    unverifiedEmail,
    resendVerification,
    setVerificationEmail,
    clearUnverifiedEmail,
  } = useAuthStore();

  const [mode, setMode] = useState<'login' | 'register'>('login');
  const [showPassword, setShowPassword] = useState(false);

  const [form, setForm] = useState({ username: '', email: '', password: '', confirmPassword: '' });
  const [fieldErrors, setFieldErrors] = useState<FieldErrors>({});

  const [resendCountdown, setResendCountdown] = useState(0);
  const [resending, setResending] = useState(false);
  const [resendSuccess, setResendSuccess] = useState(false);
  const [resendError, setResendError] = useState<string | null>(null);
  const [isSlowLoading, setIsSlowLoading] = useState(false);

  useEffect(() => {
    if (!isLoading) return;
    const timer = setTimeout(() => setIsSlowLoading(true), 3500);
    return () => {
      clearTimeout(timer);
      setIsSlowLoading(false);
    };
  }, [isLoading]);

  useEffect(() => {
    if (resendCountdown <= 0) return;
    const interval = setInterval(() => {
      setResendCountdown((prev) => (prev > 0 ? prev - 1 : 0));
    }, 1000);
    return () => clearInterval(interval);
  }, [resendCountdown]);

  const handleResend = async (targetEmail: string) => {
    if (resendCountdown > 0 || resending) return;
    setResending(true);
    setResendSuccess(false);
    setResendError(null);
    try {
      await resendVerification(targetEmail);
      setResendSuccess(true);
      setResendCountdown(60);
    } catch (err: unknown) {
      if (err instanceof ApiError && err.status === 429) {
        setResendCountdown(60);
        setResendError('Too many requests. Please wait before requesting another email.');
      } else {
        setResendError(err instanceof Error ? err.message : 'Failed to resend verification email');
      }
    } finally {
      setResending(false);
    }
  };

  const handleGoogleLogin = () => {
    const apiBase = getApiBaseUrl();
    window.location.href = `${apiBase}/oauth2/authorization/google`;
  };

  const handleGithubLogin = () => {
    const apiBase = getApiBaseUrl();
    window.location.href = `${apiBase}/oauth2/authorization/github`;
  };

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
    clearUnverifiedEmail();
    if (mode === 'login') {
      if (!validateLogin()) return;
      await login(form.email.trim(), form.password);
    } else {
      if (!validateRegister()) return;
      await register(form.username.trim(), form.email.trim(), form.password);
    }
  };

  useEffect(() => {
    if (isAuthenticated) router.replace(`/${locale}/dashboard`);
  }, [isAuthenticated, locale, router]);

  const updateField = (field: string) => (e: React.ChangeEvent<HTMLInputElement>) => {
    setForm((p) => ({ ...p, [field]: e.target.value }));
    if (fieldErrors[field as keyof FieldErrors]) {
      setFieldErrors((p) => ({ ...p, [field]: undefined }));
    }
  };

  const switchMode = (m: 'login' | 'register') => {
    setMode(m);
    clearError();
    clearUnverifiedEmail();
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

          {/* VERIFICATION CHALLENGE VIEW */}
          {verificationEmail ? (
            <div className="text-center py-6 px-4 animate-in fade-in zoom-in-95 duration-300">
              <div className="w-16 h-16 mx-auto mb-6 rounded-2xl bg-gradient-to-br from-[#8b5cf6]/20 to-[#A855F7]/20 border border-[#8b5cf6]/40 flex items-center justify-center text-[#A855F7] shadow-[0_0_30px_rgba(139,92,246,0.3)]">
                <Mail size={32} />
              </div>
              <h2 className="text-2xl font-bold text-[var(--foreground)] mb-3">
                {t('verificationNoticeTitle')}
              </h2>
              <p className="text-[var(--muted-foreground)] text-sm leading-relaxed max-w-sm mx-auto mb-6">
                {t('verificationNoticeDesc', { email: verificationEmail })}
              </p>

              {resendSuccess && (
                <div className="p-3 mb-5 bg-[#10B981]/10 border border-[#10B981]/30 rounded-[var(--radius-sm)] text-sm text-[#10B981] flex items-center justify-center gap-2">
                  <CheckCircle2 size={16} />
                  <span>{t('resendSuccess')}</span>
                </div>
              )}

              {resendError && (
                <div className="p-3 mb-5 bg-[#F43F5E]/10 border border-[#F43F5E]/30 rounded-[var(--radius-sm)] text-sm text-[#F43F5E] flex items-center justify-center gap-2">
                  <AlertCircle size={16} />
                  <span>{resendError}</span>
                </div>
              )}

              <div className="space-y-3 max-w-xs mx-auto">
                <button
                  type="button"
                  onClick={() => handleResend(verificationEmail)}
                  disabled={resendCountdown > 0 || resending}
                  className="w-full py-3 px-4 bg-[var(--input)] border border-[var(--border)] rounded-[var(--radius-md)] text-sm font-semibold text-[var(--foreground)] hover:border-[#8B5CF6] transition-all disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
                >
                  {resending && <Loader2 size={14} className="animate-spin" />}
                  {resendCountdown > 0
                    ? t('resendCooldown', { seconds: resendCountdown })
                    : t('resendVerification')}
                </button>
                <button
                  type="button"
                  onClick={() => {
                    setVerificationEmail(null);
                    switchMode('login');
                  }}
                  className="w-full py-3 px-4 bg-gradient-to-br from-[#8b5cf6] to-[#A855F7] text-white rounded-[var(--radius-md)] text-sm font-semibold shadow-[0_4px_15px_rgba(139,92,246,0.3)] hover:shadow-[0_8px_25px_rgba(139,92,246,0.5)] transition-all cursor-pointer"
                >
                  {tn('login')}
                </button>
              </div>
            </div>
          ) : (
            <>
              {/* Tab Switcher */}
              <div className="flex gap-1 p-1 bg-[var(--input)] rounded-[var(--radius-md)] border border-[var(--border)] mb-6 relative">
                <div className="absolute top-1 left-1 w-[calc(50%-4px)] h-[calc(100%-8px)] bg-gradient-to-br from-[#8b5cf6]/80 to-[#A855F7]/80 rounded-[10px] shadow-[0_4px_15px_rgba(139,92,246,.4)] transition-transform duration-300" style={{ transform: mode === 'register' ? 'translateX(100%)' : 'translateX(0)' }} />
                <button type="button" onClick={() => switchMode('login')} className={`flex-1 py-3 rounded-[10px] text-sm font-semibold transition-all relative z-10 cursor-pointer ${mode === 'login' ? 'text-white' : 'text-[var(--muted-foreground)]'}`}>{tn('login')}</button>
                <button type="button" onClick={() => switchMode('register')} className={`flex-1 py-3 rounded-[10px] text-sm font-semibold transition-all relative z-10 cursor-pointer ${mode === 'register' ? 'text-white' : 'text-[var(--muted-foreground)]'}`}>{tn('register')}</button>
              </div>

              {/* Unverified Email Warning Banner */}
              {unverifiedEmail && (
                <div className="p-4 mb-5 bg-[#F59E0B]/10 border border-[#F59E0B]/30 rounded-[var(--radius-md)] text-sm text-[var(--foreground)] animate-in fade-in">
                  <div className="flex items-start gap-2.5">
                    <Mail size={18} className="text-[#F59E0B] flex-shrink-0 mt-0.5" />
                    <div className="flex-1">
                      <p className="font-medium text-[#F59E0B] mb-1">{t('emailNotVerified')}</p>
                      {resendSuccess && <p className="text-xs text-[#10B981] mt-1">{t('resendSuccess')}</p>}
                      {resendError && <p className="text-xs text-[#F43F5E] mt-1">{resendError}</p>}
                      <button
                        type="button"
                        onClick={() => handleResend(unverifiedEmail)}
                        disabled={resendCountdown > 0 || resending}
                        className="mt-2 text-xs font-semibold text-[#8B5CF6] hover:underline flex items-center gap-1.5 disabled:opacity-50 cursor-pointer"
                      >
                        {resending && <Loader2 size={12} className="animate-spin" />}
                        {resendCountdown > 0 ? t('resendCooldown', { seconds: resendCountdown }) : t('resendVerification')}
                      </button>
                    </div>
                  </div>
                </div>
              )}

              {/* API Error (excluding EMAIL_NOT_VERIFIED which is rendered above) */}
              {error && error !== 'EMAIL_NOT_VERIFIED' && (
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
                      <button type="button" onClick={() => setShowPassword(!showPassword)} className="absolute right-4 top-1/2 -translate-y-1/2 text-[var(--dim)] hover:text-[var(--foreground)] transition-colors cursor-pointer">{showPassword ? <EyeOff size={16} /> : <Eye size={16} />}</button>
                    </div>
                    {fieldErrors.password && <p className="text-[#F43F5E] text-xs mt-1.5">{fieldErrors.password}</p>}
                  </div>
                  <div>
                    <button type="submit" disabled={isLoading} className="w-full py-3.5 bg-gradient-to-br from-[#8b5cf6] to-[#A855F7] text-white rounded-[var(--radius-md)] text-[15px] font-semibold shadow-[0_10px_25px_rgba(139,92,246,.4)] hover:shadow-[0_15px_35px_rgba(139,92,246,.6)] hover:-translate-y-0.5 transition-all flex items-center justify-center gap-2 disabled:opacity-60 disabled:cursor-not-allowed disabled:hover:translate-y-0 cursor-pointer">
                      {isLoading ? <Loader2 size={16} className="animate-spin" /> : <ArrowRight size={16} />}
                      {isLoading ? 'Signing in...' : t('signInButton')}
                    </button>
                    {isSlowLoading && (
                      <p className="text-xs text-[#A78BFA] text-center mt-2 animate-pulse">
                        {t('serverWakingUp')}
                      </p>
                    )}
                  </div>
                  <div className="relative my-4"><div className="absolute inset-0 flex items-center"><div className="w-full border-t border-[var(--border)]" /></div><div className="relative flex justify-center text-xs"><span className="bg-[var(--card)] px-3 text-[var(--dim)]">{t('orContinueWith')}</span></div></div>
                  <div className="grid grid-cols-2 gap-3">
                    <button
                      type="button"
                      onClick={handleGoogleLogin}
                      className="py-3 px-4 bg-[var(--input)] border border-[var(--border)] rounded-[var(--radius-md)] text-sm font-medium text-[var(--foreground)] hover:border-[#8B5CF6] hover:bg-white/[0.04] transition-all flex items-center justify-center gap-2.5 cursor-pointer shadow-sm"
                    >
                      <GoogleIcon className="w-4 h-4 flex-shrink-0" />
                      <span>Google</span>
                    </button>
                    <button
                      type="button"
                      onClick={handleGithubLogin}
                      className="py-3 px-4 bg-[var(--input)] border border-[var(--border)] rounded-[var(--radius-md)] text-sm font-medium text-[var(--foreground)] hover:border-[#8B5CF6] hover:bg-white/[0.04] transition-all flex items-center justify-center gap-2.5 cursor-pointer shadow-sm"
                    >
                      <GithubIcon className="w-4 h-4 flex-shrink-0" />
                      <span>GitHub</span>
                    </button>
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
                      <button type="button" onClick={() => setShowPassword(!showPassword)} className="absolute right-4 top-1/2 -translate-y-1/2 text-[var(--dim)] hover:text-[var(--foreground)] cursor-pointer">{showPassword ? <EyeOff size={16} /> : <Eye size={16} />}</button>
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
                  <div>
                    <button type="submit" disabled={isLoading} className="w-full py-3.5 bg-gradient-to-br from-[#8b5cf6] to-[#A855F7] text-white rounded-[var(--radius-md)] text-[15px] font-semibold shadow-[0_10px_25px_rgba(139,92,246,.4)] hover:shadow-[0_15px_35px_rgba(139,92,246,.6)] hover:-translate-y-0.5 transition-all flex items-center justify-center gap-2 disabled:opacity-60 disabled:cursor-not-allowed disabled:hover:translate-y-0 cursor-pointer">
                      {isLoading ? <Loader2 size={16} className="animate-spin" /> : <ArrowRight size={16} />}
                      {isLoading ? 'Creating account...' : t('createAccountButton')}
                    </button>
                    {isSlowLoading && (
                      <p className="text-xs text-[#A78BFA] text-center mt-2 animate-pulse">
                        {t('serverWakingUp')}
                      </p>
                    )}
                  </div>
                  <div className="relative my-4"><div className="absolute inset-0 flex items-center"><div className="w-full border-t border-[var(--border)]" /></div><div className="relative flex justify-center text-xs"><span className="bg-[var(--card)] px-3 text-[var(--dim)]">{t('orContinueWith')}</span></div></div>
                  <div className="grid grid-cols-2 gap-3">
                    <button
                      type="button"
                      onClick={handleGoogleLogin}
                      className="py-3 px-4 bg-[var(--input)] border border-[var(--border)] rounded-[var(--radius-md)] text-sm font-medium text-[var(--foreground)] hover:border-[#8B5CF6] hover:bg-white/[0.04] transition-all flex items-center justify-center gap-2.5 cursor-pointer shadow-sm"
                    >
                      <GoogleIcon className="w-4 h-4 flex-shrink-0" />
                      <span>Google</span>
                    </button>
                    <button
                      type="button"
                      onClick={handleGithubLogin}
                      className="py-3 px-4 bg-[var(--input)] border border-[var(--border)] rounded-[var(--radius-md)] text-sm font-medium text-[var(--foreground)] hover:border-[#8B5CF6] hover:bg-white/[0.04] transition-all flex items-center justify-center gap-2.5 cursor-pointer shadow-sm"
                    >
                      <GithubIcon className="w-4 h-4 flex-shrink-0" />
                      <span>GitHub</span>
                    </button>
                  </div>
                  <p className="text-center text-xs text-[var(--dim)] mt-3">By registering, you agree to our <Link href={`/${locale}/terms`} className="text-[var(--muted-foreground)] underline">Terms</Link>.</p>
                  <p className="text-center text-sm text-[var(--muted-foreground)] mt-2">{t('hasAccount')} <button type="button" onClick={() => switchMode('login')} className="text-[#C084FC] hover:text-[var(--foreground)] font-semibold bg-transparent border-none cursor-pointer">{tn('login')}</button></p>
                </form>
              )}
            </>
          )}
        </div>
      </div>
    </div>
  );
}
