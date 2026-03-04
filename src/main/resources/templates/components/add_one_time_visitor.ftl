<#if otv_success??>
    <div class="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded relative mb-4" role="alert">
        <strong class="font-bold">Success!</strong>
        <span class="block sm:inline">The one-time visitor has been added successfully.</span>
    </div>
</#if>

<#if otv_error??>
    <div class="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative mb-4" role="alert">
        <strong class="font-bold">Error!</strong>
        <span class="block sm:inline">There was an error adding the one-time visitor. Please try again.</span>
    </div>
</#if>

<form action="/create-otv" method="post" class="space-y-4">
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
        <p class="mt-2 text-sm text-gray-500">Needded to send the QR code to the visitor's email address</p>
    </div>

    <div class="grid grid-cols-2 gap-4">
        <div>
            <label class="block text-sm font-medium text-gray-700">Access Start Time</label>
            <input type="datetime-local" name="access_start" required
                   class="mt-1 block w-full rounded-md border-gray-300 shadow-sm border p-2 focus:border-blue-500 focus:ring-blue-500">
        </div>
        <div>
            <label class="block text-sm font-medium text-gray-700">Access End Time</label>
            <input type="datetime-local" name="access_end" required
                   class="mt-1 block w-full rounded-md border-gray-300 shadow-sm border p-2 focus:border-blue-500 focus:ring-blue-500">
        </div>
    </div>

    <#include "resource_dropdown.ftl">

    <div>
        <label class="block text-sm font-medium text-gray-700">Custom Invite Message</label>
        <textarea name="custom_message" rows="3"
                  class="mt-1 block w-full rounded-md border-gray-300 shadow-sm border p-2 focus:border-blue-500 focus:ring-blue-500"></textarea>
    </div>

    <div class="text-sm text-gray-500">
        <p class="text-center">The visitor will receive an email with the QR code and a custom message (if provided).</p>
        <p class="text-red-700 text-center">The visitor will be able to access your resources at the given time with a QR code!</p>
    </div>

    <button type="submit" class="w-full bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline">
        Add One-Time Visitor
    </button>
</form>