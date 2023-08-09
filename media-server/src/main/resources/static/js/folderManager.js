document.addEventListener("DOMContentLoaded", function() {

    // Create folder
    document.getElementById("createFolderButton").addEventListener('click', function() {
        const form = document.getElementById("createFolderForm");
        const formData = new FormData(form);
        const folderName = formData.get('name');

        fetch('/folder/create', {
            method: 'POST',
            body: new URLSearchParams({ 'name': folderName }),
            headers: {
                'Content-Type': 'application/json'
            }
        })
        .then(response => {
            if (response.status === 201) {
                return response.json();
            }
            throw new Error('Failed to create folder');
        })
        .then(data => {
            console.log(data);
            // Handle the successful creation of a folder (e.g., update the UI)
        })
        .catch(error => {
            console.error('Error:', error);
        });
    });

    // Delete folder
    document.getElementById("deleteFolderButton").addEventListener('click', function() {
        const form = document.getElementById("deleteFolderForm");
        const formData = new FormData(form);
        const folderName = formData.get('folderName');

        fetch('/folder/delete', {
            method: 'DELETE',
            body: new URLSearchParams({ 'folderName': folderName }),
            headers: {
                'Content-Type': 'application/json'
            }
        })
        .then(response => {
            if (response.ok) {
                return response.json();
            }
            throw new Error('Failed to delete folder');
        })
        .then(data => {
            console.log(data);
            // Handle the successful deletion of a folder (e.g., update the UI)
        })
        .catch(error => {
            console.error('Error:', error);
        });
    });

    // Upload image (add this if you want a separate function for it)
    document.getElementById("uploadImageButton").addEventListener('click', function() {
        const form = document.getElementById("uploadImageForm");
        const formData = new FormData(form);

        fetch('/upload', {
            method: 'POST',
            body: formData,
        })
        .then(response => {
            if (response.ok) {
                return response.json();
            }
            throw new Error('Failed to upload image');
        })
        .then(data => {
            console.log(data);
            // Handle the successful upload of an image (e.g., update the UI)
        })
        .catch(error => {
            console.error('Error:', error);
        });
    });

});


