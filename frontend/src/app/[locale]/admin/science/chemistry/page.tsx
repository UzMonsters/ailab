'use client';

import React, { useState } from 'react';
import { useRouter, usePathname } from 'next/navigation';
import AdminPageHeader from '@/widgets/admin/AdminPageHeader';
import AdminDataTable, { Column } from '@/widgets/admin/AdminDataTable';
import { ChemistryPanel } from '@/widgets/admin/AdminCatalogPanels';
import { Plus } from 'lucide-react';

import { mockElements } from '@/mocks/admin/chemistryElements';
import { mockSubstances } from '@/mocks/admin/chemistrySubstances';
import { mockReactions } from '@/mocks/admin/chemistryReactions';

const TABS = ['Overview', 'Elements', 'Substances', 'Reactions', 'Properties', 'Hazards', 'Scenarios'];

export default function AdminChemistryPage() {
  const [activeTab, setActiveTab] = useState('Elements');
  const router = useRouter();
  const pathname = usePathname();

  const getColumns = (): Column<any>[] => {
    if (activeTab === 'Elements') {
      return [
        { header: 'ID', accessorKey: 'id', sortable: true },
        { header: 'Name', accessorKey: 'name', sortable: true },
        { header: 'Symbol', accessorKey: 'symbol', sortable: true },
        { header: 'Atomic Number', accessorKey: 'atomicNumber', sortable: true },
        { header: 'Group', accessorKey: 'group', sortable: true },
        { header: 'Status', accessorKey: 'status', sortable: true },
      ];
    }
    if (activeTab === 'Substances') {
      return [
        { header: 'ID', accessorKey: 'id', sortable: true },
        { header: 'Name', accessorKey: 'name', sortable: true },
        { header: 'Formula', accessorKey: 'formula', sortable: true },
        { header: 'Type', accessorKey: 'type', sortable: true },
        { header: 'State', accessorKey: 'state', sortable: true },
        { header: 'Status', accessorKey: 'status', sortable: true },
      ];
    }
    if (activeTab === 'Reactions') {
      return [
        { header: 'ID', accessorKey: 'id', sortable: true },
        { header: 'Name', accessorKey: 'name', sortable: true },
        { header: 'Type', accessorKey: 'type', sortable: true },
        { header: 'Equation', accessorKey: 'equation', sortable: true },
        { header: 'Status', accessorKey: 'status', sortable: true },
      ];
    }
    return [
      { header: 'ID', accessorKey: 'id', sortable: true },
      { header: 'Name', accessorKey: 'name', sortable: true },
    ];
  };

  const getData = () => {
    if (activeTab === 'Elements') return mockElements;
    if (activeTab === 'Substances') return mockSubstances;
    if (activeTab === 'Reactions') return mockReactions;
    return [];
  };

  const handleRowClick = (item: any) => {
    if (activeTab === 'Elements') router.push(`${pathname}/elements/${item.id}`);
    if (activeTab === 'Substances') router.push(`${pathname}/substances/${item.id}`);
    if (activeTab === 'Reactions') router.push(`${pathname}/reactions/${item.id}`);
  };

  const handleAddNew = () => {
    if (activeTab === 'Elements') router.push(`${pathname}/elements/new`);
    if (activeTab === 'Substances') router.push(`${pathname}/substances/new`);
    if (activeTab === 'Reactions') router.push(`${pathname}/reactions/new`);
  };

  return (
    <div className="p-6 h-full flex flex-col">
      <AdminPageHeader 
        title="Chemistry Management"
        description="Manage chemistry elements, substances, reactions, and more."
        counters={[
          { label: 'Elements', value: mockElements.length },
          { label: 'Substances', value: mockSubstances.length },
          { label: 'Reactions', value: mockReactions.length }
        ]}
        actions={
          ['Elements', 'Substances', 'Reactions'].includes(activeTab) && (
            <button 
              onClick={handleAddNew}
              className="flex items-center gap-2 bg-[#8b5cf6] hover:bg-[#7c3aed] text-white px-4 py-2 rounded-lg font-medium transition-colors no-underline"
            >
              <Plus size={18} />
              <span>Add New</span>
            </button>
          )
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
        {['Elements', 'Substances', 'Reactions'].includes(activeTab) ? (
          <AdminDataTable 
            data={getData()}
            columns={getColumns()}
            onRowClick={handleRowClick}
            searchPlaceholder={`Search ${activeTab.toLowerCase()}...`}
          />
        ) : <ChemistryPanel tab={activeTab} />}
      </div>
    </div>
  );
}
