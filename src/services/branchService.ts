// src/services/branchService.ts

import { Branch, mockBranchesData, BranchFormData } from '../types/branch';

// Giả lập ID tự tăng
let nextBranchId = mockBranchesData.length > 0 ? Math.max(...mockBranchesData.map(b => parseInt(b.id.substring(1)))) + 1 : 1;

const simulateDelay = (ms: number) => new Promise(res => setTimeout(res, ms));

interface PaginationParams {
  page: number;
  limit: number;
  search?: string;
}

interface PaginatedResponse<T> {
  data: T[];
  total: number;
  page: number;
  limit: number;
}

const branchService = {
  // Lấy danh sách chi nhánh có phân trang và tìm kiếm
  async getBranches({ page, limit, search }: PaginationParams): Promise<PaginatedResponse<Branch>> {
    await simulateDelay(500);

    let filteredBranches = [...mockBranchesData];

    if (search) {
      const lowercasedSearch = search.toLowerCase();
      filteredBranches = filteredBranches.filter(
        branch =>
          branch.name.toLowerCase().includes(lowercasedSearch) ||
          branch.id.toLowerCase().includes(lowercasedSearch) ||
          branch.city.toLowerCase().includes(lowercasedSearch) ||
          branch.phone.includes(lowercasedSearch) ||
          branch.email.toLowerCase().includes(lowercasedSearch)
      );
    }

    const startIndex = (page - 1) * limit;
    const endIndex = startIndex + limit;
    const paginatedBranches = filteredBranches.slice(startIndex, endIndex);

    return {
      data: paginatedBranches,
      total: filteredBranches.length,
      page,
      limit,
    };
  },

  // Lấy một chi nhánh theo ID
  async getBranchById(id: string): Promise<Branch | undefined> {
    await simulateDelay(300);
    return mockBranchesData.find(b => b.id === id);
  },

  // Thêm chi nhánh mới
  async addBranch(branchData: Omit<BranchFormData, 'id'>): Promise<Branch> {
    await simulateDelay(500);
    const newBranch: Branch = {
      ...branchData,
      id: `B${String(nextBranchId++).padStart(3, '0')}`,
      status: 'active', // Mặc định trạng thái là active khi thêm mới
    };
    mockBranchesData.push(newBranch);
    return newBranch;
  },

  // Cập nhật chi nhánh
  async updateBranch(id: string, updatedFields: Partial<BranchFormData>): Promise<Branch> {
    await simulateDelay(500);
    const index = mockBranchesData.findIndex(b => b.id === id);
    if (index === -1) {
      throw new Error('Branch not found');
    }
    const currentBranch = mockBranchesData[index];
    const updatedBranch = {
      ...currentBranch,
      ...updatedFields,
      id: currentBranch.id, // Đảm bảo ID không đổi
    } as Branch; // Ép kiểu lại để đảm bảo là Branch
    mockBranchesData[index] = updatedBranch;
    return updatedBranch;
  },

  // Xóa chi nhánh
  async deleteBranch(id: string): Promise<void> {
    await simulateDelay(300);
    const initialLength = mockBranchesData.length;
    const index = mockBranchesData.findIndex(b => b.id === id);
    if (index !== -1) {
      mockBranchesData.splice(index, 1);
    }
    if (mockBranchesData.length === initialLength) {
      throw new Error('Branch not found');
    }
  },

  // Cập nhật trạng thái chi nhánh
  async updateBranchStatus(id: string, status: 'active' | 'inactive'): Promise<Branch> {
    await simulateDelay(300);
    const index = mockBranchesData.findIndex(b => b.id === id);
    if (index === -1) {
      throw new Error('Branch not found');
    }
    const branchToUpdate = mockBranchesData[index];
    branchToUpdate.status = status;
    return branchToUpdate;
  }
};

export default branchService;