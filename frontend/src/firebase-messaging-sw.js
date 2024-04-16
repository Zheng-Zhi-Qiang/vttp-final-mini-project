import { initializeApp } from 'https://www.gstatic.com/firebasejs/10.10.0/firebase-app.js';
import { getMessaging, onBackgroundMessage, isSupported } from 'https://www.gstatic.com/firebasejs/10.10.0/firebase-messaging-sw.js';


const app = initializeApp({
    apiKey: "AIzaSyBo6K3-wzeBPBuYk4Gio_2m6tZOrMLav8A",
    authDomain: "buddyfinder-416903.firebaseapp.com",
    projectId: "buddyfinder-416903",
    storageBucket: "buddyfinder-416903.appspot.com",
    messagingSenderId: "320623366748",
    appId: "1:320623366748:web:b5f86a9019781937420c99",
    measurementId: "G-12SSLSP1MW"
});


isSupported().then(isSupported => {

    if (isSupported) {
  
      const messaging = getMessaging(app);
  
      onBackgroundMessage(messaging, ({ notification: { title, body, image } }) => {
        self.registration.showNotification(title, { body, icon: image || '/assets/icons/icon-72x72.png' })
      });
    }
})