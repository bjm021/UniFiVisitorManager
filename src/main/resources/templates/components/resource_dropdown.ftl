<#-- Define a recursive macro to handle any depth of nested doors/groups -->
<#macro resourceOption resource level>
<#-- Create indentation based on the current depth level -->
    <#local indent = "">
    <#if level gt 0>
        <#list 1..level as i>
            <#local indent = indent + "&nbsp;&nbsp;&nbsp;&nbsp;">
        </#list>
        <#local indent = indent + "↳ ">
    </#if>

<#-- Print the actual <option> tag -->
    <option value="${resource.id}">
        ${indent}${resource.name} <#if resource.isGroup()>(Group)<#else>(Door)</#if>
    </option>

<#-- If this resource has children, call this exact macro again for each child -->
    <#if resource.hasChildren()>
        <#list resource.children as child>
            <@resourceOption resource=child level=level+1 />
        </#list>
    </#if>
</#macro>

<div class="flex-1 w-full">
    <label for="resource_id" class="block text-sm font-medium text-gray-700 mb-1">Select Location / Door</label>
    <small class="text-gray-500">Select a door or location group to access.</small>
    <select name="resource_id" id="resource_id" required class="w-full border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500 p-2 border bg-white">
        <option value="" disabled selected>-- Choose a Location --</option>

        <#-- Loop through the root resources passed from Javalin -->
        <#if accessResources??>
            <#list accessResources as resource>
                <@resourceOption resource=resource level=0 />
            </#list>
        </#if>
    </select>
</div>