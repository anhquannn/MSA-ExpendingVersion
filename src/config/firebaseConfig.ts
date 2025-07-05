// src/config/firebaseConfig.ts
import { initializeApp } from 'firebase/app';
import { getMessaging, getToken, onMessage } from 'firebase/messaging';
import firebaseConfigLocal from './firebaseConfigLocal';

// Use local Firebase configuration
const firebaseConfig = firebaseConfigLocal;

// Initialize Firebase
const app = initializeApp(firebaseConfig);

// Initialize Firebase Cloud Messaging
const messaging = getMessaging(app);

// Handle messages when the app is in the foreground
onMessage(messaging, (payload) => {
  console.log('Received foreground message:', payload);
});

// FCM token constants
export const FCM_TOKEN_KEY = 'fcm_token';

// Get FCM token from localStorage
export const getSavedFCMToken = () => {
  return localStorage.getItem(FCM_TOKEN_KEY);
};

// Save FCM token to localStorage
export const saveFCMToken = (token: string) => {
  localStorage.setItem(FCM_TOKEN_KEY, token);
};

// Get FCM token and save to localStorage
export const getFCMToken = async () => {
  try {
    // Check if token exists in localStorage
    const savedToken = getSavedFCMToken();
    if (savedToken) {
      console.log('Using saved FCM token:', savedToken);
      return savedToken;
    }

    // Check notification permission
    const permission = await Notification.requestPermission();
    if (permission !== 'granted') {
      console.log('Notification permission denied');
      return null;
    }

    // Check if Service Worker is supported
    if (!navigator.serviceWorker) {
      console.log('Service Worker not supported');
      return null;
    }

    // Register Service Worker
    const registration = await navigator.serviceWorker.register('/firebase-messaging-sw.js', {
      scope: '/'
    });
    if (!registration) {
      console.log('Service Worker registration failed');
      return null;
    }

    // Get token with proper configuration
    const currentToken = await getToken(messaging, {
      vapidKey: process.env.REACT_APP_VAPID_KEY,
      serviceWorkerRegistration: registration
    });

    if (currentToken) {
      console.log('New FCM token received:', currentToken);
      saveFCMToken(currentToken);
      return currentToken;
    } else {
      console.log('No FCM token available. Requesting permission to send notifications.');
      return null;
    }
  } catch (error) {
    console.error('An error occurred while retrieving FCM token:', error);
    return null;
  }
};

function urlBase64ToUint8Array(base64String: string) {
  const padding = '='.repeat((4 - (base64String.length % 4)) % 4);
  const base64 = (base64String + padding)
    .replace(/\-/g, '+')
    .replace(/_/g, '/');

  const rawData = window.atob(base64);
  const outputArray = new Uint8Array(rawData.length);

  for (let i = 0; i < rawData.length; ++i) {
    outputArray[i] = rawData.charCodeAt(i);
  }
  return outputArray;
}


// Hàm xử lý thông báo khi app đang chạy
export const setupFCM = () => {
  onMessage(messaging, (payload) => {
    console.log('Received foreground message:', payload);
    // Xử lý thông báo khi app đang chạy ở chế độ foreground
  });
};

export { app, messaging };
