// A simple Service Worker to satisfy PWA install requirements
self.addEventListener('install', (event) => {
    console.log('[ServiceWorker] Installed');
    self.skipWaiting();
});

self.addEventListener('activate', (event) => {
    console.log('[ServiceWorker] Activated');
});

self.addEventListener('fetch', (event) => {
    // We just let all network requests pass through normally
    // (No offline caching for now since we need live API data)
});