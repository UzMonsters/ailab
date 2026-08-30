import AdminLayout from '@/widgets/layout/AdminLayout';
import './admin.css';

export default function AdminRouteLayout({ children }: { children: React.ReactNode }) {
  return <AdminLayout>{children}</AdminLayout>;
}
