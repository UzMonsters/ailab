'use client';

import React, { useState } from 'react';
import AdminPageHeader from '@/widgets/admin/AdminPageHeader';
import AdminDataTable, { Column } from '@/widgets/admin/AdminDataTable';
import AdminDrawer from '@/widgets/admin/AdminDrawer';
import { Plus } from 'lucide-react';
import demoCatalogs from '@/mocks/data/demo-catalogs.json';

const TABS = ['Models', 'Constants', 'Equipment'];

// Mock Data
type Model = { id: string; name: string; category: string; complexity: string };
const mockModels: Model[] = demoCatalogs.physics.models;

type Constant = { id: string; name: string; symbol: string; value: string; unit: string };
const mockConstants: Constant[] = demoCatalogs.physics.constants;

export default function AdminPhysicsPage() {
  const [activeTab, setActiveTab] = useState('Models');
  const [selectedItem, setSelectedItem] = useState<any | null>(null);

  const getColumns = (): Column<any>[] => {
    if (activeTab === 'Models') {
      return [
        { header: 'ID', accessorKey: 'id', sortable: true },
        { header: 'Name', accessorKey: 'name', sortable: true },
        { header: 'Category', accessorKey: 'category', sortable: true },
        { header: 'Complexity', accessorKey: 'complexity', sortable: true },
      ];
    }
    if (activeTab === 'Constants') {
      return [
        { header: 'ID', accessorKey: 'id', sortable: true },
        { header: 'Name', accessorKey: 'name', sortable: true },
        { header: 'Symbol', accessorKey: 'symbol', sortable: true },
        { header: 'Value', accessorKey: 'value', sortable: true },
        { header: 'Unit', accessorKey: 'unit', sortable: true },
      ];
    }
    return [
      { header: 'ID', accessorKey: 'id', sortable: true },
      { header: 'Name', accessorKey: 'name', sortable: true },
    ];
  };

  const getData = () => {
    if (activeTab === 'Models') return mockModels;
    if (activeTab === 'Constants') return mockConstants;
    return [];
  };

  return (
    <div className="p-6 h-full flex flex-col">
      <AdminPageHeader 
        title="Physics Management"
        description="Manage physics models, constants, and specific equipment."
        counters={[
          { label: 'Models', value: mockModels.length },
          { label: 'Constants', value: mockConstants.length }
        ]}
        actions={
          <button className="flex items-center gap-2 bg-[#8b5cf6] hover:bg-[#7c3aed] text-white px-4 py-2 rounded-lg font-medium transition-colors">
            <Plus size={18} />
            <span>Add New</span>
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
          data={getData()}
          columns={getColumns()}
          onRowClick={(item) => setSelectedItem(item)}
          searchPlaceholder={`Search ${activeTab.toLowerCase()}...`}
        />
      </div>

      <AdminDrawer 
        isOpen={!!selectedItem} 
        onClose={() => setSelectedItem(null)}
        title={selectedItem?.name || 'Item Details'}
      >
        {selectedItem && (
          <div className="space-y-6 text-[#a9a5b8]">
            <div className="bg-[#141b2a] p-4 rounded-xl border border-[rgba(255,255,255,0.05)]">
              <h3 className="text-white font-medium mb-4">Properties</h3>
              <div className="grid grid-cols-2 gap-4 text-sm">
                {Object.entries(selectedItem).map(([key, value]) => (
                  <div key={key}>
                    <span className="block text-[#8490a3] mb-1 capitalize">{key.replace(/([A-Z])/g, ' $1')}</span>
                    <span className="text-white">{String(value)}</span>
                  </div>
                ))}
              </div>
            </div>
          </div>
        )}
      </AdminDrawer>
    </div>
  );
}
