import UserLayout from '@/components/layout/UserLayout';

export default function UserRouteLayout({ children }: { children: React.ReactNode }) {
  return <UserLayout>{children}</UserLayout>;
}
