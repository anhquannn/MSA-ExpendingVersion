import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Bell, X } from 'lucide-react';
import { NotificationService } from '../services/notificationService';

interface NotificationDto {
  notificationId: number;
  message: string;
  isRead: boolean;
  notificationDate: string;
  notificationType: string;
  title?: string;
}

interface NotificationsProps {}

const Notifications: React.FC<NotificationsProps> = () => {
  const [open, setOpen] = useState(false);
  const queryClient = useQueryClient();

  // Get userId - admin=1, manager from localStorage
  const getUserId = (): number => {
    const userInfo = localStorage.getItem('userInfo');
    if (userInfo) {
      try {
        const parsed = JSON.parse(userInfo);
        return parsed.userId || parsed.id || 1; // fallback to admin
      } catch {
        return 1; // fallback to admin
      }
    }
    return 1; // default admin
  };

  const userId = getUserId();

  /* --- Queries --------------------------------------------------------- */
  const notificationsQuery = useQuery<NotificationDto[]>({
    queryKey: ['notifications', userId, open],
    queryFn: () => NotificationService.getNotificationsByUserId(userId),
    enabled: open,
    refetchInterval: 30000,
  });

  const notifications = notificationsQuery.data ?? [];
  const unreadCount = notifications.filter(n => !n.isRead).length;

  /* --- Mutations ------------------------------------------------------- */
  const markAsReadMutation = useMutation({
    mutationFn: (notificationId: number) => NotificationService.markAsRead(notificationId.toString()),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['notifications', userId] });
    },
  });

  /* --- Handlers -------------------------------------------------------- */
  const toggleDropdown = () => {
    const newOpen = !open;
    setOpen(newOpen);
    if (newOpen) {
      notificationsQuery.refetch();
    }
  };

  const handleNotificationClick = (notification: NotificationDto) => {
    if (!notification.isRead) {
      markAsReadMutation.mutate(notification.notificationId);
    }
  };

  /* --- Render ---------------------------------------------------------- */
  return (
    <div className="relative">
      {/* Bell icon */}
      <button
        onClick={toggleDropdown}
        className="relative p-2 rounded-full text-gray-600 hover:text-green-600 hover:bg-gray-100"
        aria-label="Thông báo"
      >
        <Bell className="w-5 h-5" />
        {unreadCount > 0 && (
          <span className="absolute -top-1 -right-1 bg-red-600 text-white text-xs rounded-full px-1 min-w-[1rem] h-4 flex items-center justify-center">
            {unreadCount > 99 ? '99+' : unreadCount}
          </span>
        )}
      </button>

      {/* Dropdown */}
      {open && (
        <div className="absolute right-0 mt-2 w-80 bg-white rounded-lg shadow-xl z-50 border border-gray-200">
          {/* Header */}
          <div className="flex justify-between items-center px-4 py-3 border-b border-gray-200">
            <span className="font-semibold text-gray-900">Thông báo</span>
            <button 
              onClick={toggleDropdown} 
              className="text-gray-500 hover:text-gray-700 p-1 rounded"
              aria-label="Đóng"
            >
              <X className="w-4 h-4" />
            </button>
          </div>

          {/* Header with unread count */}
          <div className="px-4 py-2 border-b border-gray-200 bg-gray-50">
            <div className="flex justify-between items-center text-sm">
              <span className="text-gray-600">Tất cả thông báo</span>
              {unreadCount > 0 && (
                <span className="bg-red-100 text-red-600 px-2 py-1 rounded-full text-xs font-medium">
                  {unreadCount} chưa đọc
                </span>
              )}
            </div>
          </div>

          {/* List */}
          <div className="max-h-80 overflow-y-auto">
            {notifications.length > 0 ? (
              <div className="divide-y divide-gray-100">
                {notifications.map((notification) => (
                  <div
                    key={notification.notificationId}
                    onClick={() => handleNotificationClick(notification)}
                    className={`p-4 hover:bg-gray-50 cursor-pointer transition-colors relative ${
                      !notification.isRead 
                        ? 'bg-blue-50 border-l-4 border-l-blue-500' 
                        : 'text-gray-600'
                    }`}
                  >
                    {/* Unread indicator */}
                    {!notification.isRead && (
                      <div className="absolute top-4 right-4 w-2 h-2 bg-red-500 rounded-full"></div>
                    )}
                    
                    {/* Notification content */}
                    <div className="pr-6">
                      {notification.title && (
                        <div className={`font-medium text-sm mb-1 ${
                          !notification.isRead ? 'text-gray-900' : 'text-gray-700'
                        }`}>
                          {notification.title}
                        </div>
                      )}
                      <div className={`text-sm leading-relaxed ${
                        !notification.isRead ? 'text-gray-800' : 'text-gray-600'
                      }`}>
                        {notification.message}
                      </div>
                      {notification.notificationDate && (
                        <div className="text-xs text-gray-400 mt-2">
                          {new Date(notification.notificationDate).toLocaleString('vi-VN')}
                        </div>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <div className="p-8 text-center">
                <div className="text-gray-400 mb-3">
                  <Bell className="w-12 h-12 mx-auto opacity-50" />
                </div>
                <p className="text-sm text-gray-500 font-medium">Không có thông báo</p>
                <p className="text-xs text-gray-400 mt-1">Thông báo mới sẽ xuất hiện ở đây</p>
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default Notifications;