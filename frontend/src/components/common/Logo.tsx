import { Atom } from 'lucide-react';

interface LogoProps {
  className?: string;
}

export default function Logo({ className }: LogoProps) {
  return (
    <a className={`logo ${className || ''}`} href="#home" aria-label="jasScience home">
      <span className="logo-symbol"><Atom /></span>
      <span>jas<span className="text-[#8b5cf6]">Core</span></span>
    </a>
  );
}
