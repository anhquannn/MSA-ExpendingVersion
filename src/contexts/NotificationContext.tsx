import React, { createContext, useState, useEffect } from 'react';
import { onMessage } from 'firebase/messaging';
import { messaging } from '../config/firebaseConfig';
import { toast } from 'react-toastify';

export interface NotificationPayload {
  // Unique identifier for each notification (required for list rendering)
  id: string;
  title: string;
  body: string;
  data?: any;
  receivedAt: number;
  isRead: boolean;
  // Optional fields that our UI may use
  branchName?: string;
  date?: string;
  products?: { id: string; name: string; quantity: number }[];
}

export interface NotificationContextType {
  list: NotificationPayload[];
  unread: number;
  markAllRead: () => void;
}

export const NotificationCtx = createContext<NotificationContextType>({
  list: [],
  unread: 0,
  markAllRead: () => {},
});

export const NotificationProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [list, setList] = useState<NotificationPayload[]>([]);

  // listener once
  useEffect(() => {
    const unsubscribe = onMessage(messaging, (payload) => {
      // Build a unified notification object that matches the UI's expectation
      const data = payload.data || {};
      const noti: NotificationPayload = {
        // Use provided id or fallback to timestamp as unique id
        id: (data.id as string) || Date.now().toString(),
        title: payload.notification?.title || 'Notification',
        body: payload.notification?.body || '',
        data,
        receivedAt: Date.now(),
        isRead: false,
        branchName: data.branchName as string | undefined,
        date: data.date as string | undefined,
        // products might be passed as a JSON string – attempt to parse, otherwise leave undefined
        products: typeof data.products === 'string' ? (() => {
          try {
            return JSON.parse(data.products);
          } catch {
            return undefined;
          }
        })() : (data.products as any),
      };
      setList((prev) => [noti, ...prev]);
      toast.info(noti.title);
    });
    return () => {
      // firebase v9: onMessage returns void (no unsubscribe), so nothing
      // but keep placeholder
      if (typeof unsubscribe === 'function') unsubscribe();
    };
  }, []);

  const markAllRead = () => {
    setList((prev) => prev.map((n) => ({ ...n, isRead: true })));
  };

  const unread = list.filter((n) => !n.isRead).length;

  return (
    <NotificationCtx.Provider value={{ list, unread, markAllRead }}>
      {children}
    </NotificationCtx.Provider>
  );
};
