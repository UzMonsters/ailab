'use client';
import { useEffect, useState } from 'react';
import { adminApi } from '@/services/api/admin.api';
import type { AdminUserResponse, AdminUpdateUserRequest } from '@/types';
import {
  Plus, Search, MoreVertical, Shield, ShieldCheck, User,
  Trash2, Edit3, CheckCircle, XCircle, Loader2, X, AlertTriangle,
} from 'lucide-react';
import { useTranslations } from 'next-intl';
import { normalizeError } from '@/lib/errors';

export default function AdminUsersPage() {
  const t = useTranslations('admin');
  const [users, setUsers] = useState<AdminUserResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [search, setSearch] = useState('');
  const [roleFilter, setRoleFilter] = useState('all');
  const [statusFilter, setStatusFilter] = useState('all');
  const [addModalOpen, setAddModalOpen] = useState(false);
  const [editModalOpen, setEditModalOpen] = useState(false);
  const [deleteConfirmOpen, setDeleteConfirmOpen] = useState<string | null>(null);
  const [selectedUser, setSelectedUser] = useState<AdminUserResponse | null>(null);
  const [actionLoading, setActionLoading] = useState(false);
  const [toast, setToast] = useState<string | null>(null);
  const [actionMenu, setActionMenu] = useState<string | null>(null);

  const [addForm, setAddForm] = useState<{ username: string; email: string; password: string; role: AdminUserResponse['role'] }>({ username: '', email: '', password: '', role: 'ROLE_USER' });
  const [editForm, setEditForm] = useState<{ username: string; email: string; role: AdminUserResponse['role'] }>({ username: '', email: '', role: 'ROLE_USER' });

  const showToast = (msg: string) => {
    setToast(msg);
    setTimeout(() => setToast(null), 3000);
  };

  const fetchUsers = async () => {
    try {
      setLoading(true);
      const data = await adminApi.getUsers();
      setUsers(data);
      setError(null);
    } catch (err: unknown) {
      setError(normalizeError(err, t('fetchFailed')).message);
    } finally {
      setLoading(false);
    }
  };

  // Initial mock load intentionally hydrates the local admin dataset once.
  // eslint-disable-next-line react-hooks/set-state-in-effect, react-hooks/exhaustive-deps
  useEffect(() => { fetchUsers(); }, []);

  const handleAddUser = async () => {
    if (!addForm.username || !addForm.email || !addForm.password) return;
    try {
      setActionLoading(true);
      await adminApi.createUser(addForm);
      setAddModalOpen(false);
      setAddForm({ username: '', email: '', password: '', role: 'ROLE_USER' });
      await fetchUsers();
      showToast(t('created'));
    } catch (err: unknown) {
      showToast(normalizeError(err, t('createFailed')).message);
    } finally {
      setActionLoading(false);
    }
  };

  const handleUpdateUser = async () => {
    if (!selectedUser) return;
    try {
      setActionLoading(true);
      const updateData: AdminUpdateUserRequest = {
        username: editForm.username || undefined,
        email: editForm.email || undefined,
        role: (editForm.role as AdminUpdateUserRequest['role']) || undefined,
      };
      await adminApi.updateUser(selectedUser.id, updateData);
      setEditModalOpen(false);
      await fetchUsers();
      showToast(t('updated'));
    } catch (err: unknown) {
      showToast(normalizeError(err, t('updateFailed')).message);
    } finally {
      setActionLoading(false);
    }
  };

  const handleDeleteUser = async (_id: string) => {
    const id = deleteConfirmOpen;
    if (!id) return;
    try {
      setActionLoading(true);
      await adminApi.deleteUser(id);
      setDeleteConfirmOpen(null);
      await fetchUsers();
      showToast(t('deleted'));
    } catch (err: unknown) {
      showToast(normalizeError(err, t('deleteFailed')).message);
    } finally {
      setActionLoading(false);
      setActionMenu(null);
    }
  };

  const handleToggleRole = async (user: AdminUserResponse) => {
    try {
      setActionLoading(true);
      const newRole = user.role === 'ROLE_ADMIN' ? 'ROLE_USER' : 'ROLE_ADMIN';
      await adminApi.updateUser(user.id, { role: newRole });
      await fetchUsers();
      showToast(t('roleChanged', { role: newRole === 'ROLE_ADMIN' ? t('admin') : t('user') }));
    } catch (err: unknown) {
      showToast(normalizeError(err, t('roleUpdateFailed')).message);
    } finally {
      setActionLoading(false);
      setActionMenu(null);
    }
  };

  const openEdit = (user: AdminUserResponse) => {
    setSelectedUser(user);
    setEditForm({ username: user.username, email: user.email, role: user.role });
    setEditModalOpen(true);
    setActionMenu(null);
  };

  const filtered = users.filter((u) => {
    const matchSearch = u.username.toLowerCase().includes(search.toLowerCase()) ||
      u.email.toLowerCase().includes(search.toLowerCase());
    const matchRole = roleFilter === 'all' || u.role === roleFilter;
    const matchStatus = statusFilter === 'all' ||
      (statusFilter === 'active' && u.role !== 'ROLE_BANNED') ||
      (statusFilter === 'inactive' && u.role === 'ROLE_BANNED');
    return matchSearch && matchRole && matchStatus;
  });

  const getRoleBadge = (role: string) => {
    if (role === 'ROLE_ADMIN') return { label: t('admin'), bg: 'bg-[#8B5CF6]/10 text-[#C084FC] border-[#8B5CF6]/30', icon: ShieldCheck };
    if (role === 'ROLE_BANNED') return { label: t('banned'), bg: 'bg-[#F43F5E]/10 text-[#F43F5E] border-[#F43F5E]/30', icon: XCircle };
    return { label: t('user'), bg: 'bg-[#14F195]/10 text-[#14F195] border-[#14F195]/30', icon: User };
  };

  return (
    <div className="p-4 md:p-6">
      {/* Header */}
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold">{t('users')}</h1>
          <p className="text-sm text-[var(--muted-foreground)] mt-1">{t('totalUsersCount', { count: users.length })}</p>
        </div>
        <button onClick={() => setAddModalOpen(true)} className="py-2.5 px-5 bg-gradient-to-br from-[#8b5cf6] to-[#A855F7] text-white rounded-[var(--radius-md)] text-sm font-semibold flex items-center gap-2 shadow-[0_10px_25px_rgba(139,92,246,.4)] hover:-translate-y-0.5 transition-all">
          <Plus size={14} />{t('addUser')}
        </button>
      </div>

      {/* Filters */}
      <div className="flex items-center gap-3 mb-6 flex-wrap">
        <div className="relative flex-1 min-w-[200px] max-w-sm">
          <Search size={14} className="absolute left-3 top-1/2 -translate-y-1/2 text-[var(--muted-foreground)]" />
          <input type="text" placeholder={t('searchPlaceholder')} value={search} onChange={(e) => setSearch(e.target.value)} className="w-full py-2 pl-9 pr-4 bg-[var(--input)] border border-[var(--border)] rounded-[var(--radius-sm)] text-sm text-[var(--foreground)] outline-none focus:border-[var(--border-focus)] transition-all" />
        </div>
        <select value={roleFilter} onChange={(e) => setRoleFilter(e.target.value)} className="py-2 px-3 bg-[var(--input)] border border-[var(--border)] rounded-[var(--radius-sm)] text-sm text-[var(--foreground)] outline-none focus:border-[var(--border-focus)]">
          <option value="all">{t('allRoles')}</option>
          <option value="ROLE_ADMIN">{t('admin')}</option>
          <option value="ROLE_USER">{t('user')}</option>
        </select>
        <select value={statusFilter} onChange={(e) => setStatusFilter(e.target.value)} className="py-2 px-3 bg-[var(--input)] border border-[var(--border)] rounded-[var(--radius-sm)] text-sm text-[var(--foreground)] outline-none focus:border-[var(--border-focus)]">
          <option value="all">{t('allStatus')}</option>
          <option value="active">{t('activeStatus')}</option>
          <option value="inactive">{t('inactive')}</option>
        </select>
      </div>

      {/* Loading / Error */}
      {loading && (
        <div className="flex items-center justify-center py-16">
          <Loader2 size={24} className="animate-spin text-[#8B5CF6]" />
          <span className="ml-3 text-sm text-[var(--muted-foreground)]">{t('loadingUsers')}</span>
        </div>
      )}
      {error && (
        <div className="bg-[#F43F5E]/10 border border-[#F43F5E]/30 rounded-[var(--radius-md)] p-4 mb-6 flex items-center gap-3">
          <AlertTriangle size={16} className="text-[#F43F5E]" />
          <span className="text-sm text-[#F43F5E]">{error}</span>
          <button onClick={fetchUsers} className="ml-auto text-xs text-[#F43F5E] underline">{t('retry')}</button>
        </div>
      )}

      {/* Table */}
      {!loading && !error && (
        <div className="border border-[var(--border)] bg-[var(--card)] rounded-[var(--radius-lg)] overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-[var(--border)]">
                  <th className="text-left py-3 px-4 text-[var(--muted-foreground)] font-medium">{t('colUser')}</th>
                  <th className="text-left py-3 px-4 text-[var(--muted-foreground)] font-medium hidden md:table-cell">{t('colEmail')}</th>
                  <th className="text-left py-3 px-4 text-[var(--muted-foreground)] font-medium">{t('colRole')}</th>
                  <th className="text-left py-3 px-4 text-[var(--muted-foreground)] font-medium hidden sm:table-cell">{t('colLevel')}</th>
                  <th className="text-right py-3 px-4 text-[var(--muted-foreground)] font-medium">{t('colActions')}</th>
                </tr>
              </thead>
              <tbody>
                {filtered.length === 0 ? (
                  <tr><td colSpan={5} className="py-12 text-center text-[var(--muted-foreground)] text-sm">{t('noUsers')}</td></tr>
                ) : (
                  filtered.map((user) => {
                    const badge = getRoleBadge(user.role);
                    const BadgeIcon = badge.icon;
                    return (
                      <tr key={user.id} className="border-b border-[var(--border)]/50 hover:bg-white/[0.02] relative">
                        <td className="py-3 px-4">
                          <div className="flex items-center gap-3">
                            <div className="w-9 h-9 rounded-full bg-gradient-to-br from-[#8b5cf6] to-[#A855F7] flex items-center justify-center text-white text-xs font-bold flex-shrink-0">
                              {user.username[0].toUpperCase()}
                            </div>
                            <div>
                              <div className="font-medium">{user.username}</div>
                              <div className="text-xs text-[var(--muted-foreground)] md:hidden">{user.email}</div>
                            </div>
                          </div>
                        </td>
                        <td className="py-3 px-4 text-[var(--muted-foreground)] hidden md:table-cell">{user.email}</td>
                        <td className="py-3 px-4">
                          <span className={`text-[10px] font-medium px-2.5 py-1 rounded-full border inline-flex items-center gap-1 ${badge.bg}`}>
                            <BadgeIcon size={10} />{badge.label}
                          </span>
                        </td>
                        <td className="py-3 px-4 text-[var(--muted-foreground)] hidden sm:table-cell">
                          <span className="font-mono text-xs">{user.level || 1}</span>
                        </td>
                        <td className="py-3 px-4 text-right">
                          <div className="relative inline-block">
                            <button onClick={() => setActionMenu(actionMenu === user.id ? null : user.id)} className="p-1.5 rounded-lg hover:bg-white/[0.05] text-[var(--muted-foreground)] hover:text-[var(--foreground)] transition-all">
                              <MoreVertical size={16} />
                            </button>
                            {actionMenu === user.id && (
                              <div className="absolute right-0 top-full mt-1 w-44 bg-[#1a1b2e] border border-[var(--border)] rounded-[var(--radius-sm)] shadow-xl z-50 py-1">
                                <button onClick={() => openEdit(user)} className="w-full flex items-center gap-2.5 px-3 py-2 text-sm text-[var(--foreground)] hover:bg-white/[0.05] transition-all">
                                  <Edit3 size={14} />{t('editUser')}
                                </button>
                                <button onClick={() => handleToggleRole(user)} className="w-full flex items-center gap-2.5 px-3 py-2 text-sm text-[var(--foreground)] hover:bg-white/[0.05] transition-all">
                                  <Shield size={14} />{t('changeRole')}
                                </button>
                                <button onClick={() => { setDeleteConfirmOpen(user.id); setActionMenu(null); }} className="w-full flex items-center gap-2.5 px-3 py-2 text-sm text-[#F43F5E] hover:bg-[#F43F5E]/10 transition-all">
                                  <Trash2 size={14} />{t('deleteUser')}
                                </button>
                              </div>
                            )}
                          </div>
                        </td>
                      </tr>
                    );
                  })
                )}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* ADD USER MODAL */}
      {addModalOpen && (
        <div className="fixed inset-0 bg-[rgba(5,5,8,0.8)] backdrop-blur-xl z-[100] flex items-center justify-center p-5" onClick={() => setAddModalOpen(false)}>
          <div className="bg-[#0F101A] border border-[rgba(255,255,255,0.08)] rounded-[24px] w-full max-w-[480px] p-8 shadow-[0_25px_50px_rgba(0,0,0,0.7)]" onClick={(e) => e.stopPropagation()} style={{ animation: 'modalSlide 0.3s ease forwards' }}>
            <div className="flex items-center justify-between mb-6">
              <h3 className="text-xl font-bold">{t('addNewUser')}</h3>
              <button onClick={() => setAddModalOpen(false)} className="bg-transparent border-none text-[var(--dim)] text-xl cursor-pointer hover:text-[var(--foreground)]">&times;</button>
            </div>
            <div className="space-y-4">
              <div>
                <label className="block text-xs text-[var(--muted-foreground)] mb-1.5">{t('username')}</label>
                <input type="text" value={addForm.username} onChange={(e) => setAddForm({ ...addForm, username: e.target.value })} className="w-full py-2.5 px-3.5 bg-[var(--input)] border border-[var(--border-glass)] rounded-[var(--radius-sm)] text-sm text-[var(--foreground)] outline-none focus:border-[var(--border-focus)]" placeholder={t('enterUsername')} />
              </div>
              <div>
                <label className="block text-xs text-[var(--muted-foreground)] mb-1.5">{t('email')}</label>
                <input type="email" value={addForm.email} onChange={(e) => setAddForm({ ...addForm, email: e.target.value })} className="w-full py-2.5 px-3.5 bg-[var(--input)] border border-[var(--border-glass)] rounded-[var(--radius-sm)] text-sm text-[var(--foreground)] outline-none focus:border-[var(--border-focus)]" placeholder="user@email.com" />
              </div>
              <div>
                <label className="block text-xs text-[var(--muted-foreground)] mb-1.5">{t('password')}</label>
                <input type="password" value={addForm.password} onChange={(e) => setAddForm({ ...addForm, password: e.target.value })} className="w-full py-2.5 px-3.5 bg-[var(--input)] border border-[var(--border-glass)] rounded-[var(--radius-sm)] text-sm text-[var(--foreground)] outline-none focus:border-[var(--border-focus)]" placeholder={t('minChars')} />
              </div>
              <div>
                <label className="block text-xs text-[var(--muted-foreground)] mb-1.5">{t('role')}</label>
                <select value={addForm.role} onChange={(e) => setAddForm({ ...addForm, role: e.target.value as AdminUserResponse['role'] })} className="w-full py-2.5 px-3.5 bg-[var(--input)] border border-[var(--border-glass)] rounded-[var(--radius-sm)] text-sm text-[var(--foreground)] outline-none focus:border-[var(--border-focus)]">
                  <option value="ROLE_USER">{t('user')}</option>
                  <option value="ROLE_ADMIN">{t('admin')}</option>
                </select>
              </div>
            </div>
            <div className="flex justify-end gap-3 mt-6">
              <button onClick={() => setAddModalOpen(false)} className="py-2.5 px-5 bg-[var(--input)] border border-[var(--border-glass)] rounded-[var(--radius-md)] text-sm font-medium text-[var(--foreground)] cursor-pointer hover:bg-white/[0.08] transition-all">{t('cancel')}</button>
              <button onClick={handleAddUser} disabled={actionLoading || !addForm.username || !addForm.email || !addForm.password} className="py-2.5 px-5 bg-gradient-to-br from-[#8b5cf6] to-[#A855F7] text-white rounded-[var(--radius-md)] text-sm font-semibold cursor-pointer shadow-[0_8px_20px_rgba(139,92,246,0.35)] disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2">
                {actionLoading ? <Loader2 size={14} className="animate-spin" /> : <Plus size={14} />}
                {t('createUser')}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* EDIT USER MODAL */}
      {editModalOpen && selectedUser && (
        <div className="fixed inset-0 bg-[rgba(5,5,8,0.8)] backdrop-blur-xl z-[100] flex items-center justify-center p-5" onClick={() => setEditModalOpen(false)}>
          <div className="bg-[#0F101A] border border-[rgba(255,255,255,0.08)] rounded-[24px] w-full max-w-[480px] p-8 shadow-[0_25px_50px_rgba(0,0,0,0.7)]" onClick={(e) => e.stopPropagation()} style={{ animation: 'modalSlide 0.3s ease forwards' }}>
            <div className="flex items-center justify-between mb-6">
              <h3 className="text-xl font-bold">{t('editUserTitle')}</h3>
              <button onClick={() => setEditModalOpen(false)} className="bg-transparent border-none text-[var(--dim)] text-xl cursor-pointer hover:text-[var(--foreground)]">&times;</button>
            </div>
            <div className="space-y-4">
              <div>
                <label className="block text-xs text-[var(--muted-foreground)] mb-1.5">{t('username')}</label>
                <input type="text" value={editForm.username} onChange={(e) => setEditForm({ ...editForm, username: e.target.value })} className="w-full py-2.5 px-3.5 bg-[var(--input)] border border-[var(--border-glass)] rounded-[var(--radius-sm)] text-sm text-[var(--foreground)] outline-none focus:border-[var(--border-focus)]" />
              </div>
              <div>
                <label className="block text-xs text-[var(--muted-foreground)] mb-1.5">{t('email')}</label>
                <input type="email" value={editForm.email} onChange={(e) => setEditForm({ ...editForm, email: e.target.value })} className="w-full py-2.5 px-3.5 bg-[var(--input)] border border-[var(--border-glass)] rounded-[var(--radius-sm)] text-sm text-[var(--foreground)] outline-none focus:border-[var(--border-focus)]" />
              </div>
              <div>
                <label className="block text-xs text-[var(--muted-foreground)] mb-1.5">{t('role')}</label>
                <select value={editForm.role} onChange={(e) => setEditForm({ ...editForm, role: e.target.value as AdminUserResponse['role'] })} className="w-full py-2.5 px-3.5 bg-[var(--input)] border border-[var(--border-glass)] rounded-[var(--radius-sm)] text-sm text-[var(--foreground)] outline-none focus:border-[var(--border-focus)]">
                  <option value="ROLE_USER">{t('user')}</option>
                  <option value="ROLE_ADMIN">{t('admin')}</option>
                </select>
              </div>
            </div>
            <div className="flex justify-end gap-3 mt-6">
              <button onClick={() => setEditModalOpen(false)} className="py-2.5 px-5 bg-[var(--input)] border border-[var(--border-glass)] rounded-[var(--radius-md)] text-sm font-medium text-[var(--foreground)] cursor-pointer hover:bg-white/[0.08] transition-all">{t('cancel')}</button>
              <button onClick={handleUpdateUser} disabled={actionLoading} className="py-2.5 px-5 bg-gradient-to-br from-[#8b5cf6] to-[#A855F7] text-white rounded-[var(--radius-md)] text-sm font-semibold cursor-pointer shadow-[0_8px_20px_rgba(139,92,246,0.35)] disabled:opacity-50 flex items-center gap-2">
                {actionLoading ? <Loader2 size={14} className="animate-spin" /> : <CheckCircle size={14} />}
                {t('saveChanges')}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* DELETE CONFIRMATION MODAL */}
      {deleteConfirmOpen && (
        <div className="fixed inset-0 bg-[rgba(5,5,8,0.8)] backdrop-blur-xl z-[110] flex items-center justify-center p-5" onClick={() => setDeleteConfirmOpen(null)}>
          <div className="bg-[#0F101A] border border-[#F43F5E]/20 rounded-[24px] w-full max-w-[400px] p-8 shadow-[0_25px_50px_rgba(0,0,0,0.7)]" onClick={(e) => e.stopPropagation()}>
            <div className="text-center">
              <AlertTriangle size={40} className="text-[#F43F5E] mx-auto mb-4" />
              <h3 className="text-lg font-bold mb-2">{t('deleteUserTitle')}</h3>
              <p className="text-sm text-[var(--muted-foreground)] mb-6">{t('deleteConfirm')}</p>
            </div>
            <div className="flex justify-center gap-3">
              <button onClick={() => setDeleteConfirmOpen(null)} className="py-2.5 px-5 bg-[var(--input)] border border-[var(--border)] rounded-[var(--radius-md)] text-sm text-[var(--foreground)] cursor-pointer hover:bg-white/[0.08] transition-all">{t('cancel')}</button>
              <button onClick={() => handleDeleteUser('')} disabled={actionLoading} className="py-2.5 px-5 bg-[#F43F5E]/10 border border-[#F43F5E]/30 text-[#F43F5E] rounded-[var(--radius-md)] text-sm font-medium cursor-pointer hover:bg-[#F43F5E]/20 transition-all flex items-center gap-2">
                {actionLoading ? <Loader2 size={14} className="animate-spin" /> : <Trash2 size={14} />}
                {t('delete')}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* TOAST */}
      {toast && (
        <div className="fixed bottom-8 right-8 bg-[#14F195] text-[#050508] py-3 px-5 rounded-[var(--radius-sm)] font-semibold text-sm flex items-center gap-2.5 shadow-[0_10px_25px_rgba(20,241,149,0.4)] z-[200]" style={{ animation: 'toastIn 0.3s ease forwards' }}>
          <CheckCircle size={16} />{toast}
        </div>
      )}

      <style jsx global>{`
        @keyframes modalSlide { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }
        @keyframes toastIn { from { opacity: 0; transform: translateY(100px); } to { opacity: 1; transform: translateY(0); } }
      `}</style>
    </div>
  );
}
