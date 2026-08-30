'use client';

import React, { useState } from 'react';
import { useAdminStore, Material } from '@/stores/admin.store';
import AdminPageHeader from '@/widgets/admin/AdminPageHeader';
import AdminDataTable, { Column } from '@/widgets/admin/AdminDataTable';
import AdminDrawer from '@/widgets/admin/AdminDrawer';
import { Plus } from 'lucide-react';

const TABS = ['All', 'Liquids', 'Solids', 'Gases', 'Solutions', 'Biological Samples', 'Physical Materials'];

export default function AdminMaterialsPage() {
  const { materials } = useAdminStore();
  const [activeTab, setActiveTab] = useState('All');
  const [selectedItem, setSelectedItem] = useState<Material | null>(null);

  const filteredData = React.useMemo(() => {
    if (activeTab === 'All') return materials;
    const tabMap: Record<string, string> = {
      'Liquids': 'Liquid',
      'Solids': 'Solid',
      'Gases': 'Gas',
      'Solutions': 'Solution',
      'Biological Samples': 'Biological Sample',
      'Physical Materials': 'Physical Material'
    };
    const targetType = tabMap[activeTab];
    if (targetType) {
      return materials.filter(m => m.type === targetType);
    }
    return materials;
  }, [materials, activeTab]);

  const columns: Column<Material>[] = [
    { header: 'ID', accessorKey: 'id', sortable: true },
    { header: 'Name', accessorKey: 'name', sortable: true },
    { 
      header: 'Type', 
      accessorKey: 'type', 
      sortable: true,
      cell: (item) => {
        let colorClass = 'bg-gray-500/20 text-gray-400';
        switch (item.type) {
          case 'Liquid': colorClass = 'bg-blue-500/20 text-blue-400'; break;
          case 'Solid': colorClass = 'bg-orange-500/20 text-orange-400'; break;
          case 'Gas': colorClass = 'bg-teal-500/20 text-teal-400'; break;
          case 'Solution': colorClass = 'bg-indigo-500/20 text-indigo-400'; break;
          case 'Biological Sample': colorClass = 'bg-green-500/20 text-green-400'; break;
          case 'Physical Material': colorClass = 'bg-purple-500/20 text-purple-400'; break;
        }
        return (
          <span className={`px-2 py-1 rounded-md text-xs whitespace-nowrap ${colorClass}`}>
            {item.type}
          </span>
        );
      }
    },
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
        title="Materials Library"
        description="Manage reagents, samples, and consumable items for the laboratory."
        counters={[
          { label: 'Total Items', value: materials.length },
          { label: 'Available', value: materials.filter(m => m.status === 'Available').length }
        ]}
        actions={
          <button className="flex items-center gap-2 bg-[#8b5cf6] hover:bg-[#7c3aed] text-white px-4 py-2 rounded-lg font-medium transition-colors">
            <Plus size={18} />
            <span>Add Material</span>
          </button>
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
          searchPlaceholder="Search materials..."
        />
      </div>

      <AdminDrawer 
        isOpen={!!selectedItem} 
        onClose={() => setSelectedItem(null)}
        title={selectedItem?.name || 'Material Details'}
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
                  <span className="block text-[#8490a3] mb-1">Type</span>
                  <span className="text-white">{selectedItem.type}</span>
                </div>
                <div>
                  <span className="block text-[#8490a3] mb-1">Status</span>
                  <span className="text-white">{selectedItem.status}</span>
                </div>
                <div>
                  <span className="block text-[#8490a3] mb-1">Total Uses</span>
                  <span className="text-white">{selectedItem.uses}</span>
                </div>
              </div>
            </div>
          </div>
        )}
      </AdminDrawer>
    </div>
  );
}
