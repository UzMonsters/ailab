'use client';
import Link from 'next/link';
import { useTranslations } from 'next-intl';
import { usePathname } from 'next/navigation';
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
          <p>The operating system for scientific simulation. Build, understand, and share what comes next.</p>
          <div className="social-row">
            <button aria-label="Github">GH</button>
            <button aria-label="Discord">DS</button>
            <button aria-label="LinkedIn">in</button>
          </div>
        </div>
        <div>
          <h3>Platform</h3>
          <a href="#workspace">Workspace</a>
          <a href="#sciences">Sciences</a>
          <a href="#molecules">Molecules</a>
        </div>
        <div>
          <h3>Resources</h3>
          <Link href={`/${locale}/about`}>About</Link>
          <Link href={`/${locale}/terms`}>Terms</Link>
        </div>
        <div>
          <h3>Company</h3>
          <Link href={`/${locale}/about`}>About</Link>
          <Link href={`/${locale}/terms`}>Privacy</Link>
        </div>
        <div className="footer-newsletter">
          <h3>Stay in the field</h3>
          <p>New models, discoveries, and platform notes — once a month.</p>
          <div className="newsletter-row">
            <input aria-label="Email address" placeholder="Email address" />
            <button className="button button-primary" aria-label="Subscribe">→</button>
          </div>
        </div>
      </div>
      <div className="copyright section-wrap">© 2026 AI Laboratory. All systems operational.</div>
    </footer>
  );
}
