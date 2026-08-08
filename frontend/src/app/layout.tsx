import type { Metadata } from 'next';
import './globals.css';

export const metadata: Metadata = {
  title: 'AI Laboratory — The Scientific OS',
  description: 'AI-powered virtual laboratory platform for scientific research and education.',
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return children;
}
