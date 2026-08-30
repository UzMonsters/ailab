import React, { useState } from 'react';
import { ChevronLeft, ChevronRight, Search, SlidersHorizontal } from 'lucide-react';

export interface Column<T> {
  header: string;
  accessorKey?: keyof T;
  cell?: (item: T) => React.ReactNode;
  sortable?: boolean;
}

interface AdminDataTableProps<T> {
  data: T[];
  columns: Column<T>[];
  onRowClick?: (item: T) => void;
  searchPlaceholder?: string;
  itemsPerPageOptions?: number[];
}

export default function AdminDataTable<T extends { id: string }>({ 
  data, 
  columns, 
  onRowClick,
  searchPlaceholder = 'Search...',
  itemsPerPageOptions = [10, 25, 50, 100]
}: AdminDataTableProps<T>) {
  const [searchTerm, setSearchTerm] = useState('');
  const [itemsPerPage, setItemsPerPage] = useState(itemsPerPageOptions[0]);
  const [currentPage, setCurrentPage] = useState(1);
  const [sortConfig, setSortConfig] = useState<{ key: keyof T | null, direction: 'asc' | 'desc' }>({ key: null, direction: 'asc' });

  // Basic search filter (checks all string/number values in the object)
  const filteredData = React.useMemo(() => {
    return data.filter(item => {
      if (!searchTerm) return true;
      return Object.values(item).some(val => 
        String(val).toLowerCase().includes(searchTerm.toLowerCase())
      );
    });
  }, [data, searchTerm]);

  // Sorting
  const sortedData = React.useMemo(() => {
    let sortableItems = [...filteredData];
    if (sortConfig.key !== null) {
      sortableItems.sort((a, b) => {
        const key = sortConfig.key as keyof T;
        if (a[key] < b[key]) return sortConfig.direction === 'asc' ? -1 : 1;
        if (a[key] > b[key]) return sortConfig.direction === 'asc' ? 1 : -1;
        return 0;
      });
    }
    return sortableItems;
  }, [filteredData, sortConfig]);

  // Pagination
  const totalPages = Math.ceil(sortedData.length / itemsPerPage);
  const currentData = sortedData.slice(
    (currentPage - 1) * itemsPerPage,
    currentPage * itemsPerPage
  );

  const handleSort = (key?: keyof T) => {
    if (!key) return;
    let direction: 'asc' | 'desc' = 'asc';
    if (sortConfig.key === key && sortConfig.direction === 'asc') {
      direction = 'desc';
    }
    setSortConfig({ key, direction });
  };

  return (
    <div className="bg-[#0b101a] border border-[rgba(255,255,255,0.05)] rounded-xl overflow-hidden flex flex-col">
      {/* Toolbar */}
      <div className="p-4 border-b border-[rgba(255,255,255,0.05)] flex flex-wrap items-center justify-between gap-4">
        <div className="relative w-full sm:w-72">
          <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-[#8490a3]" />
          <input 
            type="text" 
            placeholder={searchPlaceholder}
            className="w-full bg-[#141b2a] border border-[rgba(255,255,255,0.1)] rounded-lg pl-9 pr-4 py-2 text-sm text-white placeholder:text-[#8490a3] focus:outline-none focus:border-[#8b5cf6]"
            value={searchTerm}
            onChange={e => setSearchTerm(e.target.value)}
          />
        </div>
        <div className="flex items-center gap-2">
          <button className="flex items-center gap-2 px-3 py-2 text-sm text-[#8490a3] hover:text-white bg-[#141b2a] rounded-lg border border-[rgba(255,255,255,0.05)] transition-colors">
            <SlidersHorizontal size={14} /> Filters
          </button>
        </div>
      </div>

      {/* Table container */}
      <div className="overflow-x-auto">
        <table className="w-full text-left text-sm text-[#a9a5b8]">
          <thead className="bg-[#090d16] text-[#8490a3] text-xs uppercase tracking-wider border-b border-[rgba(255,255,255,0.05)]">
            <tr>
              <th className="p-4 w-12 text-center">
                <input type="checkbox" className="rounded bg-[#141b2a] border-gray-600 text-[#8b5cf6] focus:ring-[#8b5cf6]" />
              </th>
              {columns.map((col, idx) => (
                <th 
                  key={idx} 
                  className={`p-4 font-medium ${col.sortable ? 'cursor-pointer hover:text-white transition-colors' : ''}`}
                  onClick={() => col.sortable && handleSort(col.accessorKey)}
                >
                  <div className="flex items-center gap-1">
                    {col.header}
                    {col.sortable && sortConfig.key === col.accessorKey && (
                      <span className="text-[#8b5cf6]">{sortConfig.direction === 'asc' ? '↑' : '↓'}</span>
                    )}
                  </div>
                </th>
              ))}
            </tr>
          </thead>
          <tbody className="divide-y divide-[rgba(255,255,255,0.03)]">
            {currentData.length > 0 ? currentData.map((row) => (
              <tr 
                key={row.id} 
                className="hover:bg-[rgba(255,255,255,0.02)] transition-colors cursor-pointer group"
                onClick={() => onRowClick?.(row)}
              >
                <td className="p-4 text-center" onClick={e => e.stopPropagation()}>
                  <input type="checkbox" className="rounded bg-[#141b2a] border-gray-600 text-[#8b5cf6] focus:ring-[#8b5cf6]" />
                </td>
                {columns.map((col, idx) => (
                  <td key={idx} className="p-4 whitespace-nowrap group-hover:text-white transition-colors">
                    {col.cell ? col.cell(row) : (col.accessorKey ? row[col.accessorKey] as React.ReactNode : '-')}
                  </td>
                ))}
              </tr>
            )) : (
              <tr>
                <td colSpan={columns.length + 1} className="p-8 text-center text-[#8490a3]">
                  No results found
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      {/* Pagination */}
      <div className="p-4 border-t border-[rgba(255,255,255,0.05)] flex flex-wrap items-center justify-between gap-4 text-sm text-[#8490a3]">
        <div className="flex items-center gap-2">
          <span>Rows per page:</span>
          <select 
            value={itemsPerPage} 
            onChange={e => { setItemsPerPage(Number(e.target.value)); setCurrentPage(1); }}
            className="bg-[#141b2a] border border-[rgba(255,255,255,0.1)] rounded p-1 text-white focus:outline-none"
          >
            {itemsPerPageOptions.map(opt => <option key={opt} value={opt}>{opt}</option>)}
          </select>
        </div>
        
        <div className="flex items-center gap-4">
          <span>{sortedData.length > 0 ? (currentPage - 1) * itemsPerPage + 1 : 0}-{Math.min(currentPage * itemsPerPage, sortedData.length)} of {sortedData.length}</span>
          <div className="flex items-center gap-1">
            <button 
              onClick={() => setCurrentPage(p => Math.max(1, p - 1))}
              disabled={currentPage === 1}
              className="p-1 rounded hover:bg-[#141b2a] disabled:opacity-50 transition-colors"
            >
              <ChevronLeft size={18} />
            </button>
            <button 
              onClick={() => setCurrentPage(p => Math.min(totalPages, p + 1))}
              disabled={currentPage === totalPages || totalPages === 0}
              className="p-1 rounded hover:bg-[#141b2a] disabled:opacity-50 transition-colors"
            >
              <ChevronRight size={18} />
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
