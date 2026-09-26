'use client';

import { useEffect, useState, useCallback } from 'react';
import { useSearchParams } from 'next/navigation';
import Link from 'next/link';
import { useTranslations } from 'next-intl';
import { CheckCircle2, XCircle, AlertCircle, Loader2, Mail, ArrowRight, RotateCw } from 'lucide-react';
import ScienceBackground, { BackgroundGlow } from '@/shared/ui/ScienceBackground';
import { authApi } from '@/entities/auth/api/auth.api';
import { ApiError } from '@/shared/api/client';
import { errorMessage } from '@/shared/utils/errorMessage';

type VerifyStatus = 'loading' | 'success' | 'invalid' | 'expired' | 'error';

export default function VerifyEmailPage({ locale = 'ru' }: { locale?: string }) {
  const t = useTranslations('auth');
  const tn = useTranslations('common');
  const searchParams = useSearchParams();
  const token = searchParams.get('token');

  const [status, setStatus] = useState<VerifyStatus>(token ? 'loading' : 'invalid');
  const [errorMsg, setErrorMsg] = useState<string>(token ? '' : t('verifyErrorDesc'));
  const [resendEmail, setResendEmail] = useState('');
  const [resending, setResending] = useState(false);
  const [resendSuccess, setResendSuccess] = useState(false);
  const [resendCountdown, setResendCountdown] = useState(0);
  const [resendError, setResendError] = useState<string | null>(null);

  useEffect(() => {
    if (resendCountdown <= 0) return;
    const interval = setInterval(() => {
      setResendCountdown((prev) => (prev > 1 ? prev - 1 : 0));
    }, 1000);
    return () => clearInterval(interval);
  }, [resendCountdown]);

  const runVerification = useCallback(async (tok: string) => {
    setStatus('loading');
    setErrorMsg('');
    try {
      await authApi.verifyEmail(tok);
      setStatus('success');
    } catch (e: unknown) {
      if (e instanceof ApiError) {
        if (e.status === 410 || e.code === 'TOKEN_EXPIRED') {
          setStatus('expired');
          setErrorMsg(e.message || t('verifyErrorDesc'));
        } else if (e.status === 400 || e.code === 'INVALID_TOKEN') {
          setStatus('invalid');
          setErrorMsg(e.message || t('verifyErrorDesc'));
        } else {
          setStatus('error');
          setErrorMsg(errorMessage(e, t('verifyErrorDesc')));
        }
      } else {
        setStatus('error');
        setErrorMsg(errorMessage(e, t('verifyErrorDesc')));
      }
    }
  }, [t]);

  useEffect(() => {
    if (!token) return;
    let cancelled = false;

    authApi.verifyEmail(token)
      .then(() => {
        if (!cancelled) setStatus('success');
      })
      .catch((e: unknown) => {
        if (cancelled) return;
        if (e instanceof ApiError) {
          if (e.status === 410 || e.code === 'TOKEN_EXPIRED') {
            setStatus('expired');
            setErrorMsg(e.message || t('verifyErrorDesc'));
          } else if (e.status === 400 || e.code === 'INVALID_TOKEN') {
            setStatus('invalid');
            setErrorMsg(e.message || t('verifyErrorDesc'));
          } else {
            setStatus('error');
            setErrorMsg(errorMessage(e, t('verifyErrorDesc')));
          }
        } else {
          setStatus('error');
          setErrorMsg(errorMessage(e, t('verifyErrorDesc')));
        }
      });

    return () => {
      cancelled = true;
    };
  }, [token, t]);

  const handleResend = async (e?: React.FormEvent) => {
    if (e) e.preventDefault();
    if (!resendEmail || resending || resendCountdown > 0) return;

    setResending(true);
    setResendError(null);
    setResendSuccess(false);

    try {
      await authApi.resendVerification(resendEmail.trim().toLowerCase());
      setResendSuccess(true);
      setResendCountdown(60);
    } catch (err: unknown) {
      setResendError(errorMessage(err, 'Failed to resend verification email'));
    } finally {
      setResending(false);
    }
  };

  return (
    <div className="relative min-h-screen flex items-center justify-center p-4" style={{ backgroundColor: 'var(--background)' }}>
      <BackgroundGlow />
      <ScienceBackground />

      <div className="relative z-10 w-full max-w-md">
        <div className="text-center mb-8">
          <Link href={`/${locale}`} className="inline-flex items-center gap-3 no-underline group mb-4">
            <div className="w-12 h-12 rounded-2xl bg-gradient-to-br from-[#8B5CF6] to-[#A855F7] flex items-center justify-center text-white shadow-[0_8px_25px_rgba(139,92,246,0.35)] group-hover:shadow-[0_12px_30px_rgba(139,92,246,0.5)] transition-all">
              <span className="font-bold text-xl tracking-tight">jas</span>
            </div>
            <div className="text-left">
              <span className="text-xl font-bold tracking-tight block text-[var(--foreground)]">jasScience</span>
              <span className="text-[11px] font-mono text-[#8B5CF6] tracking-wider uppercase block">Scientific OS</span>
            </div>
          </Link>
        </div>

        <div className="p-8 rounded-[var(--radius-lg)] border border-[var(--border)] bg-[var(--card)]/90 backdrop-blur-xl shadow-[0_20px_50px_rgba(0,0,0,0.2)]">
          {status === 'loading' && (
            <div className="text-center py-6">
              <div className="w-16 h-16 mx-auto mb-5 rounded-full bg-[#8B5CF6]/10 border border-[#8B5CF6]/30 flex items-center justify-center">
                <Loader2 size={32} className="animate-spin text-[#8B5CF6]" />
              </div>
              <h2 className="text-xl font-bold text-[var(--foreground)] mb-2">{t('verifyingEmail')}</h2>
              <p className="text-sm text-[var(--muted-foreground)]">{t('oauthCallbackDesc')}</p>
            </div>
          )}

          {status === 'success' && (
            <div className="text-center py-4">
              <div className="w-16 h-16 mx-auto mb-5 rounded-full bg-[#10B981]/10 border border-[#10B981]/30 flex items-center justify-center text-[#10B981]">
                <CheckCircle2 size={36} />
              </div>
              <h2 className="text-2xl font-bold text-[var(--foreground)] mb-2">{t('verifySuccessTitle')}</h2>
              <p className="text-sm text-[var(--muted-foreground)] mb-6">{t('verifySuccessDesc')}</p>
              <Link
                href={`/${locale}/auth`}
                className="w-full py-3.5 px-4 bg-gradient-to-br from-[#8B5CF6] to-[#A855F7] text-white rounded-[var(--radius-md)] text-sm font-semibold shadow-[0_10px_25px_rgba(139,92,246,.4)] hover:shadow-[0_15px_35px_rgba(139,92,246,.6)] hover:-translate-y-0.5 transition-all flex items-center justify-center gap-2 no-underline"
              >
                <span>{t('signInButton')}</span>
                <ArrowRight size={16} />
              </Link>
            </div>
          )}

          {(status === 'expired' || status === 'invalid' || status === 'error') && (
            <div className="text-center py-2">
              <div className="w-16 h-16 mx-auto mb-5 rounded-full bg-[#F43F5E]/10 border border-[#F43F5E]/30 flex items-center justify-center text-[#F43F5E]">
                {status === 'expired' ? <AlertCircle size={36} /> : <XCircle size={36} />}
              </div>
              <h2 className="text-xl font-bold text-[var(--foreground)] mb-2">{t('verifyErrorTitle')}</h2>
              <p className="text-sm text-[var(--muted-foreground)] mb-6">
                {errorMsg || t('verifyErrorDesc')}
              </p>

              {/* Resend form */}
              <div className="text-left mb-6 p-4 rounded-[var(--radius-md)] bg-[var(--input)]/50 border border-[var(--border)]">
                <label className="block text-xs font-semibold text-[var(--foreground)] mb-2">
                  {t('resendVerification')}
                </label>
                <form onSubmit={handleResend} className="space-y-3">
                  <div className="relative">
                    <input
                      type="email"
                      value={resendEmail}
                      onChange={(e) => setResendEmail(e.target.value)}
                      placeholder="researcher@email.com"
                      className="w-full bg-[var(--card)] border border-[var(--border)] rounded-[var(--radius-md)] px-3.5 py-2.5 pl-10 text-sm text-[var(--foreground)] outline-none focus:border-[#8B5CF6] transition-all"
                      required
                    />
                    <Mail size={16} className="absolute left-3.5 top-1/2 -translate-y-1/2 text-[var(--dim)]" />
                  </div>

                  {resendSuccess && (
                    <p className="text-xs text-[#10B981] font-medium">{t('resendSuccess')}</p>
                  )}
                  {resendError && (
                    <p className="text-xs text-[#F43F5E]">{resendError}</p>
                  )}

                  <button
                    type="submit"
                    disabled={!resendEmail || resending || resendCountdown > 0}
                    className="w-full py-2.5 px-4 bg-[var(--card)] border border-[var(--border)] rounded-[var(--radius-md)] text-xs font-semibold text-[var(--foreground)] hover:border-[#8B5CF6] transition-all disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2 cursor-pointer"
                  >
                    {resending && <Loader2 size={12} className="animate-spin" />}
                    {resendCountdown > 0
                      ? t('resendCooldown', { seconds: resendCountdown })
                      : t('resendVerification')}
                  </button>
                </form>
              </div>

              <div className="flex flex-col gap-2">
                {token && status === 'error' && (
                  <button
                    type="button"
                    onClick={() => runVerification(token)}
                    className="w-full py-2.5 px-4 rounded-[var(--radius-md)] text-xs font-semibold border border-[var(--border)] bg-[var(--input)] text-[var(--foreground)] hover:border-[#8B5CF6] transition-all flex items-center justify-center gap-2 cursor-pointer"
                  >
                    <RotateCw size={14} />
                    <span>{tn('retry')}</span>
                  </button>
                )}
                <Link
                  href={`/${locale}/auth`}
                  className="w-full py-3 px-4 bg-gradient-to-br from-[#8B5CF6] to-[#A855F7] text-white rounded-[var(--radius-md)] text-sm font-semibold shadow-[0_4px_15px_rgba(139,92,246,0.3)] hover:shadow-[0_8px_25px_rgba(139,92,246,0.5)] transition-all flex items-center justify-center gap-2 no-underline"
                >
                  <span>{t('signInButton')}</span>
                  <ArrowRight size={16} />
                </Link>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}