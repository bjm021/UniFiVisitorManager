<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Visitor Access Portal</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-100 flex flex-col items-center justify-center min-h-screen p-4">

<div class="bg-white p-8 rounded-xl shadow-lg w-full max-w-4xl mx-auto">
    <h2 class="text-2xl font-bold mb-6 text-gray-800 text-center">Visitor Access Portal</h2>

    <#if successMessage??>
        <div class="bg-green-50 border-l-4 border-green-500 text-green-700 p-4 mb-6 rounded">
            <p class="font-medium">Success!</p>
            <p class="text-sm">${successMessage}</p>
        </div>
    </#if>

    <#if errorMessage??>
        <div class="bg-red-50 border-l-4 border-red-500 text-red-700 p-4 mb-6 rounded">
            <p class="font-medium">Error!</p>
            <p class="text-sm">${errorMessage}</p>
        </div>
    </#if>

    <form action="/create-visitor" method="post" class="space-y-4">
        <div class="grid grid-cols-2 gap-4">
            <div>
                <label class="block text-sm font-medium text-gray-700">Visitor First Name</label>
                <input type="text" name="first_name" required
                       class="mt-1 block w-full rounded-md border-gray-300 shadow-sm border p-2 focus:border-blue-500 focus:ring-blue-500">
            </div>
            <div>
                <label class="block text-sm font-medium text-gray-700">Visitor Last Name</label>
                <input type="text" name="last_name"
                       class="mt-1 block w-full rounded-md border-gray-300 shadow-sm border p-2 focus:border-blue-500 focus:ring-blue-500">
            </div>
        </div>

        <div>
            <label class="block text-sm font-medium text-gray-700">Visitor Email</label>
            <input type="email" name="email" required
                   class="mt-1 block w-full rounded-md border-gray-300 shadow-sm border p-2 focus:border-blue-500 focus:ring-blue-500">
        </div>

        <div class="grid grid-cols-2 gap-4">
            <div>
                <label class="block text-sm font-medium text-gray-700">Start Time</label>
                <input type="datetime-local" name="startTime" required
                       class="mt-1 block w-full rounded-md border-gray-300 shadow-sm border p-2 text-sm focus:border-blue-500">
            </div>
            <div>
                <label class="block text-sm font-medium text-gray-700">End Time</label>
                <input type="datetime-local" name="endTime" required
                       class="mt-1 block w-full rounded-md border-gray-300 shadow-sm border p-2 text-sm focus:border-blue-500">
            </div>
        </div>

        <div class="flex items-center">
            <input type="checkbox" name="privileged" id="privileged" class="mr-2">
            <label for="privileged" class="text-sm text-gray-700">
                This visitor is a privileged visitor
            </label>
        </div>

        <button type="submit"
                class="w-full mt-6 bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded-md transition duration-200">
            Generate & Send QR
        </button>
    </form>
</div>
<div class="mt-8 bg-white p-6 rounded-xl shadow-lg w-full max-w-4xl mx-auto">
    <h3 class="text-xl font-bold mb-4 text-gray-800">Kaböff Visitors</h3>

    <div class="overflow-x-auto">
        <table class="min-w-full text-left text-sm whitespace-nowrap">
            <thead class="uppercase tracking-wider border-b-2 border-gray-200 bg-gray-50">
            <tr>
                <th scope="col" class="px-6 py-4 text-gray-600 font-semibold">Name</th>
                <th scope="col" class="px-6 py-4 text-gray-600 font-semibold">Email</th>
                <th scope="col" class="px-6 py-4 text-gray-600 font-semibold">Allowed Date & Time</th>
                <th scope="col" class="px-6 py-4 text-gray-600 font-semibold">Visitor ID</th>
            </tr>
            </thead>
            <tbody>
            <#list privileged_users as visitor>
                <tr class="border-b border-gray-100 hover:bg-gray-50 transition duration-150">
                    <td class="px-6 py-4">${visitor.firstName!"No Name"} ${visitor.lastName!""}</td>
                    <td class="px-6 py-4">${visitor.email!"No Email"}</td>
                    <td class="px-6 py-4">${visitor.prettyDateRange!""}</td>
                    <td class="px-6 py-4 font-mono text-xs text-gray-500">${visitor.id!"-"}</td>
                </tr>
            <#else>
                <tr>
                    <td colspan="3" class="px-6 py-8 text-center text-gray-500">
                        No visitors currently found.
                    </td>
                </tr>
            </#list>
            </tbody>
        </table>
    </div>
</div>
<div class="mt-8 bg-white p-6 rounded-xl shadow-lg w-full max-w-4xl mx-auto">
    <h3 class="text-xl font-bold mb-4 text-gray-800">All Visitors</h3>

    <div class="overflow-x-auto">
        <table class="min-w-full text-left text-sm whitespace-nowrap">
            <thead class="uppercase tracking-wider border-b-2 border-gray-200 bg-gray-50">
            <tr>
                <th scope="col" class="px-6 py-4 text-gray-600 font-semibold">Name</th>
                <th scope="col" class="px-6 py-4 text-gray-600 font-semibold">Email</th>
                <th scope="col" class="px-6 py-4 text-gray-600 font-semibold">Visitor ID</th>
            </tr>
            </thead>
            <tbody>
            <#list visitors as visitor>
                <tr class="border-b border-gray-100 hover:bg-gray-50 transition duration-150">
                    <td class="px-6 py-4">${visitor.firstName!"No Name"} ${visitor.lastName!""}</td>
                    <td class="px-6 py-4">${visitor.email!"No Email"}</td>
                    <td class="px-6 py-4 font-mono text-xs text-gray-500">${visitor.id!"-"}</td>
                </tr>
            <#else>
                <tr>
                    <td colspan="3" class="px-6 py-8 text-center text-gray-500">
                        No visitors currently found.
                    </td>
                </tr>
            </#list>
            </tbody>
        </table>
    </div>
</div>



</body>
</html>