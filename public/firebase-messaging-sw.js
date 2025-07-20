importScripts('https://www.gstatic.com/firebasejs/9.23.0/firebase-app-compat.js');
importScripts('https://www.gstatic.com/firebasejs/9.23.0/firebase-messaging-compat.js');

// Initialize Firebase
const firebaseConfig = {
  apiKey: 'AIzaSyBTL1Bm4VRS4-EaViTdFZWJPUuzvFfmkHw',
  authDomain: 'msa-project-54548.firebaseapp.com',
  projectId: 'msa-project-54548',
  storageBucket: 'msa-project-54548.firebasestorage.app',
  messagingSenderId: '1035643912625',
  appId: '1:1035643912625:web:2a5977c76155641969fd90',
  measurementId: 'G-BZSR3QVEP1'
};

// Declare messaging variable in outer scope so it is accessible later
let messaging;

// Initialize Firebase with configuration
try {
  firebase.initializeApp(firebaseConfig);
  messaging = firebase.messaging();
  
  // Handle background messages
  messaging.onBackgroundMessage((payload) => {
    console.log('[firebase-messaging-sw.js] Received background message ', payload);
    const notificationTitle = payload.notification.title;
    const notificationOptions = {
      body: payload.notification.body,
      icon: payload.notification.icon
    };

    self.registration.showNotification(notificationTitle, notificationOptions);
  });
} catch (error) {
  console.error('Firebase initialization failed:', error);
}

// If Firebase initialized successfully, set up background message handler
if (messaging) {
  messaging.onBackgroundMessage((payload) => {
    console.log('[firebase-messaging-sw.js] Received background message ', payload);
    const notificationTitle = payload.notification?.title ?? 'New notification';
    const notificationOptions = {
      body: payload.notification?.body,
      icon: payload.notification?.icon ?? '/icon.png'
    };
    self.registration.showNotification(notificationTitle, notificationOptions);
  });
}


