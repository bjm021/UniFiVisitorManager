<div id="deleteModal"
     class="fixed inset-0 z-50 flex items-center justify-center bg-gray-900 bg-opacity-0 pointer-events-none transition-all duration-300 ease-out opacity-0">

    <div id="modalBox"
         class="relative mx-auto p-5 border w-96 shadow-lg rounded-md bg-white transform scale-95 opacity-0 transition-all duration-300 ease-out">

        <div class="mt-3 text-center">
            <div class="mx-auto flex items-center justify-center h-12 w-12 rounded-full bg-red-100 mb-4">
                <span class="text-red-600 text-xl">⚠️</span>
            </div>
            <h3 class="text-lg leading-6 font-medium text-gray-900">Confirm Deletion</h3>
            <br>
            <h3 class="text-lg leading-6 font-medium text-gray-900"><span id="deleteType">TYPE</span><br><span id="deleteName">NAME</span> </h3>
            <div class="mt-2 px-7 py-3">
                <p class="text-sm text-gray-500">
                    Are you sure you want to delete this <span id="deleteTypeDesc"></span>? This action cannot be undone.
                </p>
            </div>
            <div class="flex justify-center items-center px-4 py-3 space-x-4">
                <button id="cancelBtn" type="button"
                        class="px-4 py-2 bg-gray-200 text-gray-800 text-base font-medium rounded-md shadow-sm hover:bg-gray-300 focus:outline-none transition-colors">
                    Cancel
                </button>
                <form id="deleteForm" method="post" action="/visitors/delete">
                    <input type="hidden" name="id" id="deleteVisitorId">
                    <input type="hidden" name="redirect" id="redirectToId">
                    <button type="submit"
                            class="px-4 py-2 bg-red-600 text-white text-base font-medium rounded-md shadow-sm hover:bg-red-700 focus:outline-none transition-colors">
                        Delete
                    </button>
                </form>
            </div>
        </div>
    </div>
</div>

<script>
    function openDeleteModal(visitorId, visitorType, visitorName, redirectToId) {


        const modal = document.getElementById('deleteModal');
        const modalBox = document.getElementById('modalBox');
        const cancelBtn = document.getElementById('cancelBtn');

        const redirectTo = document.getElementById('redirectToId');
        redirectTo.value = redirectToId;
        const deleteVisitorId = document.getElementById('deleteVisitorId');
        deleteVisitorId.value = visitorId;
        const deleteType = document.getElementById('deleteType');
        const deleteTypeDesc = document.getElementById('deleteTypeDesc');
        const deleteForm = document.getElementById('deleteForm');
        switch (visitorType) {
            case 'onetime':
                deleteType.innerText = 'One-Time Visitor';
                deleteForm.action = '/delete-otv';
                deleteTypeDesc.innerText = 'One-Time Visitor';
                break;
            case 'event':
                deleteType.innerText = 'Event Visitor';
                deleteTypeDesc.innerText = 'Event Visitor';
                deleteForm.action = '/delete-evv';
                break;
            case 'privileged':
                deleteType.innerText = 'Privileged Visitor';
                deleteTypeDesc.innerText = 'Privileged Visitor';
                deleteForm.action = '/delete-prv';
                break;
            default:
                deleteType.innerText = 'Visitor';
        }
        const deleteName = document.getElementById('deleteName');
        deleteName.innerText = visitorName;

        cancelBtn.removeEventListener('click', () => {});
        cancelBtn.addEventListener('click', evt => {
            modal.classList.replace('bg-opacity-50', 'bg-opacity-0');
            modal.classList.replace('opacity-100', 'opacity-0');
            modalBox.classList.replace('scale-100', 'scale-95');
            modalBox.classList.replace('opacity-100', 'opacity-0');
            setTimeout(() => {
                modal.classList.add('pointer-events-none');
            }, 300);
        });

        modal.classList.remove('pointer-events-none');
        modal.classList.replace('bg-opacity-0', 'bg-opacity-50');
        modal.classList.replace('opacity-0', 'opacity-100');
        modalBox.classList.replace('scale-95', 'scale-100');
        modalBox.classList.replace('opacity-0', 'opacity-100');
    }
</script>