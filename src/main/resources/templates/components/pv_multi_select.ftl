<div class="w-full">
    <label class="block text-sm font-medium text-gray-700 mb-2">Select Visitors</label>

    <div class="max-h-48 overflow-y-auto border border-gray-300 rounded-md bg-white shadow-sm">

        <#if privilegedVisitors?? && privilegedVisitors?has_content>
            <#list privilegedVisitors?values as pv>
                <label class="flex items-center px-4 py-3 border-b border-gray-200 hover:bg-blue-50 cursor-pointer transition-colors last:border-b-0">
                    <input type="checkbox" name="pv_ids" value="${pv.id}" class="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500">

                    <div class="ml-3 flex flex-col">
                        <span class="text-sm font-medium text-gray-900">${pv.firstName} ${pv.lastName}</span>
                        <span class="text-xs text-gray-500">${pv.email}</span>
                    </div>
                </label>
            </#list>
        <#else>
            <div class="px-4 py-6 text-sm text-gray-500 italic text-center">
                No saved visitors found. Add some on the left first!
            </div>
        </#if>

    </div>
    <p class="text-xs text-gray-500 mt-1">Select one or more visitors to add to this event.</p>
</div>