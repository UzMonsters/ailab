'use client';

import React, { useState } from 'react';
import AdminPageHeader from '@/widgets/admin/AdminPageHeader';
import AdminDataTable, { Column } from '@/widgets/admin/AdminDataTable';
import AdminDrawer from '@/widgets/admin/AdminDrawer';
import { Plus } from 'lucide-react';
import demoCatalogs from '@/mocks/data/demo-catalogs.json';

const TABS = ['Specimens', 'Processes', 'Equipment'];

// Mock Data
type Specimen = { id: string; name: string; species: string; classification: string };
const mockSpecimens: Specimen[] = demoCatalogs.biology.specimens;

type Process = { id: string; name: string; type: string; duration: string };
const mockProcesses: Process[] = demoCatalogs.biology.processes;

export default function AdminBiologyPage() {
  const [activeTab, setActiveTab] = useState('Specimens');
  const [selectedItem, setSelectedItem] = useState<any | null>(null);

  const getColumns = (): Column<any>[] => {
    if (activeTab === 'Specimens') {
      return [
        { header: 'ID', accessorKey: 'id', sortable: true },
        { header: 'Name', accessorKey: 'name', sortable: true },
        { header: 'Species', accessorKey: 'species', sortable: true },
        { header: 'Classification', accessorKey: 'classification', sortable: true },
      ];
    }
    if (activeTab === 'Processes') {
      return [
        { header: 'ID', accessorKey: 'id', sortable: true },
        { header: 'Name', accessorKey: 'name', sortable: true },
        { header: 'Type', accessorKey: 'type', sortable: true },
        { header: 'Duration', accessorKey: 'duration', sortable: true },
      ];
    }
    return [
      { header: 'ID', accessorKey: 'id', sortable: true },
      { header: 'Name', accessorKey: 'name', sortable: true },
    ];
  };

  const getData = () => {
    if (activeTab === 'Specimens') return mockSpecimens;
    if (activeTab === 'Processes') return mockProcesses;
    return [];
  };

  return (
    <div className="p-6 h-full flex flex-col">
      <AdminPageHeader 
        title="Biology Management"
        description="Manage biology specimens, processes, and specific equipment."
        counters={[
          { label: 'Specimens', value: mockSpecimens.length },
          { label: 'Processes', value: mockProcesses.length }
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
