'use client';

import { useState } from 'react';

const mockUsers = [
  { id: 1, name: 'Dr. Sarah Chen', email: 'sarah@lab.com', status: 'active', role: 'Researcher', joined: '2024-01-15' },
  { id: 2, name: 'Prof. James Wilson', email: 'james@lab.com', status: 'active', role: 'Admin', joined: '2023-11-20' },
  { id: 3, name: 'Dr. Emily Brown', email: 'emily@lab.com', status: 'inactive', role: 'Researcher', joined: '2024-02-10' },
  { id: 4, name: 'Alex Kumar', email: 'alex@lab.com', status: 'active', role: 'Student', joined: '2024-03-05' },
  { id: 5, name: 'Maria Garcia', email: 'maria@lab.com', status: 'active', role: 'Researcher', joined: '2024-01-28' },
  { id: 6, name: 'Dr. Kim Park', email: 'kim@lab.com', status: 'active', role: 'Professor', joined: '2023-12-12' },
];

export default function UsersPage() {
  const [search, setSearch] = useState('');

  const filteredUsers = mockUsers.filter(
    (u) => u.name.toLowerCase().includes(search.toLowerCase()) || u.email.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div className="relative z-10 min-h-screen p-4 md:p-6">
      <div className="max-w-7xl mx-auto">
        <div className="flex items-center justify-between mb-6">
          <h1 className="text-2xl font-bold">Users Management</h1>
          <button className="btn-primary text-sm py-2 px-4">+ Add User</button>
        </div>

        <div className="glass-card p-6">
          <div className="mb-4">
            <input
              type="text"
              placeholder="Search users..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="input-field max-w-sm"
            />
          </div>

          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-border">
                  <th className="text-left py-3 px-4 text-dim font-medium">User</th>
                  <th className="text-left py-3 px-4 text-dim font-medium hidden md:table-cell">Role</th>
                  <th className="text-left py-3 px-4 text-dim font-medium">Status</th>
                  <th className="text-left py-3 px-4 text-dim font-medium hidden md:table-cell">Joined</th>
                  <th className="text-left py-3 px-4 text-dim font-medium">Actions</th>
                </tr>
              </thead>
              <tbody>
                {filteredUsers.map((user) => (
                  <tr key={user.id} className="border-b border-border/50 hover:bg-white/[0.02]">
                    <td className="py-3 px-4">
                      <div className="flex items-center gap-3">
                        <div className="w-8 h-8 rounded-full bg-gradient-to-br from-purple to-violet flex items-center justify-center text-white text-xs font-bold flex-shrink-0">{user.name[0]}</div>
                        <div>
                          <div className="font-medium">{user.name}</div>
                          <div className="text-xs text-dim md:hidden">{user.role}</div>
                          <div className="text-xs text-dim">{user.email}</div>
                        </div>
                      </div>
                    </td>
                    <td className="py-3 px-4 text-muted hidden md:table-cell">{user.role}</td>
                    <td className="py-3 px-4">
                      <span className={`text-[10px] font-medium px-2 py-0.5 rounded-full ${
                        user.status === 'active' ? 'bg-teal/10 text-teal' : 'bg-rose/10 text-rose'
                      }`}>{user.status}</span>
                    </td>
                    <td className="py-3 px-4 text-dim hidden md:table-cell">{user.joined}</td>
                    <td className="py-3 px-4">
                      <button className="text-dim hover:text-foreground transition-colors">⋮</button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
}
