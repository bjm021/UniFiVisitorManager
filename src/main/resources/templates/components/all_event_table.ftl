<#assign currentTime = (.now?long / 1000)?floor>

<#if delete_evt_success??>
    <div class="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded relative mb-4" role="alert">
        <strong class="font-bold">Success!</strong>
        <span class="block sm:inline">The one-time visitor has been deleted successfully.</span>
    </div>
</#if>

<#if delete_evt_error??>
    <div class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative mb-4" role="alert">
        <strong class="font-bold">Error!</strong>
        <span class="block sm:inline">There was an error deleting the one-time visitor. Please try again.</span>
    </div>
</#if>

<div class="overflow-x-auto w-full" id="all-events">
    <table class="min-w-full text-left text-sm whitespace-nowrap">
        <thead class="uppercase tracking-wider border-b-2 border-gray-200 bg-gray-50">
        <tr>
            <th scope="col" class="px-6 py-4 text-gray-600 font-semibold">Name</th>
            <th scope="col" class="px-6 py-4 text-gray-600 font-semibold">Delete & Revoke</th>
            <th scope="col" class="px-6 py-4 text-gray-600 font-semibold">Allowed Date & Time</th>
            <th scope="col" class="px-6 py-4 text-gray-600 font-semibold">Visitors</th>
        </tr>
        </thead>
        <tbody>
        <#list events as id, event>
            <tr class="border-b border-gray-100 hover:bg-gray-50 transition duration-150">
                <td class="px-6 py-4">${event.name!"No Name"}</td>
                <td class="px-6 py-4">
                    <button onclick="openDeleteModal('${event.id!""}', 'event', '${event.name!}', 'all-events')" type="button"
                            class="bg-red-500 hover:bg-red-700 text-white font-bold py-1 px-3 rounded focus:outline-none focus:shadow-outline revoke-btn"
                            data-visitor-id="${event.id!""}">
                        <#if currentTime gt event.end_time>
                            Delete
                        <#else>
                            Revoke & Delete
                        </#if>
                    </button>
                </td>
                <td class="px-6 py-4
                    <#if currentTime gt event.end_time>
                        text-red-600
                    <#elseif currentTime lt event.start_time>
                        text-orange-500
                    <#else>
                        text-green-600
                    </#if>">
                    ${event.prettyDateRange!""}
                </td>
                <td class="px-6 py-4 font-mono text-xs text-gray-500">
                    <#list event.visitors as visitor>
                        ${visitor.firstName!"No Name"} ${visitor.lastName!"No Last Name"} (${visitor.email!"No Email"})<#if visitor?has_next>, </#if>
                    </#list>
                </td>
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
<small class="text-xs text-gray-500">
    Date and time colors: <span class="text-red-600">Expired</span>,
    <span class="text-orange-500">Not yet started</span>,
    <span class="text-green-600">Active</span>
</small>