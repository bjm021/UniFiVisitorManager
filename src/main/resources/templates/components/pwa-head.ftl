<link rel="manifest" href="/manifest.json" crossorigin="use-credentials">

<meta name="theme-color" content="#2563eb">
<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-status-bar-style" content="black-translucent">

<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-status-bar-style" content="black-translucent">
<meta name="apple-mobile-web-app-title" content="VisitorManager">

<link rel="apple-touch-icon" href="/public/img/icon-192.png">

<script>
    if ('serviceWorker' in navigator) {
        window.addEventListener('load', () => {
            navigator.serviceWorker.register('/sw.js')
                .then(reg => console.log('Service Worker registered!', reg))
                .catch(err => console.error('Service Worker failed!', err));
        });
    }
</script>