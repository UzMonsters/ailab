'use client';

import { useState } from 'react';
import { useAdminStore, SafetyRule } from '@/stores/admin.store';
import { Plus, Search, MoreVertical, Edit3, Trash2, ShieldAlert, AlertTriangle, CheckCircle } from 'lucide-react';
import { useTranslations } from 'next-intl';

export default function AdminSafetyPage() {
  const t = useTranslations('admin');
  const safetyRules = useAdminStore(state => state.safetyRules);
  const addSafetyRule = useAdminStore(state => state.addSafetyRule);
  const updateSafetyRule = useAdminStore(state => state.updateSafetyRule);
  const deleteSafetyRule = useAdminStore(state => state.deleteSafetyRule);

  const [search, setSearch] = useState('');
  const [categoryFilter, setCategoryFilter] = useState('all');
  const [addModalOpen, setAddModalOpen] = useState(false);
  const [editModalOpen, setEditModalOpen] = useState(false);
  const [deleteConfirmOpen, setDeleteConfirmOpen] = useState<string | null>(null);
  const [selectedRule, setSelectedRule] = useState<SafetyRule | null>(null);
  const [actionMenu, setActionMenu] = useState<string | null>(null);

  const [addForm, setAddForm] = useState<Omit<SafetyRule, 'id'>>({
    title: '',
    description: '',
    category: 'General',
    severity: 'Medium'
  });

  const [editForm, setEditForm] = useState<Partial<SafetyRule>>({});

  const filtered = safetyRules.filter((sr) => {
    const matchSearch = sr.title.toLowerCase().includes(search.toLowerCase()) || sr.description.toLowerCase().includes(search.toLowerCase());
    const matchCategory = categoryFilter === 'all' || sr.category === categoryFilter;
    return matchSearch && matchCategory;
  });

  const handleAdd = () => {
    if (!addForm.title || !addForm.description) return;
    addSafetyRule(addForm);
    setAddModalOpen(false);
    setAddForm({ title: '', description: '', category: 'General', severity: 'Medium' });
  };

  const handleUpdate = () => {
    if (!selectedRule) return;
    updateSafetyRule(selectedRule.id, editForm);
    setEditModalOpen(false);
  };

  const handleDelete = () => {
    if (!deleteConfirmOpen) return;
    deleteSafetyRule(deleteConfirmOpen);
    setDeleteConfirmOpen(null);
    setActionMenu(null);
  };

  const openEdit = (rule: SafetyRule) => {
    setSelectedRule(rule);
    setEditForm({
      title: rule.title,
      description: rule.description,
      category: rule.category,
      severity: rule.severity
    });
    setEditModalOpen(true);
    setActionMenu(null);
  };

  const getSeverityBadge = (severity: string) => {
    if (severity === 'Critical') return 'bg-[#F43F5E]/10 text-[#F43F5E] border-[#F43F5E]/30';
    if (severity === 'High') return 'bg-[#F59E0B]/10 text-[#F59E0B] border-[#F59E0B]/30';
    if (severity === 'Medium') return 'bg-[#3B82F6]/10 text-[#3B82F6] border-[#3B82F6]/30';
    return 'bg-[#14F195]/10 text-[#14F195] border-[#14F195]/30';
  };

  return (
    <div className="p-4 md:p-6">
      {/* Header */}
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold">Safety Rules</h1>
          <p className="text-sm text-[var(--muted-foreground)] mt-1">{safetyRules.length} total rules</p>
        </div>
        <button onClick={() => setAddModalOpen(true)} className="py-2.5 px-5 bg-gradient-to-br from-[#8b5cf6] to-[#A855F7] text-white rounded-[var(--radius-md)] text-sm font-semibold flex items-center gap-2 shadow-[0_10px_25px_rgba(139,92,246,.4)] hover:-translate-y-0.5 transition-all">
          <Plus size={14} />Add Rule
        </button>
      </div>

      {/* Filters */}
      <div className="flex items-center gap-3 mb-6 flex-wrap">
        <div className="relative flex-1 min-w-[200px] max-w-sm">
          <Search size={14} className="absolute left-3 top-1/2 -translate-y-1/2 text-[var(--muted-foreground)]" />
          <input type="text" placeholder="Search safety rules..." value={search} onChange={(e) => setSearch(e.target.value)} className="w-full py-2 pl-9 pr-4 bg-[var(--input)] border border-[var(--border)] rounded-[var(--radius-sm)] text-sm text-[var(--foreground)] outline-none focus:border-[var(--border-focus)] transition-all" />
        </div>
        <select value={categoryFilter} onChange={(e) => setCategoryFilter(e.target.value)} className="py-2 px-3 bg-[var(--input)] border border-[var(--border)] rounded-[var(--radius-sm)] text-sm text-[var(--foreground)] outline-none focus:border-[var(--border-focus)]">
          <option value="all">All Categories</option>
          <option value="General">General</option>
          <option value="Chemical">Chemical</option>
          <option value="Biological">Biological</option>
          <option value="Electrical">Electrical</option>
          <option value="Fire">Fire</option>
        </select>
      </div>

      {/* Table */}
      <div className="border border-[var(--border)] bg-[var(--card)] rounded-[var(--radius-lg)] overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-[var(--border)]">
                <th className="text-left py-3 px-4 text-[var(--muted-foreground)] font-medium">Title & Description</th>
                <th className="text-left py-3 px-4 text-[var(--muted-foreground)] font-medium">Category</th>
                <th className="text-left py-3 px-4 text-[var(--muted-foreground)] font-medium">Severity</th>
                <th className="text-right py-3 px-4 text-[var(--muted-foreground)] font-medium">Actions</th>
              </tr>
            </thead>
            <tbody>
              {filtered.length === 0 ? (
                <tr><td colSpan={4} className="py-12 text-center text-[var(--muted-foreground)] text-sm">No safety rules found</td></tr>
              ) : (
                filtered.map((rule) => (
                  <tr key={rule.id} className="border-b border-[var(--border)]/50 hover:bg-white/[0.02] relative">
                    <td className="py-3 px-4">
                      <div className="flex items-start gap-3">
                        <div className="w-9 h-9 mt-0.5 rounded-full bg-gradient-to-br from-[#F43F5E] to-[#FB7185] flex items-center justify-center text-white text-xs font-bold flex-shrink-0">
                          <ShieldAlert size={16} />
                        </div>
                        <div>
                          <div className="font-medium">{rule.title}</div>
                          <div className="text-xs text-[var(--muted-foreground)] mt-0.5 max-w-md">{rule.description}</div>
                        </div>
                      </div>
                    </td>
                    <td className="py-3 px-4 text-[var(--muted-foreground)]">{rule.category}</td>
                    <td className="py-3 px-4">
                      <span className={`text-[10px] font-medium px-2.5 py-1 rounded-full border inline-flex items-center gap-1 ${getSeverityBadge(rule.severity)}`}>
                        {rule.severity}
                      </span>
                    </td>
                    <td className="py-3 px-4 text-right">
                      <div className="relative inline-block">
                        <button onClick={() => setActionMenu(actionMenu === rule.id ? null : rule.id)} className="p-1.5 rounded-lg hover:bg-white/[0.05] text-[var(--muted-foreground)] hover:text-[var(--foreground)] transition-all">
                          <MoreVertical size={16} />
                        </button>
                        {actionMenu === rule.id && (
                          <div className="absolute right-0 top-full mt-1 w-44 bg-[#1a1b2e] border border-[var(--border)] rounded-[var(--radius-sm)] shadow-xl z-50 py-1">
                            <button onClick={() => openEdit(rule)} className="w-full flex items-center gap-2.5 px-3 py-2 text-sm text-[var(--foreground)] hover:bg-white/[0.05] transition-all">
                              <Edit3 size={14} />Edit
                            </button>
                            <button onClick={() => { setDeleteConfirmOpen(rule.id); setActionMenu(null); }} className="w-full flex items-center gap-2.5 px-3 py-2 text-sm text-[#F43F5E] hover:bg-[#F43F5E]/10 transition-all">
                              <Trash2 size={14} />Delete
                            </button>
                          </div>
                        )}
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* ADD MODAL */}
      {addModalOpen && (
        <div className="fixed inset-0 bg-[rgba(5,5,8,0.8)] backdrop-blur-xl z-[100] flex items-center justify-center p-5" onClick={() => setAddModalOpen(false)}>
          <div className="bg-[#0F101A] border border-[rgba(255,255,255,0.08)] rounded-[24px] w-full max-w-[480px] p-8 shadow-[0_25px_50px_rgba(0,0,0,0.7)]" onClick={(e) => e.stopPropagation()} style={{ animation: 'modalSlide 0.3s ease forwards' }}>
            <div className="flex items-center justify-between mb-6">
              <h3 className="text-xl font-bold">Add Safety Rule</h3>
              <button onClick={() => setAddModalOpen(false)} className="bg-transparent border-none text-[var(--dim)] text-xl cursor-pointer hover:text-[var(--foreground)]">&times;</button>
            </div>
            <div className="space-y-4">
              <div>
                <label className="block text-xs text-[var(--muted-foreground)] mb-1.5">Title</label>
                <input type="text" value={addForm.title} onChange={(e) => setAddForm({ ...addForm, title: e.target.value })} className="w-full py-2.5 px-3.5 bg-[var(--input)] border border-[var(--border-glass)] rounded-[var(--radius-sm)] text-sm text-[var(--foreground)] outline-none focus:border-[var(--border-focus)]" />
              </div>
              <div>
                <label className="block text-xs text-[var(--muted-foreground)] mb-1.5">Description</label>
                <textarea value={addForm.description} onChange={(e) => setAddForm({ ...addForm, description: e.target.value })} className="w-full py-2.5 px-3.5 bg-[var(--input)] border border-[var(--border-glass)] rounded-[var(--radius-sm)] text-sm text-[var(--foreground)] outline-none focus:border-[var(--border-focus)] min-h-[80px]" />
              </div>
              <div>
                <label className="block text-xs text-[var(--muted-foreground)] mb-1.5">Category</label>
                <select value={addForm.category} onChange={(e) => setAddForm({ ...addForm, category: e.target.value as any })} className="w-full py-2.5 px-3.5 bg-[var(--input)] border border-[var(--border-glass)] rounded-[var(--radius-sm)] text-sm text-[var(--foreground)] outline-none focus:border-[var(--border-focus)]">
                  <option value="General">General</option>
                  <option value="Chemical">Chemical</option>
                  <option value="Biological">Biological</option>
                  <option value="Electrical">Electrical</option>
                  <option value="Fire">Fire</option>
                </select>
              </div>
              <div>
                <label className="block text-xs text-[var(--muted-foreground)] mb-1.5">Severity</label>
                <select value={addForm.severity} onChange={(e) => setAddForm({ ...addForm, severity: e.target.value as any })} className="w-full py-2.5 px-3.5 bg-[var(--input)] border border-[var(--border-glass)] rounded-[var(--radius-sm)] text-sm text-[var(--foreground)] outline-none focus:border-[var(--border-focus)]">
                  <option value="Low">Low</option>
                  <option value="Medium">Medium</option>
                  <option value="High">High</option>
                  <option value="Critical">Critical</option>
                </select>
              </div>
            </div>
            <div className="flex justify-end gap-3 mt-6">
              <button onClick={() => setAddModalOpen(false)} className="py-2.5 px-5 bg-[var(--input)] border border-[var(--border-glass)] rounded-[var(--radius-md)] text-sm font-medium text-[var(--foreground)] cursor-pointer hover:bg-white/[0.08] transition-all">Cancel</button>
              <button onClick={handleAdd} disabled={!addForm.title || !addForm.description} className="py-2.5 px-5 bg-gradient-to-br from-[#8b5cf6] to-[#A855F7] text-white rounded-[var(--radius-md)] text-sm font-semibold cursor-pointer shadow-[0_8px_20px_rgba(139,92,246,0.35)] disabled:opacity-50 disabled:cursor-not-allowed">
                Create
              </button>
            </div>
          </div>
        </div>
      )}

      {/* EDIT MODAL */}
      {editModalOpen && selectedRule && (
        <div className="fixed inset-0 bg-[rgba(5,5,8,0.8)] backdrop-blur-xl z-[100] flex items-center justify-center p-5" onClick={() => setEditModalOpen(false)}>
          <div className="bg-[#0F101A] border border-[rgba(255,255,255,0.08)] rounded-[24px] w-full max-w-[480px] p-8 shadow-[0_25px_50px_rgba(0,0,0,0.7)]" onClick={(e) => e.stopPropagation()} style={{ animation: 'modalSlide 0.3s ease forwards' }}>
            <div className="flex items-center justify-between mb-6">
              <h3 className="text-xl font-bold">Edit Safety Rule</h3>
              <button onClick={() => setEditModalOpen(false)} className="bg-transparent border-none text-[var(--dim)] text-xl cursor-pointer hover:text-[var(--foreground)]">&times;</button>
            </div>
            <div className="space-y-4">
              <div>
                <label className="block text-xs text-[var(--muted-foreground)] mb-1.5">Title</label>
                <input type="text" value={editForm.title || ''} onChange={(e) => setEditForm({ ...editForm, title: e.target.value })} className="w-full py-2.5 px-3.5 bg-[var(--input)] border border-[var(--border-glass)] rounded-[var(--radius-sm)] text-sm text-[var(--foreground)] outline-none focus:border-[var(--border-focus)]" />
              </div>
              <div>
                <label className="block text-xs text-[var(--muted-foreground)] mb-1.5">Description</label>
                <textarea value={editForm.description || ''} onChange={(e) => setEditForm({ ...editForm, description: e.target.value })} className="w-full py-2.5 px-3.5 bg-[var(--input)] border border-[var(--border-glass)] rounded-[var(--radius-sm)] text-sm text-[var(--foreground)] outline-none focus:border-[var(--border-focus)] min-h-[80px]" />
              </div>
              <div>
                <label className="block text-xs text-[var(--muted-foreground)] mb-1.5">Category</label>
                <select value={editForm.category} onChange={(e) => setEditForm({ ...editForm, category: e.target.value as any })} className="w-full py-2.5 px-3.5 bg-[var(--input)] border border-[var(--border-glass)] rounded-[var(--radius-sm)] text-sm text-[var(--foreground)] outline-none focus:border-[var(--border-focus)]">
                  <option value="General">General</option>
                  <option value="Chemical">Chemical</option>
                  <option value="Biological">Biological</option>
                  <option value="Electrical">Electrical</option>
                  <option value="Fire">Fire</option>
                </select>
              </div>
              <div>
                <label className="block text-xs text-[var(--muted-foreground)] mb-1.5">Severity</label>
                <select value={editForm.severity} onChange={(e) => setEditForm({ ...editForm, severity: e.target.value as any })} className="w-full py-2.5 px-3.5 bg-[var(--input)] border border-[var(--border-glass)] rounded-[var(--radius-sm)] text-sm text-[var(--foreground)] outline-none focus:border-[var(--border-focus)]">
                  <option value="Low">Low</option>
                  <option value="Medium">Medium</option>
                  <option value="High">High</option>
                  <option value="Critical">Critical</option>
                </select>
              </div>
            </div>
            <div className="flex justify-end gap-3 mt-6">
              <button onClick={() => setEditModalOpen(false)} className="py-2.5 px-5 bg-[var(--input)] border border-[var(--border-glass)] rounded-[var(--radius-md)] text-sm font-medium text-[var(--foreground)] cursor-pointer hover:bg-white/[0.08] transition-all">Cancel</button>
              <button onClick={handleUpdate} className="py-2.5 px-5 bg-gradient-to-br from-[#8b5cf6] to-[#A855F7] text-white rounded-[var(--radius-md)] text-sm font-semibold cursor-pointer shadow-[0_8px_20px_rgba(139,92,246,0.35)] flex items-center gap-2">
                <CheckCircle size={14} />Save
              </button>
            </div>
          </div>
        </div>
      )}

      {/* DELETE CONFIRM */}
      {deleteConfirmOpen && (
        <div className="fixed inset-0 bg-[rgba(5,5,8,0.8)] backdrop-blur-xl z-[110] flex items-center justify-center p-5" onClick={() => setDeleteConfirmOpen(null)}>
          <div className="bg-[#0F101A] border border-[#F43F5E]/20 rounded-[24px] w-full max-w-[400px] p-8 shadow-[0_25px_50px_rgba(0,0,0,0.7)]" onClick={(e) => e.stopPropagation()}>
            <div className="text-center">
              <AlertTriangle size={40} className="text-[#F43F5E] mx-auto mb-4" />
              <h3 className="text-lg font-bold mb-2">Delete Safety Rule</h3>
              <p className="text-sm text-[var(--muted-foreground)] mb-6">Are you sure you want to delete this safety rule? This action cannot be undone.</p>
            </div>
            <div className="flex justify-center gap-3">
              <button onClick={() => setDeleteConfirmOpen(null)} className="py-2.5 px-5 bg-[var(--input)] border border-[var(--border)] rounded-[var(--radius-md)] text-sm text-[var(--foreground)] cursor-pointer hover:bg-white/[0.08] transition-all">Cancel</button>
              <button onClick={handleDelete} className="py-2.5 px-5 bg-[#F43F5E]/10 border border-[#F43F5E]/30 text-[#F43F5E] rounded-[var(--radius-md)] text-sm font-medium cursor-pointer hover:bg-[#F43F5E]/20 transition-all flex items-center gap-2">
                <Trash2 size={14} />Delete
              </button>
            </div>
          </div>
        </div>
      )}
      
      <style jsx global>{`
        @keyframes modalSlide { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }
      `}</style>
    </div>
  );
}
