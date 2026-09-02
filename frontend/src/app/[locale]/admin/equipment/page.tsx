'use client';

import React, { useState } from 'react';
import Link from 'next/link';
import { useAdminStore, Equipment } from '@/stores/admin.store';
import AdminPageHeader from '@/widgets/admin/AdminPageHeader';
import AdminDataTable, { Column } from '@/widgets/admin/AdminDataTable';
import AdminDrawer from '@/widgets/admin/AdminDrawer';
import { Plus } from 'lucide-react';

const TABS = ['All', 'Chemistry', 'Physics', 'Biology', 'Categories', 'Ports', 'Compatibility'];

export default function AdminEquipmentPage() {
  const { equipment } = useAdminStore();
  const [activeTab, setActiveTab] = useState('All');
  const [selectedItem, setSelectedItem] = useState<Equipment | null>(null);

  const filteredData = React.useMemo(() => {
    if (activeTab === 'All') return equipment;
    if (['Chemistry', 'Physics', 'Biology'].includes(activeTab)) {
      return equipment.filter(e => e.subject === activeTab || e.subject === 'Multiple');
    }
    return equipment;
  }, [equipment, activeTab]);

  const columns: Column<Equipment>[] = [
    { header: 'ID', accessorKey: 'id', sortable: true },
    { header: 'Name', accessorKey: 'name', sortable: true },
    { 
      header: 'Subject', 
      accessorKey: 'subject', 
      sortable: true,
      cell: (item) => (
        <span className={`px-2 py-1 rounded-md text-xs ${
          item.subject === 'Chemistry' ? 'bg-blue-500/20 text-blue-400' :
          item.subject === 'Physics' ? 'bg-purple-500/20 text-purple-400' :
          item.subject === 'Biology' ? 'bg-green-500/20 text-green-400' :
          'bg-gray-500/20 text-gray-400'
        }`}>
          {item.subject}
        </span>
      )
    },
    { header: 'Category', accessorKey: 'category', sortable: true },
    { 
      header: 'Status', 
      accessorKey: 'status', 
      sortable: true,
      cell: (item) => (
        <span className={`px-2 py-1 rounded-md text-xs ${
          item.status === 'Available' ? 'bg-green-500/20 text-green-400' :
          item.status === 'Maintenance' ? 'bg-yellow-500/20 text-yellow-400' :
          'bg-red-500/20 text-red-400'
        }`}>
          {item.status}
        </span>
      )
    },
    { header: 'Uses', accessorKey: 'uses', sortable: true }
  ];

  return (
    <div className="p-6 h-full flex flex-col">
      <AdminPageHeader 
        title="Equipment Management"
        description="Manage laboratory equipment, models, and usage statistics."
        counters={[
          { label: 'Total Items', value: equipment.length },
          { label: 'Available', value: equipment.filter(e => e.status === 'Available').length }
        ]}
        actions={
          <Link href="new" className="flex items-center gap-2 bg-[#8b5cf6] hover:bg-[#7c3aed] text-white px-4 py-2 rounded-lg font-medium transition-colors no-underline">
            <Plus size={18} />
            <span>Add Equipment</span>
          </Link>
        }
        filters={
          <div className="flex bg-[#141b2a] rounded-lg p-1 border border-[rgba(255,255,255,0.05)] w-full overflow-x-auto">
            {TABS.map(tab => (
              <button
                key={tab}
                onClick={() => setActiveTab(tab)}
                className={`px-4 py-1.5 rounded-md text-sm whitespace-nowrap transition-colors ${
                  activeTab === tab 
                    ? 'bg-[#8b5cf6] text-white font-medium shadow-sm' 
                    : 'text-[#8490a3] hover:text-white hover:bg-[rgba(255,255,255,0.02)]'
                }`}
              >
                {tab}
              </button>
            ))}
          </div>
        }
      />
      
      <div className="flex-1">
        <AdminDataTable 
          data={filteredData}
          columns={columns}
          onRowClick={(item) => setSelectedItem(item)}
          searchPlaceholder="Search equipment..."
        />
      </div>

      <AdminDrawer 
        isOpen={!!selectedItem} 
        onClose={() => setSelectedItem(null)}
        title={selectedItem?.name || 'Equipment Details'}
      >
        {selectedItem && (
          <div className="space-y-6 text-[#a9a5b8]">
            <div className="bg-[#141b2a] p-4 rounded-xl border border-[rgba(255,255,255,0.05)]">
              <h3 className="text-white font-medium mb-4">Properties</h3>
              <div className="grid grid-cols-2 gap-4 text-sm">
                <div>
                  <span className="block text-[#8490a3] mb-1">ID</span>
                  <span className="text-white">{selectedItem.id}</span>
                </div>
                <div>
                  <span className="block text-[#8490a3] mb-1">Subject</span>
                  <span className="text-white">{selectedItem.subject}</span>
                </div>
                <div>
                  <span className="block text-[#8490a3] mb-1">Category</span>
                  <span className="text-white">{selectedItem.category}</span>
                </div>
                <div>
                  <span className="block text-[#8490a3] mb-1">Status</span>
                  <span className="text-white">{selectedItem.status}</span>
                </div>
              </div>
            </div>
          </div>
        )}
      </AdminDrawer>
    </div>
  );
}
