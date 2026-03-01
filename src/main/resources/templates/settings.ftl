<#import "components/footer.ftl" as f>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Visitor Access Portal</title>
    <script src="https://cdn.tailwindcss.com"></script>

    <#include "components/pwa-head.ftl">
</head>
<body class="bg-gray-100 flex flex-col items-center justify-center min-h-screen p-4">

<div class="bg-white p-8 rounded-xl shadow-lg w-full max-w-4xl mx-auto">
    <img src="/images/Ubiquiti-Logo.png" alt="UniFi Access App Logo" class="w-35 h-auto mb-6 mx-auto max-h-60">
    <h2 class="text-2xl font-bold mb-6 text-gray-800 text-center">Visitor Access Portal</h2>

    <#if successMessage??>
        <div class="bg-green-50 border-l-4 border-green-500 text-green-700 p-4 mb-6 rounded">
            <p class="font-medium">Success!</p>
            <p class="text-sm">${successMessage}</p>
            <p class="text-sm">The App will now restart...</p>
            <p class="text-sm">A page reload will be triggered in 10 seconds...</p>
            <script>
                let countdown = 10;
                setInterval(function() {
                    // check if the page is reachable (if so skip countdown if not reload after 10s
                    fetch('/').then(response => {
                        if (response.ok) {
                            window.location.reload();
                        }
                    }).catch(error => {
                        console.error('Error checking page:', error);
                    });
                    countdown--;
                    if (countdown === 0) {
                        window.location.reload();
                    }
                }, 1000);
            </script>
            <button class="mt-4 bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded" onclick="window.location.href = '/';">
                Reload now!
            </button>
        </div>
    </#if>

    <#if errorMessage??>
        <div class="bg-red-50 border-l-4 border-red-500 text-red-700 p-4 mb-6 rounded">
            <p class="font-medium">Error!</p>
            <p class="text-sm">${errorMessage}</p>
        </div>
    </#if>

    <#if configFailed?? && !successMessage??>
        <div class="bg-red-50 border-l-4 border-red-500 text-red-700 p-4 mb-6 rounded">
            <p class="font-medium">Error! The App could not connect to the API</p>
            <p class="text-sm">The App could not connect to the UniFi Access App API. Please check the following:</p>
            <ul>
                <li>The App is running on the same network as the UniFi Access App</li>
                <li>The App is running on port 8080</li>
                <li>The App is able to connect to the UniFi Access App API</li>
            </ul>
            <p class="text-sm">
                Please verify your settings and try again. If the problem persists, please check the App logs for more
                details.
            </p>
            <hr>
            <p class="text-sm">
                Details: ${configFailed}
            </p>
        </div>
    </#if>

    <p>
        This seems to be the first time you are using this portal. Please enter the hostname of your UniFi console and
        the access token you generated in the UniFi Access App Api settings. This will allow us to create a visitor
        access QR code
        and send it to the visitor's email address. The QR code will grant temporary access to your building for the
        specified duration.
    </p>
    <br>


    <form action="/update-settings" method="post" class="space-y-6">

        <div>
            <label class="block text-sm font-medium text-gray-700">Hostname (with port)</label>
            <input type="text"
                   name="hostname"
                   placeholder="e.g. 192.168.1.100:12445"
                   required
                   class="mt-1 block w-full rounded-md border-gray-300 shadow-sm border p-3 focus:border-blue-500 focus:ring-blue-500 text-gray-900">
            <p class="mt-1 text-xs text-gray-500">This is normally the hostname of your UniFi Console and the default
                port of 12445.</p>
        </div>

        <div>
            <label class="block text-sm font-medium text-gray-700">Access Token</label>
            <input type="password"
                   name="token"
                   placeholder="Enter your security token"
                   required
                   class="mt-1 block w-full rounded-md border-gray-300 shadow-sm border p-3 focus:border-blue-500 focus:ring-blue-500 text-gray-900">
        </div>

        <button type="submit"
                class="w-full bg-blue-600 hover:bg-blue-700 text-white font-bold py-3 px-4 rounded-md transition duration-200 shadow-md">
            Save Settings and Restart
        </button>
    </form>
</div>

<@f.footer/>
</body>
</html>