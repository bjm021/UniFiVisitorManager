<#import "components/card.ftl" as c>
<#import "components/footer.ftl" as f>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Visitor Access Portal</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <style>
        body {
            background-image: url("/images/Ubiquiti-Logo.png");
            background-repeat: no-repeat;
            background-position: center;
            background-attachment: fixed;
            background-size: contain;
        }
    </style>
</head>
<body class="bg-gray-100 flex flex-col items-center justify-center min-h-screen p-4">

<div class="flex flex-col lg:flex-row gap-6 w-full max-w-7xl mx-auto">

    <@c.ui title="Saved Privileged Visitors" widthClass="w-full lg:w-2/3">
        <#include "components/privileged_visitors_table.ftl">
    </@c.ui>

    <@c.ui title="Add one" widthClass="w-full lg:w-1/3">
        <#include "components/add_pv_form.ftl">
    </@c.ui>

</div>

<div class="w-full max-w-7xl mx-auto mt-8">
    <@c.ui title="Plan a new Event">
        <small class="text-sm text-gray-600 mb-2 block text-center">
            This will generate the saved privileged Visitors inside your Access App and give them QR Codes to access the
            given Location at the given Time range
        </small>
    </@c.ui>
</div>

<div class="flex flex-col lg:flex-row gap-6 w-full max-w-7xl mx-auto mt-8">

    <@c.ui title="Add a one-time Visitor" widthClass="w-full lg:w-1/3">
        <p>Sidebar content, instructions, or status goes here...</p>
    </@c.ui>

    <@c.ui title="All Console Visitors" widthClass="w-full lg:w-2/3">
        <#include "components/all_visitor_table.ftl">
    </@c.ui>

</div>

<div class="w-full max-w-7xl mx-auto mt-8">
    <@c.ui title="Old Events">
        <small class="text-sm text-gray-600 mb-2 block text-center">
            This will show all the past events and their associated visitors
        </small>
    </@c.ui>
</div>

<@f.footer maxWidth="max-w-7xl" />


</body>
</html>