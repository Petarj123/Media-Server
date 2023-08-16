// Global helper function
function closeErrorPopup() {
    const errorPopup = document.getElementById('errorPopup');
    errorPopup.style.display = 'none';
}
function getJwtToken() {
    return localStorage.getItem('token');
}
document.addEventListener("DOMContentLoaded", function() {

    // Local helper function
    function showErrorPopup(message) {
        const errorPopup = document.getElementById('errorPopup');
        const errorMessage = document.getElementById('errorMessage');

        errorMessage.textContent = message;
        errorPopup.style.display = 'flex';
    }

    // Create folder functionality
    document.getElementById("createFolderButton").addEventListener('click', function() {
        const form = document.getElementById("createFolderForm");
        const formData = new FormData(form);
        const folderName = formData.get('name');

        fetch('/folder/create', {
            method: 'POST',
            body: JSON.stringify({ name: folderName }),
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + getJwtToken()
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
            showErrorPopup(error.message);
        });
    });

    // Delete folder functionality
    document.getElementById("deleteFolderButton").addEventListener('click', function() {
        const form = document.getElementById("deleteFolderForm");
        const formData = new FormData(form);
        const folderName = formData.get('folderName');

        fetch('/folder/delete', {
            method: 'DELETE',
            body: JSON.stringify({ name: folderName }),
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + getJwtToken()
            }
        })
        .then(response => {
            if (response.status === 204) {
                console.log('Deleted');
            }
            if (response.status === 400) {
                throw new Error("Unable to delete folder");
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showErrorPopup(error.message);
        });
    });

    // Upload files functionality
    document.getElementById("uploadImageButton").addEventListener('click', function() {
        const form = document.getElementById("uploadImageForm");
        const formData = new FormData();

        // Add each file to the form data
        const files = form.querySelector('input[type="file"]').files;
        for (let i = 0; i < files.length; i++) {
            formData.append('files', files[i]);
        }
        formData.append('folderName', form.querySelector('input[name="folderName"]').value);

        fetch('/file/upload', {
            method: 'POST',
            body: formData,
            headers: {
                'Authorization': 'Bearer ' + getJwtToken()
            }
        })
        .then(response => {
            if (response.ok) {
                return response.json();
            }
            throw new Error('Failed to upload image(s)');
        })
        .then(data => {
            console.log(data);
            // Handle the successful upload of images (e.g., update the UI)
        })
        .catch(error => {
            console.error('Error:', error);
            showErrorPopup(error.message);
        });
    });

    // Fetch all folders and populate the folderList div
    fetch('/folder/all', {
            headers: {
                'Authorization': 'Bearer ' + getJwtToken()
            }
        })    
    .then(response => response.json())
    .then(data => {
        const folderList = document.getElementById('folderList');
        data.forEach(folder => {
            const folderElement = document.createElement('div');
            folderElement.className = 'folder-item';
            folderElement.innerText = `${folder.name} (Last Modified: ${new Date(folder.lastModified).toLocaleDateString()})`;

            const filesList = document.createElement('ul');
            filesList.style.display = 'none'; // Initially hidden

            folderElement.addEventListener('click', function() {
                // Check if filesList is already populated
                if (filesList.childNodes.length) {
                    // If it has children (i.e., it's populated), toggle the display
                    if(filesList.style.display === 'none') {
                        filesList.style.display = 'block';
                    } else {
                        filesList.style.display = 'none';
                    }
                } else {
                    // If not populated, fetch files and display
                    fetch(`/folder/${folder.name}/files`, {
                        headers: {
                            'Authorization': 'Bearer ' + getJwtToken()
                        }
                    })
                    .then(response => response.json())
                    .then(files => {
                        files.forEach(fileName => {
                            const listItem = document.createElement('li');
                            listItem.innerText = fileName;
                            filesList.appendChild(listItem);
                        });
                        folderElement.appendChild(filesList);
                        filesList.style.display = 'block'; // Show the files list
                    });
                }
            });
            folderList.appendChild(folderElement);
        });
    });
});
