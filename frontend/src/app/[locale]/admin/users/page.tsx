'use client';

import React, { useState } from 'react';
import { useTranslations } from 'next-intl';
import { useAdminStore, AdminUser } from '@/stores/admin.store';
import { useToastStore } from '@/stores/toast.store';
import AdminPageHeader from '@/widgets/admin/AdminPageHeader';
import AdminDataTable, { Column } from '@/widgets/admin/AdminDataTable';
import AdminDrawer from '@/widgets/admin/AdminDrawer';
import { User, Shield, GraduationCap, Ban, Activity, TrendingUp, MoreVertical, Edit3, Trash2, ShieldCheck, Mail, Hash } from 'lucide-react';

type Tab = 'All' | 'Students' | 'Teachers' | 'Admins' | 'Blocked' | 'Activity' | 'Progress';

export default function AdminUsersPage() {
  const t = useTranslations('admin');
  const { users, updateUser, deleteUser } = useAdminStore();
  const { addToast } = useToastStore();
  
  const [activeTab, setActiveTab] = useState<Tab>('All');
  const [selectedUser, setSelectedUser] = useState<AdminUser | null>(null);
  const [isDrawerOpen, setIsDrawerOpen] = useState(false);
  const [actionMenu, setActionMenu] = useState<string | null>(null);

  const tabs: { id: Tab; label: string; icon: React.FC<any> }[] = [
    { id: 'All', label: 'All Users', icon: User },
    { id: 'Students', label: 'Students', icon: GraduationCap },
    { id: 'Teachers', label: 'Teachers', icon: User },
    { id: 'Admins', label: 'Admins', icon: Shield },
    { id: 'Blocked', label: 'Blocked', icon: Ban },
    { id: 'Activity', label: 'Activity Logs', icon: Activity },
    { id: 'Progress', label: 'Learning Progress', icon: TrendingUp },
  ];

  const filteredUsers = React.useMemo(() => {
    switch (activeTab) {
      case 'Students': return users.filter(u => u.role === 'Student');
      case 'Teachers': return users.filter(u => u.role === 'Teacher');
      case 'Admins': return users.filter(u => u.role === 'Admin');
      case 'Blocked': return users.filter(u => u.status === 'Blocked');
      default: return users;
    }
  }, [users, activeTab]);

  const handleBlockUser = (user: AdminUser) => {
    const newStatus = user.status === 'Blocked' ? 'Active' : 'Blocked';
    updateUser(user.id, { status: newStatus });
    addToast({ title: 'User Updated', message: `User ${user.name} is now ${newStatus}`, type: 'success' });
    setActionMenu(null);
  };

  const handleDeleteUser = (id: string, name: string) => {
    deleteUser(id);
    addToast({ title: 'User Deleted', message: `User ${name} has been deleted`, type: 'success' });
    setActionMenu(null);
  };

  const handleEditUser = (user: AdminUser) => {
    setSelectedUser(user);
    setIsDrawerOpen(true);
    setActionMenu(null);
  };

  const columns: Column<AdminUser>[] = [
    {
      header: 'User',
      accessorKey: 'name',
      sortable: true,
      cell: (item) => (
        <div className="flex items-center gap-3">
          <div className="w-8 h-8 rounded-full bg-gradient-to-br from-[#8b5cf6] to-[#A855F7] flex items-center justify-center text-white text-xs font-bold">
            {item.name.charAt(0)}
          </div>
          <div>
            <div className="font-medium text-white">{item.name}</div>
            <div className="text-xs text-[#8490a3]">{item.email}</div>
          </div>
        </div>
      )
    },
    {
      header: 'Role',
      accessorKey: 'role',
      sortable: true,
      cell: (item) => {
        let color = 'text-[#14F195] bg-[#14F195]/10 border-[#14F195]/20';
        if (item.role === 'Admin') color = 'text-[#8b5cf6] bg-[#8b5cf6]/10 border-[#8b5cf6]/20';
        if (item.role === 'Teacher') color = 'text-[#38bdf8] bg-[#38bdf8]/10 border-[#38bdf8]/20';
        
        return (
          <span className={`px-2 py-1 rounded-full border text-xs font-medium ${color}`}>
            {item.role}
          </span>
        );
      }
    },
    {
      header: 'Status',
      accessorKey: 'status',
      sortable: true,
      cell: (item) => {
        let color = 'text-gray-400 bg-gray-400/10 border-gray-400/20';
        if (item.status === 'Active') color = 'text-[#14F195] bg-[#14F195]/10 border-[#14F195]/20';
        if (item.status === 'Blocked') color = 'text-[#F43F5E] bg-[#F43F5E]/10 border-[#F43F5E]/20';
        
        return (
          <span className={`px-2 py-1 rounded border text-xs font-medium ${color}`}>
            {item.status}
          </span>
        );
      }
    },
    {
      header: 'Level / XP',
      accessorKey: 'level',
      sortable: true,
      cell: (item) => (
        <div>
          <div className="text-white">Lvl {item.level}</div>
          <div className="text-xs text-[#8490a3]">{item.xp.toLocaleString()} XP</div>
        </div>
      )
    },
    {
      header: 'Last Active',
      accessorKey: 'lastActive',
      sortable: true,
      cell: (item) => <span className="text-[#8490a3] text-sm">{item.lastActive}</span>
    },
    {
      header: 'Actions',
      cell: (item) => (
        <div className="relative flex justify-end">
          <button 
            onClick={(e) => {
              e.stopPropagation();
              setActionMenu(actionMenu === item.id ? null : item.id);
            }}
            className="p-1.5 text-[#8490a3] hover:text-white rounded hover:bg-white/5 transition-colors"
          >
            <MoreVertical size={16} />
          </button>
          
          {actionMenu === item.id && (
            <div className="absolute right-0 top-full mt-1 w-40 bg-[#141b2a] border border-white/10 rounded-lg shadow-xl z-50 py-1" onClick={e => e.stopPropagation()}>
              <button 
                onClick={() => handleEditUser(item)}
                className="w-full text-left px-3 py-2 text-sm text-white hover:bg-white/5 flex items-center gap-2"
              >
                <Edit3 size={14} /> Edit Details
              </button>
              <button 
                onClick={() => handleBlockUser(item)}
                className="w-full text-left px-3 py-2 text-sm text-white hover:bg-white/5 flex items-center gap-2"
              >
                <Ban size={14} className={item.status === 'Blocked' ? 'text-[#14F195]' : 'text-[#F43F5E]'} /> 
                {item.status === 'Blocked' ? 'Unblock User' : 'Block User'}
              </button>
              <div className="h-px bg-white/10 my-1"></div>
              <button 
                onClick={() => handleDeleteUser(item.id, item.name)}
                className="w-full text-left px-3 py-2 text-sm text-[#F43F5E] hover:bg-white/5 flex items-center gap-2"
              >
                <Trash2 size={14} /> Delete User
              </button>
            </div>
          )}
        </div>
      )
    }
  ];

  return (
    <div className="p-6 h-full flex flex-col">
      <AdminPageHeader 
        title="User Management" 
        description="Manage students, teachers, and administrative staff"
        counters={[
          { label: 'Total Users', value: users.length },
          { label: 'Active Today', value: users.filter(u => u.status === 'Active').length },
          { label: 'New This Week', value: 12 }
        ]}
      />

      <div className="flex items-center gap-1 overflow-x-auto mb-6 pb-2 scrollbar-none border-b border-white/5">
        {tabs.map(tab => {
          const Icon = tab.icon;
          const isActive = activeTab === tab.id;
          return (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id)}
              className={`flex items-center gap-2 px-4 py-2.5 text-sm font-medium rounded-t-lg transition-colors border-b-2 whitespace-nowrap ${
                isActive 
                  ? 'text-[#8b5cf6] border-[#8b5cf6] bg-[#8b5cf6]/5' 
                  : 'text-[#8490a3] border-transparent hover:text-white hover:bg-white/5'
              }`}
            >
              <Icon size={16} />
              {tab.label}
            </button>
          );
        })}
      </div>

      <div className="flex-1 min-h-0">
        {activeTab === 'Activity' || activeTab === 'Progress' ? (
          <div className="space-y-5">
            <div className="grid gap-4 sm:grid-cols-3">
              {(activeTab === 'Activity' ? [['Sessions today','1,284','+12.4%'],['Avg. lab time','18m 42s','+2.1%'],['Experiments','3,906','+8.7%']] : [['Active learners','4,812','84%'],['Levels completed','18,420','+16.8%'],['Avg. mastery','73%','+4.2%']]).map(([label,value,trend]) => <div key={label} className="rounded-xl border border-white/[.06] bg-[#0b101a] p-5"><div className="text-xs text-[#8490a3]">{label}</div><div className="mt-2 flex items-end justify-between"><span className="text-2xl font-bold text-white">{value}</span><span className="text-xs text-emerald-400">{trend}</span></div></div>)}
            </div>
            <div className="rounded-xl border border-white/[.06] bg-[#0b101a] p-5">
              <div className="mb-6 flex items-center justify-between"><div><h3 className="font-semibold text-white">{activeTab === 'Activity' ? 'Weekly participation' : 'Cohort progression'}</h3><p className="mt-1 text-xs text-[#8490a3]">Updated from the current reporting window</p></div><Activity size={20} className="text-violet-400"/></div>
              <div className="flex h-44 items-end gap-3">{[42,58,51,74,66,88,79,96,84,91,76,100].map((height,index) => <div key={index} className="flex h-full flex-1 items-end"><div className="w-full rounded-t bg-gradient-to-t from-violet-600 to-cyan-400" style={{height:`${height}%`}}/></div>)}</div>
            </div>
          </div>
        ) : (
          <AdminDataTable 
            data={filteredUsers} 
            columns={columns} 
            onRowClick={handleEditUser}
            searchPlaceholder={`Search ${activeTab.toLowerCase()}...`}
          />
        )}
      </div>

      <AdminDrawer
        isOpen={isDrawerOpen}
        onClose={() => setIsDrawerOpen(false)}
        title="Edit User Profile"
      >
        {selectedUser && (
          <div className="space-y-6">
            <div className="flex items-center gap-4">
              <div className="w-16 h-16 rounded-full bg-gradient-to-br from-[#8b5cf6] to-[#A855F7] flex items-center justify-center text-white text-2xl font-bold">
                {selectedUser.name.charAt(0)}
              </div>
              <div>
                <h3 className="text-lg font-bold text-white">{selectedUser.name}</h3>
                <p className="text-sm text-[#8490a3]">{selectedUser.id}</p>
              </div>
            </div>

            <div className="space-y-4">
              <div className="space-y-1.5">
                <label className="text-xs text-[#8490a3] uppercase tracking-wider flex items-center gap-1.5"><User size={12}/> Name</label>
                <input 
                  type="text" 
                  value={selectedUser.name}
                  onChange={(e) => setSelectedUser({...selectedUser, name: e.target.value})}
                  className="w-full bg-[#141b2a] border border-white/10 rounded-lg px-3 py-2 text-white focus:outline-none focus:border-[#8b5cf6]"
                />
              </div>

              <div className="space-y-1.5">
                <label className="text-xs text-[#8490a3] uppercase tracking-wider flex items-center gap-1.5"><Mail size={12}/> Email</label>
                <input 
                  type="email" 
                  value={selectedUser.email}
                  onChange={(e) => setSelectedUser({...selectedUser, email: e.target.value})}
                  className="w-full bg-[#141b2a] border border-white/10 rounded-lg px-3 py-2 text-white focus:outline-none focus:border-[#8b5cf6]"
                />
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-1.5">
                  <label className="text-xs text-[#8490a3] uppercase tracking-wider flex items-center gap-1.5"><ShieldCheck size={12}/> Role</label>
                  <select 
                    value={selectedUser.role}
                    onChange={(e) => setSelectedUser({...selectedUser, role: e.target.value as AdminUser['role']})}
                    className="w-full bg-[#141b2a] border border-white/10 rounded-lg px-3 py-2 text-white focus:outline-none focus:border-[#8b5cf6]"
                  >
                    <option value="Student">Student</option>
                    <option value="Teacher">Teacher</option>
                    <option value="Admin">Admin</option>
                  </select>
                </div>
                
                <div className="space-y-1.5">
                  <label className="text-xs text-[#8490a3] uppercase tracking-wider flex items-center gap-1.5"><Activity size={12}/> Status</label>
                  <select 
                    value={selectedUser.status}
                    onChange={(e) => setSelectedUser({...selectedUser, status: e.target.value as AdminUser['status']})}
                    className="w-full bg-[#141b2a] border border-white/10 rounded-lg px-3 py-2 text-white focus:outline-none focus:border-[#8b5cf6]"
                  >
                    <option value="Active">Active</option>
                    <option value="Offline">Offline</option>
                    <option value="Blocked">Blocked</option>
                  </select>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-1.5">
                  <label className="text-xs text-[#8490a3] uppercase tracking-wider flex items-center gap-1.5"><TrendingUp size={12}/> Level</label>
                  <input 
                    type="number" 
                    value={selectedUser.level}
                    onChange={(e) => setSelectedUser({...selectedUser, level: parseInt(e.target.value) || 0})}
                    className="w-full bg-[#141b2a] border border-white/10 rounded-lg px-3 py-2 text-white focus:outline-none focus:border-[#8b5cf6]"
                  />
                </div>
                
                <div className="space-y-1.5">
                  <label className="text-xs text-[#8490a3] uppercase tracking-wider flex items-center gap-1.5"><Hash size={12}/> XP</label>
                  <input 
                    type="number" 
                    value={selectedUser.xp}
                    onChange={(e) => setSelectedUser({...selectedUser, xp: parseInt(e.target.value) || 0})}
                    className="w-full bg-[#141b2a] border border-white/10 rounded-lg px-3 py-2 text-white focus:outline-none focus:border-[#8b5cf6]"
                  />
                </div>
              </div>
            </div>

            <div className="pt-4 flex gap-3">
              <button 
                onClick={() => setIsDrawerOpen(false)}
                className="flex-1 py-2 bg-white/5 hover:bg-white/10 text-white rounded-lg transition-colors font-medium"
              >
                Cancel
              </button>
              <button 
                onClick={() => {
                  updateUser(selectedUser.id, selectedUser);
                  addToast({ title: 'Success', message: 'User updated successfully', type: 'success' });
                  setIsDrawerOpen(false);
                }}
                className="flex-1 py-2 bg-[#8b5cf6] hover:bg-[#7c3aed] text-white rounded-lg transition-colors font-medium shadow-[0_0_15px_rgba(139,92,246,0.3)]"
              >
                Save Changes
              </button>
            </div>
          </div>
        )}
      </AdminDrawer>
    </div>
  );
}
