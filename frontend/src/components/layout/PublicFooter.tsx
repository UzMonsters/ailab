'use client';
import Link from 'next/link';
import { useTranslations } from 'next-intl';
import { usePathname } from 'next/navigation';
import { Globe, MessageSquare, ExternalLink, ArrowRight } from 'lucide-react';
import Logo from '@/components/common/Logo';

export default function PublicFooter() {
  const t = useTranslations('landing');
  const pathname = usePathname();
  const locale = pathname.split('/')[1] || 'en';

  return (
    <footer className="site-footer">
      <div className="footer-grid section-wrap">
        <div className="footer-brand">
          <Logo />
          <p>{t('footerDesc')}</p>
          <div className="social-row">
            <button aria-label="Github"><Globe size={12} /></button>
            <button aria-label="Discord"><MessageSquare size={12} /></button>
            <button aria-label="LinkedIn"><ExternalLink size={12} /></button>
          </div>
        </div>
        <div>
          <h3>{t('footerProduct')}</h3>
          <a href="#workspace">{t('footerWorkspace')}</a>
          <a href="#sciences">{t('footerSciences')}</a>
          <a href="#molecules">{t('footerMolecules')}</a>
        </div>
        <div>
          <h3>{t('footerResources')}</h3>
          <Link href={`/${locale}/about`}>{t('footerAbout')}</Link>
          <Link href={`/${locale}/terms`}>{t('footerTerms')}</Link>
        </div>
        <div>
          <h3>{t('footerCompany')}</h3>
          <Link href={`/${locale}/about`}>{t('footerAbout')}</Link>
          <Link href={`/${locale}/terms`}>{t('footerPrivacy')}</Link>
        </div>
        <div className="footer-newsletter">
          <h3>{t('footerNewsletterTitle')}</h3>
          <p>{t('footerNewsletterDesc')}</p>
          <div className="newsletter-row">
            <input aria-label={t('footerNewsletterPlaceholder')} placeholder={t('footerNewsletterPlaceholder')} />
            <button className="button button-primary" aria-label="Subscribe"><ArrowRight size={14} /></button>
          </div>
        </div>
      </div>
      <div className="copyright section-wrap">{t('footerCopyright')}</div>
    </footer>
  );
}
