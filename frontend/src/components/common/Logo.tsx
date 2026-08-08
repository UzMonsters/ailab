import { Atom } from 'lucide-react';

interface LogoProps {
  className?: string;
}

export default function Logo({ className }: LogoProps) {
  return (
    <a className={`logo ${className || ''}`} href="#home" aria-label="AI Laboratory home">
      <span className="logo-symbol"><Atom /></span>
      <span>AI Laboratory</span>
    </a>
  );
}
