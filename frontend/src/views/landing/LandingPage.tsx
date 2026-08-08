'use client';
import PublicHeader from '@/components/layout/PublicHeader';
import PublicFooter from '@/components/layout/PublicFooter';
import HeroSection from './sections/HeroSection';
import PlatformSection from './sections/PlatformSection';
import SciencesSection from './sections/SciencesSection';
import SandboxSection from './sections/SandboxSection';
import AIAssistantSection from './sections/AIAssistantSection';
import StatsSection from './sections/StatsSection';

export default function LandingPage() {
  return (
    <main className="site-shell" id="home">
      <PublicHeader />
      <HeroSection />
      <PlatformSection />
      <SciencesSection />
      <SandboxSection />
      <AIAssistantSection />
      <StatsSection />
      <PublicFooter />
    </main>
  );
}
