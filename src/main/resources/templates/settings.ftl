<#import "components/footer.ftl" as f>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Visitor Access Portal Setup</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <style>
        /* Hide the default HTML5 details arrow */
        details > summary { list-style: none; }
        details > summary::-webkit-details-marker { display: none; }
    </style>
    <#include "components/pwa-head.ftl">
</head>
<body class="bg-gray-100 flex flex-col items-center justify-center min-h-screen p-4">

<div class="bg-white p-8 rounded-xl shadow-lg w-full max-w-4xl mx-auto my-8">
    <img src="/images/Ubiquiti-Logo.png" alt="UniFi Access App Logo" class="w-35 h-auto mb-6 mx-auto max-h-40 object-contain">
    <h2 class="text-3xl font-bold mb-2 text-gray-800 text-center">Visitor Access Portal</h2>
    <p class="text-center text-gray-500 mb-8">System Configuration & Integration Setup</p>

    <#if successMessage??>
        <div class="bg-green-50 border-l-4 border-green-500 text-green-700 p-4 mb-6 rounded shadow-sm">
            <p class="font-bold">Success!</p>
            <p class="text-sm">${successMessage}</p>
            <p class="text-sm mt-2 font-medium">The App will now restart. Reloading in 10 seconds...</p>
            <script>
                let countdown = 10;
                setInterval(function() {
                    fetch('/').then(response => {
                        if (response.ok) window.location.reload();
                    }).catch(error => console.error('Error checking page:', error));
                    countdown--;
                    if (countdown === 0) window.location.reload();
                }, 1000);
            </script>
            <button class="mt-4 bg-green-600 hover:bg-green-700 text-white font-bold py-2 px-4 rounded transition" onclick="window.location.href = '/';">
                Reload now
            </button>
        </div>
    </#if>

    <#if errorMessage??>
        <div class="bg-red-50 border-l-4 border-red-500 text-red-700 p-4 mb-6 rounded shadow-sm">
            <p class="font-bold">Error!</p>
            <p class="text-sm">${errorMessage}</p>
        </div>
    </#if>

    <#if configFailed?? && !successMessage??>
        <div class="bg-red-50 border-l-4 border-red-500 text-red-700 p-4 mb-6 rounded shadow-sm">
            <p class="font-bold">Connection Failed</p>
            <p class="text-sm mb-2">Could not connect to the UniFi Access App API. Please verify:</p>
            <ul class="list-disc ml-5 text-sm mb-4 space-y-1">
                <li>The App is on the same network as the UniFi Console</li>
                <li>The correct API Token is being used</li>
                <li>The hostname includes the correct port (usually 12445)</li>
            </ul>
            <hr class="border-red-200 mb-2">
            <p class="text-xs font-mono bg-red-100 p-2 rounded">Details: ${configFailed}</p>
        </div>
    </#if>

    <div class="bg-blue-50 text-blue-800 p-4 rounded-md mb-8 text-sm leading-relaxed border border-blue-100">
        <p><strong>Welcome!</strong> Please enter your UniFi Console hostname and Access Token to get started.
            You can also optionally configure Apple Wallet integration and an SMTP Email provider to automatically send beautiful digital passes to your visitors.</p>
    </div>

    <form action="/update-settings" method="post" class="space-y-4">

        <details class="group border border-gray-200 rounded-lg shadow-sm" open>
            <summary class="flex justify-between items-center font-semibold cursor-pointer p-4 text-gray-800 bg-gray-50 rounded-lg group-open:rounded-b-none group-open:border-b border-gray-200 transition-colors hover:bg-gray-100">
                <div class="flex items-center gap-2">
                    <svg class="w-5 h-5 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 12h14M5 12a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v4a2 2 0 01-2 2M5 12a2 2 0 00-2 2v4a2 2 0 002 2h14a2 2 0 002-2v-4a2 2 0 00-2-2m-2-4h.01M17 16h.01"></path></svg>
                    UniFi Console Connection (Required)
                </div>
                <span class="transition-transform duration-300 group-open:-rotate-180">
                    <svg class="w-5 h-5 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path></svg>
                </span>
            </summary>
            <div class="p-6 space-y-4 bg-white rounded-b-lg">
                <div>
                    <label class="block text-sm font-medium text-gray-700">Hostname (with port)</label>
                    <input type="text" name="hostname" placeholder="e.g. 192.168.1.100:12445" required
                           class="mt-1 block w-full rounded-md border-gray-300 shadow-sm border p-2.5 focus:border-blue-500 focus:ring-blue-500 text-gray-900">
                    <p class="mt-1 text-xs text-gray-500">The IP/Hostname of your UniFi Console and the Access API port.</p>
                </div>
                <div>
                    <label class="block text-sm font-medium text-gray-700">Access Token</label>
                    <input type="password" name="token" placeholder="Enter your UniFi security token" required
                           class="mt-1 block w-full rounded-md border-gray-300 shadow-sm border p-2.5 focus:border-blue-500 focus:ring-blue-500 text-gray-900">
                </div>
            </div>
        </details>

        <details class="group border border-gray-200 rounded-lg shadow-sm">
            <summary class="flex justify-between items-center font-semibold cursor-pointer p-4 text-gray-800 bg-gray-50 rounded-lg group-open:rounded-b-none group-open:border-b border-gray-200 transition-colors hover:bg-gray-100">
                <div class="flex items-center gap-2">
                    <svg class="w-5 h-5 text-gray-800" fill="currentColor" viewBox="0 0 24 24"><path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-1 17.93c-3.95-.49-7-3.85-7-7.93 0-.62.08-1.21.21-1.79L9 15v1c0 1.1.9 2 2 2v1.93zm6.9-2.54c-.26-.81-1-1.39-1.9-1.39h-1v-3c0-.55-.45-1-1-1H8v-2h2c.55 0 1-.45 1-1V7h2c1.1 0 2-.9 2-2v-.41c2.93 1.19 5 4.06 5 7.41 0 2.08-.8 3.97-2.1 5.39z"/></svg>
                    Apple Wallet Integration (Optional)
                </div>
                <span class="transition-transform duration-300 group-open:-rotate-180">
                    <svg class="w-5 h-5 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path></svg>
                </span>
            </summary>
            <div class="p-6 space-y-4 bg-white rounded-b-lg grid grid-cols-1 md:grid-cols-2 gap-4">
                <div class="md:col-span-2 bg-gray-50 p-3 rounded text-sm text-gray-600 border border-gray-200">
                    Leave these blank if you do not wish to generate native <strong>.pkpass</strong> files for Apple & Google Wallet.
                </div>

                <div>
                    <label class="block text-sm font-medium text-gray-700">Team ID</label>
                    <input type="text" name="appleTeamId" placeholder="e.g. ABCDE12345" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm border p-2.5 focus:border-blue-500 focus:ring-blue-500 text-gray-900">
                </div>
                <div>
                    <label class="block text-sm font-medium text-gray-700">Pass Type ID</label>
                    <input type="text" name="applePassTypeId" placeholder="pass.de.yourdomain.visitor" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm border p-2.5 focus:border-blue-500 focus:ring-blue-500 text-gray-900">
                </div>
                <div class="md:col-span-2">
                    <label class="block text-sm font-medium text-gray-700">Organization Name (Displayed on Pass)</label>
                    <input type="text" name="appleOrgName" placeholder="e.g. Meyer&Meyer" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm border p-2.5 focus:border-blue-500 focus:ring-blue-500 text-gray-900">
                </div>
                <div class="md:col-span-2">
                    <label class="block text-sm font-medium text-gray-700">Path to .p12 Certificate file</label>
                    <input type="text" name="appleP12Path" placeholder="/certs/certificate.p12" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm border p-2.5 focus:border-blue-500 focus:ring-blue-500 font-mono text-sm text-gray-900">
                </div>
                <div>
                    <label class="block text-sm font-medium text-gray-700">.p12 Password</label>
                    <input type="password" name="appleP12Password" placeholder="Cert password" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm border p-2.5 focus:border-blue-500 focus:ring-blue-500 text-gray-900">
                </div>
                <div>
                    <label class="block text-sm font-medium text-gray-700">Path to Apple WWDR .cer file</label>
                    <input type="text" name="appleWwdrPath" placeholder="/certs/AppleWWDRCA.cer" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm border p-2.5 focus:border-blue-500 focus:ring-blue-500 font-mono text-sm text-gray-900">
                </div>
            </div>
        </details>

        <details class="group border border-gray-200 rounded-lg shadow-sm">
            <summary class="flex justify-between items-center font-semibold cursor-pointer p-4 text-gray-800 bg-gray-50 rounded-lg group-open:rounded-b-none group-open:border-b border-gray-200 transition-colors hover:bg-gray-100">
                <div class="flex items-center gap-2">
                    <svg class="w-5 h-5 text-red-500" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"></path></svg>
                    SMTP Email Provider (Optional)
                </div>
                <span class="transition-transform duration-300 group-open:-rotate-180">
                    <svg class="w-5 h-5 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path></svg>
                </span>
            </summary>
            <div class="p-6 space-y-4 bg-white rounded-b-lg grid grid-cols-1 md:grid-cols-2 gap-4">
                <div class="md:col-span-2 bg-gray-50 p-3 rounded text-sm text-gray-600 border border-gray-200">
                    Configure your SMTP relay to automatically email passes to your visitors.
                </div>

                <div class="md:col-span-1">
                    <label class="block text-sm font-medium text-gray-700">SMTP Host</label>
                    <input type="text" name="smtpHost" placeholder="smtp.gmail.com" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm border p-2.5 focus:border-blue-500 focus:ring-blue-500 text-gray-900">
                </div>
                <div class="md:col-span-1">
                    <label class="block text-sm font-medium text-gray-700">Port</label>
                    <input type="number" name="smtpPort" placeholder="587" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm border p-2.5 focus:border-blue-500 focus:ring-blue-500 text-gray-900">
                </div>
                <div class="md:col-span-2">
                    <label class="block text-sm font-medium text-gray-700">Sender Name</label>
                    <input type="text" name="smtpFromName" placeholder="Visitor Manager" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm border p-2.5 focus:border-blue-500 focus:ring-blue-500 text-gray-900">
                </div>
                <div class="md:col-span-1">
                    <label class="block text-sm font-medium text-gray-700">SMTP Username / Email</label>
                    <input type="text" name="smtpUser" placeholder="noreply@domain.com" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm border p-2.5 focus:border-blue-500 focus:ring-blue-500 text-gray-900">
                </div>
                <div class="md:col-span-1">
                    <label class="block text-sm font-medium text-gray-700">SMTP Password</label>
                    <input type="password" name="smtpPass" placeholder="App Password" class="mt-1 block w-full rounded-md border-gray-300 shadow-sm border p-2.5 focus:border-blue-500 focus:ring-blue-500 text-gray-900">
                </div>
            </div>
        </details>

        <button type="submit" class="w-full mt-4 bg-blue-600 hover:bg-blue-700 text-white font-bold py-3 px-4 rounded-md transition duration-200 shadow-md flex justify-center items-center gap-2 text-lg">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7H5a2 2 0 00-2 2v9a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2h-3m-1 4l-3 3m0 0l-3-3m3 3V4"></path></svg>
            Save Configuration & Restart
        </button>
    </form>
</div>

<@f.footer/>
</body>
</html>