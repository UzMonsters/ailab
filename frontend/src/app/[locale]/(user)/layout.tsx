import UserLayout from '@/widgets/layout/UserLayout';

export default function UserRouteLayout({ children }: { children: React.ReactNode }) {
  return <UserLayout>{children}</UserLayout>;
}
