<div class="overflow-x-auto w-full">
    <table class="min-w-full text-left text-sm whitespace-nowrap">
        <thead class="uppercase tracking-wider border-b-2 border-gray-200 bg-gray-50">
        <tr>
            <th scope="col" class="px-6 py-4 text-gray-600 font-semibold">Name</th>
            <th scope="col" class="px-6 py-4 text-gray-600 font-semibold">Email</th>
            <th scope="col" class="px-6 py-4 text-gray-600 font-semibold">Actions</th>
        </tr>
        </thead>
        <tbody>
        <#list privilegedVisitors as id, visitor>
            <tr class="border-b border-gray-100 hover:bg-gray-50 transition duration-150">
                <td class="px-6 py-4">${visitor.firstName!"No Name"} ${visitor.lastName!""}</td>
                <td class="px-6 py-4">${visitor.email!"No Email"}</td>
                <td class="px-6 py-4">
                    <button data-id="${id}" class="delete-pv-btn bg-red-600 hover:bg-red-700 text-white font-bold py-1 px-3 rounded-md transition duration-200">
                        Delete
                    </button>
                </td>
            </tr>
        <#else>
            <tr>
                <td colspan="4" class="px-6 py-8 text-center text-gray-500">
                    No privileged visitors currently found.
                </td>
            </tr>
        </#list>
        </tbody>
    </table>
</div>