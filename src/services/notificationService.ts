import { api } from './apiService';
import { NotificationType } from '../constants/enums';

interface NotificationPayload {
  title: string;
  body: string;
  data?: {
    type: NotificationType;
    [key: string]: any;
  };
}

export class NotificationService {
    /**
   * Gửi thông báo thử nghiệm tới 1 user thông qua API backend `/notification/push-test`
   */
  static async sendNotification(
    userId: number,
    payload: NotificationPayload
  ): Promise<void> {
    try {
      await api.post<void>('notification/push-test', {
        userId,
        title: payload.title,
        body: payload.body,
        data: payload.data
      });
      console.log('Notification pushed');
    } catch (error) {
      console.error('Error pushing notification:', error);
      throw error;
    }
  }

  static async getNotificationsPaged(isRead: boolean, page = 1, pageSize = 20): Promise<any[]> {
    try {
      const body = {
        isRead,
        page,
        pageSize,
        sortBy: 'notificationDate',
        sortDirection: 'DESC',
      };
      const response = await api.post<any>('notification/paging', body);
      const list = response.result?.content ?? [];
      return list.map((n: any) => ({ ...n, isRead: n.isRead ?? n.read ?? false }));
    } catch (error) {
      console.error('Error paging notifications:', error);
      throw error;
    }
  }

  static async countUnread(): Promise<number> {
    try {
      const body = { isRead: false, page: 1, pageSize: 1 };
      const response = await api.post<any>('notification/paging', body);
      const list = response.result?.content ?? [];
      return list.map((n: any) => ({ ...n, isRead: n.isRead ?? n.read ?? false }));
    } catch (error) {
      console.error('Error counting unread notifications:', error);
      return 0;
    }
  }
  
  static async getNotificationsByUserId(userId: number, page = 1, pageSize = 50): Promise<any[]> {
    try {
      const body = {
        userId,
        page,
        pageSize,
        sortBy: 'notificationDate',
        sortDirection: 'DESC',
      };
      const response = await api.post<any>('notification/paging', body);
      const list = response.result?.content ?? [];
      return list.map((n: any) => ({ ...n, isRead: n.isRead ?? n.read ?? false }));
    } catch (error) {
      console.error('Error fetching notifications by userId:', error);
      throw error;
    }
  }

  // giữ hàm cũ nhưng đánh dấu deprecated
  /** @deprecated use getNotificationsPaged */
  /*
   * @deprecated Supabase implementation removed. Use getNotificationsByUserId or getNotificationsPaged.
   */
  static async getNotifications(userId: string): Promise<any[]> {
    try {
      const response = await api.get<any>(`notification/user/${userId}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching notifications:', error);
      throw error;
    }
  }

  static async markAsRead(notificationId: string): Promise<void> {
    try {
      // Backend does not expose /read endpoint; update uses main PUT /notification/{id}
      await api.put<void>(`notification/${notificationId}`, { read: true });
      console.log('Notification marked as read');
      
    } catch (error) {
      console.error('Error marking notification as read:', error);
      throw error;
    }
  }

  static async deleteNotification(notificationId: string): Promise<void> {
    try {
      await api.delete<void>(`notification/${notificationId}`);
      console.log('Notification deleted successfully');
    } catch (error) {
      console.error('Error deleting notification:', error);
      throw error;
    }
  }

  /**
   * Đăng ký FCM token với backend để server lưu cho user hiện tại
   */
  static async registerToken(token: string, platform: string = 'WEB'): Promise<void> {
    try {
      await api.post<void>('notification/register-token', { token, platform });
      console.log('FCM token registered');
    } catch (error) {
      console.error('Error registering FCM token:', error);
      throw error;
    }
  }
}

// Example usage:
// NotificationService.sendNotification(
//   'user123',
//   {
//     title: 'New Message',
//     body: 'You have a new message',
//     data: { type: NotificationType.MESSAGE }
//   }
// );
