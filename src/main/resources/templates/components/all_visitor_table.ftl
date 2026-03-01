<div class="overflow-x-auto w-full">
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
        <#list visitors as visitor>
            <tr class="border-b border-gray-100 hover:bg-gray-50 transition duration-150">
                <td class="px-6 py-4">${visitor.firstName!"No Name"} ${visitor.lastName!""}</td>
                <td class="px-6 py-4">${visitor.email!"No Email"}</td>
                <td class="px-6 py-4">${visitor.prettyDateRange!""}</td>
                <td class="px-6 py-4 font-mono text-xs text-gray-500">${visitor.id!"-"}</td>
            </tr>
        <#else>
            <tr>
                <td colspan="4" class="px-6 py-8 text-center text-gray-500">
                    No visitors currently found.
                </td>
            </tr>
        </#list>
        </tbody>
    </table>
</div>