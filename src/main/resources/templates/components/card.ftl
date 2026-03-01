<#--
  This is your component definition.
  It accepts two "props": title and widthClass (which defaults to full width)
-->
<#macro ui title widthClass="w-full">
    <div class="bg-white p-8 rounded-xl shadow-lg ${widthClass}">
        <h1 class="text-3xl font-bold text-center mb-4">${title}</h1>
        <#nested>
    </div>
</#macro>