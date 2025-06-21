// File: src/services/roleService.ts

import {api} from './apiService'; // Import ApiService đã được cấu hình sẵn

export interface Permission {
  permissionId: number;
  name: string;
  description: string;
}

export interface Role {
  roleId: number;
  name: string;
  description: string;
  permissions: Permission[];
}

export const roleService = {

  getAllRoles: async (): Promise<Role[]> => {
    type FullApiResponse = {
      code: number;
      message: string;
      result: Role[];
    };
    
    const response = await api.get<FullApiResponse>('role');
    
    return response.result;
  },

};