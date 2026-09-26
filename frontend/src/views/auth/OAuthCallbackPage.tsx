'use client';

import { useEffect, useState, useRef, useCallback } from 'react';
import { useSearchParams, useRouter } from 'next/navigation';
import Link from 'next/link';
import { useTranslations } from 'next-intl';
import { Loader2, AlertCircle, ArrowLeft, RotateCw } from 'lucide-react';
import ScienceBackground, { BackgroundGlow } from '@/shared/ui/ScienceBackground';
import { authApi } from '@/entities/auth/api/auth.api';
import { useAuthStore } from '@/stores/auth.store';
import { errorMessage } from '@/shared/utils/errorMessage';

export default function OAuthCallbackPage({ locale = 'ru' }: { locale?: string }) {
  const t = useTranslations('auth');
  const tn = useTranslations('common');
  const searchParams = useSearchParams();
  const router = useRouter();
  const fetchUser = useAuthStore((s) => s.fetchUser);

  const [status, setStatus] = useState<'loading' | 'error'>('loading');
  const [errorDetails, setErrorDetails] = useState<string | null>(null);
  const attemptedRef = useRef(false);

  const errorParam = searchParams.get('error');

  const processCallback = useCallback(async () => {
    setStatus('loading');
    setErrorDetails(null);

    if (errorParam) {
      setStatus('error');
      if (errorParam === 'access_denied') {
        setErrorDetails('Access was denied or cancelled by user.');
      } else {
        setErrorDetails(errorParam);
      }
      return;
    }

    try {
      await authApi.refresh();
      await fetchUser();
      router.replace(`/${locale}/dashboards`);
    } catch (err: unknown) {
      setStatus('error');
      setErrorDetails(errorMessage(err, 'Failed to complete OAuth authentication.'));
    }
  }, [errorParam, fetchUser, locale, router]);

  useEffect(() => {
    if (attemptedRef.current) return;
    attemptedRef.current = true;
    void processCallback();
  }, [processCallback]);

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
          {status === 'loading' ? (
            <div className="text-center py-6">
              <div className="w-16 h-16 mx-auto mb-5 rounded-full bg-[#8B5CF6]/10 border border-[#8B5CF6]/30 flex items-center justify-center">
                <Loader2 size={32} className="animate-spin text-[#8B5CF6]" />
              </div>
              <h2 className="text-xl font-bold text-[var(--foreground)] mb-2">{t('oauthCallbackTitle')}</h2>
              <p className="text-sm text-[var(--muted-foreground)]">{t('oauthCallbackDesc')}</p>
            </div>
          ) : (
            <div className="text-center py-4">
              <div className="w-16 h-16 mx-auto mb-5 rounded-full bg-[#F43F5E]/10 border border-[#F43F5E]/30 flex items-center justify-center text-[#F43F5E]">
                <AlertCircle size={36} />
              </div>
              <h2 className="text-2xl font-bold text-[var(--foreground)] mb-2">{t('oauthErrorTitle')}</h2>
              <p className="text-sm text-[var(--muted-foreground)] mb-6">
                {t('oauthErrorDesc', { error: errorDetails || 'Unknown error' })}
              </p>

              <div className="flex flex-col gap-3">
                <button
                  type="button"
                  onClick={() => {
                    attemptedRef.current = false;
                    void processCallback();
                  }}
                  className="w-full py-3 px-4 rounded-[var(--radius-md)] text-sm font-semibold border border-[var(--border)] bg-[var(--input)] text-[var(--foreground)] hover:border-[#8B5CF6] transition-all flex items-center justify-center gap-2 cursor-pointer"
                >
                  <RotateCw size={14} />
                  <span>{tn('retry')}</span>
                </button>
                <Link
                  href={`/${locale}/auth`}
                  className="w-full py-3 px-4 bg-gradient-to-br from-[#8B5CF6] to-[#A855F7] text-white rounded-[var(--radius-md)] text-sm font-semibold shadow-[0_4px_15px_rgba(139,92,246,0.3)] hover:shadow-[0_8px_25px_rgba(139,92,246,0.5)] transition-all flex items-center justify-center gap-2 no-underline"
                >
                  <ArrowLeft size={16} />
                  <span>{t('signInButton')}</span>
                </Link>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}