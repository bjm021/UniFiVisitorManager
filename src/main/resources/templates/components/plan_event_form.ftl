<#if pe_success??>
    <div class="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded relative mb-4" role="alert">
        <strong class="font-bold">Success! (Async Task)</strong><br>
        <span class="block sm:inline">The plan-event request has been submitted successfully. Please not that this task will be running in the background. You can refresh this page to se the updates status.</span>
    </div>
</#if>

<#if pe_error??>
    <div class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative mb-4" role="alert">
        <strong class="font-bold">Error!</strong><br>
        <span class="block sm:inline">There was an error submitting the plan-event request. Please try again.</span>
    </div>
</#if>

<form action="/plan_event" method="post" class="space-y-4">

    <div class="lg:grid lg:grid-cols-2 lg:gap-4">
        <div>
            <#include "resource_dropdown.ftl">
            <!-- description text area -->
            <div class="mt-4">
                <label for="description" class="block text-sm font-medium text-gray-700">Description</label>
                <small class="text-gray-500">Provide a brief description for the event</small>
                <textarea id="description" name="description" rows="3"
                          class="mt-1 block w-full rounded-md border-gray-300 shadow-sm border p-2 focus:border-blue-500 focus:ring-blue-500"></textarea>
            </div>
            <!-- start and end time -->
            <div class="mt-4">
                <label for="start_time" class="block text-sm font-medium text-gray-700">Start Time</label>
                <input type="datetime-local" id="start_time" name="start_time"
                       class="mt-1 block w-full rounded-md border-gray-300 shadow-sm border p-2 focus:border-blue-500 focus:ring-blue-500">
            </div>
            <div class="mt-4">
                <label for="end_time" class="block text-sm font-medium text-gray-700">End Time</label>
                <input type="datetime-local" id="end_time" name="end_time"
                       class="mt-1 block w-full rounded-md border-gray-300 shadow-sm border p-2 focus:border-blue-500 focus:ring-blue-500">
            </div>
        </div>
        <div>
            <div class="">
                <label for="name" class="block text-sm font-medium text-gray-700">Event Name</label>
                <small class="text-gray-500">Provide a name for the event</small>
                <input type="text" id="name" name="name"
                       class="mt-1 block w-full rounded-md border-gray-300 shadow-sm border p-2 focus:border-blue-500 focus:ring-blue-500">
            </div>
            <div class="mt-4">
                <#include "pv_multi_select.ftl">
            </div>
        </div>
    </div>

    <div class="w-full mt-6">
        <button type="submit"
                class="w-full flex justify-center items-center px-4 py-2 border border-transparent text-base font-medium rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500">
            Create Event
        </button>
    </div>

</form>