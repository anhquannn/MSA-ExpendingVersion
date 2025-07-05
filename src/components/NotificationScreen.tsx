// src/components/NotificationScreen.tsx
import React, { useState, useContext } from 'react';
import { NotificationCtx } from '../contexts/NotificationContext';
import { useNavigate } from 'react-router-dom';

// Import NotificationPayload to reuse the type defined in context
import type { NotificationPayload, NotificationContextType } from '../contexts/NotificationContext';

interface Product {
  id: string;
  name: string;
  quantity: number;
}

const NotificationScreen: React.FC = () => {
  const { list: notifications, markAllRead } = useContext(NotificationCtx) as NotificationContextType;
  const [selectedNotification, setSelectedNotification] = useState<NotificationPayload | null>(null);
  const navigate = useNavigate();

  // Đánh dấu thông báo là đã đọc khi người dùng nhấp vào
  const handleNotificationClick = (id: string) => {
    const updatedNotifications = notifications.map((notif: NotificationPayload) =>
      notif.id === id ? { ...notif, isRead: true } : notif
    );
    // mark single read is optional; currently markAllRead
    markAllRead();
    setSelectedNotification(updatedNotifications.find(notif => notif.id === id) || null);
  };

  const handleBackToList = () => {
    setSelectedNotification(null);
  };

  return (
    <div className="p-6 bg-gray-50 min-h-screen">
      <div className="max-w-4xl mx-auto bg-white rounded-lg shadow-lg p-6">
        <div className="flex items-center mb-6">
          <button
            onClick={() => navigate(-1)} // Quay lại trang trước
            className="p-2 mr-4 text-gray-600 hover:text-gray-900 focus:outline-none"
            aria-label="Quay lại"
          >
            <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M10 19l-7-7m0 0l7-7m-7 7h18"></path>
            </svg>
          </button>
          <h2 className="text-3xl font-bold text-gray-800">Thông báo của bạn</h2>
        </div>

        {selectedNotification ? (
          <div>
            <button
              onClick={handleBackToList}
              className="mb-4 flex items-center text-green-600 hover:text-green-800 focus:outline-none"
            >
              <svg className="w-5 h-5 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M10 19l-7-7m0 0l7-7m-7 7h18"></path>
              </svg>
              Quay lại danh sách thông báo
            </button>
            <div className="border border-gray-200 rounded-lg p-4 bg-blue-50">
              <h3 className="text-2xl font-semibold text-blue-800 mb-3">Yêu cầu nhập kho từ {selectedNotification.branchName}</h3>
              <p className="text-gray-600 mb-4">Ngày yêu cầu: {selectedNotification.date}</p>
              <h4 className="text-xl font-medium text-gray-700 mb-2">Danh sách sản phẩm:</h4>
              <ul className="space-y-2">
                {selectedNotification.products?.map((product: Product) => (
                  <li key={product.id} className="flex justify-between items-center bg-white p-3 rounded-md shadow-sm">
                    <span className="text-gray-800 font-medium">{product.name}</span>
                    <span className="text-gray-600">Số lượng: <strong className="text-green-700">{product.quantity}</strong></span>
                  </li>
                ))}
              </ul>
            </div>
          </div>
        ) : (
          <div className="space-y-4">
            {notifications.length === 0 ? (
              <p className="text-gray-600 text-center text-lg mt-10">Không có thông báo nào.</p>
            ) : (
              notifications.map((notification: NotificationPayload) => (
                <div
                  key={notification.id}
                  onClick={() => handleNotificationClick(notification.id)}
                  className={`flex items-center p-4 rounded-lg shadow-sm cursor-pointer transition duration-300 ease-in-out
                    ${notification.isRead ? 'bg-gray-100 hover:bg-gray-200' : 'bg-white hover:bg-green-50 border border-green-200'}`}
                >
                  {!notification.isRead && (
                    <div className="w-2 h-2 bg-green-500 rounded-full mr-3 flex-shrink-0"></div>
                  )}
                  <div className="flex-grow">
                    <h3 className="text-lg font-semibold text-gray-800">Yêu cầu nhập kho từ <span className="text-green-600">{notification.branchName}</span></h3>
                    <p className="text-sm text-gray-500 mt-1">
                      Có sản phẩm cần nhập kho. <span className="font-medium">Nhấp để xem chi tiết.</span>
                    </p>
                    <p className="text-xs text-gray-400 mt-1">{notification.date}</p>
                  </div>
                </div>
              ))
            )}
          </div>
        )}
      </div>
    </div>
  );
};

export default NotificationScreen;