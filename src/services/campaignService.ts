import { api } from './apiService';
import { PagedResponse } from './categoryService';

export interface Campaign {
  
  campaignId?: number;
  name: string;
  description: string;
  status: string;
  scopeType: 'ALL' | 'CATEGORY' | 'SUPPLIER';
  minOrderValue: number;
  startDate: string;
  endDate: string;
  createdAt?: string;
  updatedAt?: string;
  targets?: CampaignTarget[];
}

export interface CampaignTarget {
  campaignTargetId?: number;
  campaignId: number;
  targetType: 'CATEGORY' | 'SUPPLIER';
  targetId: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface CampaignFilter {
  name?: string;
  status?: string;
  fromDate?: string;
  toDate?: string;
  keyword?: string;
  sortBy?: string;
  sortDirection?: 'ASC' | 'DESC';
  page: number;
  pageSize: number;
}

export type CampaignPayload = Omit<Campaign, 'campaignId' | 'createdAt' | 'updatedAt'>;

// ✅ Hàm format ngày với giờ chỉ định
export const formatDateWithTime = (dateInput: Date | string, hour: number, minute: number, second: number): string => {
  const pad = (n: number) => n.toString().padStart(2, '0');
  const d = typeof dateInput === 'string' ? new Date(dateInput) : new Date(dateInput);
  d.setHours(hour, minute, second, 0);
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(hour)}:${pad(minute)}:${pad(second)}`;
};

// ✅ Hàm lấy mặc định hôm nay lúc 00:00:00
export const getTodayStartDate = (): string => {
  return formatDateWithTime(new Date(), 0, 0, 0);
};

// ✅ Hàm lấy mặc định hôm nay lúc 23:59:59
export const getTodayEndDate = (): string => {
  return formatDateWithTime(new Date(), 23, 59, 59);
};

export const campaignService = {
  // Campaign endpoints
  getCampaigns: async (): Promise<Campaign[]> => {
    type FullApiResponse = {
      code: number;
      message: string;
      result: Campaign[];
    };

    const response = await api.post<FullApiResponse>('campaign', {}); // Không có filter
    return Array.isArray(response.result) ? response.result : [];
  },

  getCampaignsWithPaging: async (filter: CampaignFilter): Promise<PagedResponse<Campaign>> => {
    type FullApiResponse = {
      code: number;
      message: string;
      result: PagedResponse<Campaign>;
    };

    const response = await api.post<FullApiResponse>('campaign/paging', {
      ...filter,
      sortBy: filter.sortBy || 'startDate',
      sortDirection: filter.sortDirection || 'DESC',
    });

    return response.result;
  },

  getAllCampaigns: async (filter: Omit<CampaignFilter, 'page' | 'pageSize'>): Promise<Campaign[]> => {
    type FullApiResponse = {
      code: number;
      message: string;
      result: Campaign[];
    };

    const response = await api.post<FullApiResponse>('campaign/list', filter);
    return response.result;
  },

  getCampaignById: async (campaignId: number): Promise<Campaign> => {
    type FullApiResponse = {
      code: number;
      message: string;
      result: Campaign;
    };

    const response = await api.get<FullApiResponse>(`campaign/${campaignId}`);
    return response.result;
  },

  createCampaign: async (payload: CampaignPayload): Promise<Campaign> => {
    type FullApiResponse = {
      code: number;
      message: string;
      result: Campaign;
    };

    const formattedPayload = {
      ...payload,
      startDate: formatDateWithTime(payload.startDate, 0, 0, 0),
      endDate: formatDateWithTime(payload.endDate, 23, 59, 59),
    };

    const response = await api.post<FullApiResponse>('campaign', formattedPayload);
    return response.result;
  },

  updateCampaign: async (campaignId: number, payload: CampaignPayload): Promise<Campaign> => {
    type FullApiResponse = {
      code: number;
      message: string;
      result: Campaign;
    };

    const formattedPayload = {
      ...payload,
      startDate: formatDateWithTime(payload.startDate, 0, 0, 0),
      endDate: formatDateWithTime(payload.endDate, 23, 59, 59),
    };

    const response = await api.put<FullApiResponse>(`campaign/${campaignId}`, formattedPayload);
    return response.result;
  },

  deleteCampaign: async (campaignId: number): Promise<void> => {
    await api.delete<void>(`campaign/${campaignId}`);
  },

  // Campaign Target endpoints
  createCampaignTarget: async (target: CampaignTarget): Promise<CampaignTarget> => {
    type FullApiResponse = {
      code: number;
      message: string;
      result: CampaignTarget;
    };

    const cleanedTarget = {
      ...target,
      campaignTargetId: undefined // id sẽ được sinh ở backend
    };

    // Backend dùng endpoint /campaign-target
    const response = await api.post<FullApiResponse>('campaign-target', cleanedTarget);
    return response.result;
  },

  updateCampaignTarget: async (campaignTargetId: number, target: CampaignTarget): Promise<CampaignTarget> => {
    type FullApiResponse = {
      code: number;
      message: string;
      result: CampaignTarget;
    };

    // Backend chỉ mong đợi campaignId, targetType, targetId trong body
    const payload = {
      campaignId: target.campaignId,
      targetType: target.targetType,
      targetId: target.targetId,
    };

    const response = await api.put<FullApiResponse>(`campaign-target/${campaignTargetId}`, payload);
    return response.result;
  },

  deleteCampaignTarget: async (campaignTargetId: number): Promise<void> => {
    await api.delete<void>(`campaign-target/${campaignTargetId}`);
  },

  /**
   * Lấy danh sách campaign target của một campaign.
   * Backend hiện tại không có endpoint GET /campaign/{id}/targets.
   * Thay vào đó, chúng ta gọi POST /campaign-target/list với body filter { campaignId }.
   */
  getCampaignTargets: async (campaignId: number): Promise<CampaignTarget[]> => {
    type FullApiResponse = {
      code: number;
      message: string;
      result: CampaignTarget[];
    };

    const filterPayload = {
      campaignId,
      page: 1,
      pageSize: 1000, // lấy tối đa 1000 target, đủ cho hầu hết các trường hợp
    };

    const response = await api.post<FullApiResponse>('campaign-target/list', filterPayload);
    return response.result;
  },
} as const;
