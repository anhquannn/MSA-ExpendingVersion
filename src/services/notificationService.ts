import { api } from './apiService';
import { supabaseClient } from '../api/baseApi';
import { NotificationType } from '@constants/enums';

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

  static async getNotifications(userId: string): Promise<any[]> {
    try {
      const response = await supabaseClient.get(`/notifications/${userId}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching notifications:', error);
      throw error;
    }
  }

  static async markAsRead(notificationId: string): Promise<void> {
    try {
      const response = await supabaseClient.put(`/notifications/${notificationId}/read`);
      if (response.status === 200) {
        console.log('Notification marked as read');
      }
    } catch (error) {
      console.error('Error marking notification as read:', error);
      throw error;
    }
  }

  static async deleteNotification(notificationId: string): Promise<void> {
    try {
      const response = await supabaseClient.delete(`/notifications/${notificationId}`);
      if (response.status === 200) {
        console.log('Notification deleted successfully');
      }
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
