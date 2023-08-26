<template>
  <div class="home-container">
    <div v-for="item in items" :key="item.id" class="item" @click="handleItemClick(item)">
      <span v-if="item.fileType === 'FOLDER'" class="folder-text">Folder</span>
      <span v-else class="file-text">File</span>
      {{ item.name }}

      <input 
        type="checkbox" 
        v-if="isMoving && item.fileType !== 'FOLDER'" 
        v-model="selectedItems" 
        :value="item.id"
      />

      <button 
        v-if="currentFolderId !== 'home' && item.fileType === 'FOLDER'" 
        @click.stop="deleteFolder(item.id)"
      >Delete</button>

      <button 
        v-else-if="currentFolderId !== 'home' && item.fileType !== 'FOLDER'" 
        @click.stop="deleteFile(item.name)"
      >Delete</button>
    </div>

    <div v-if="moveModalVisible" class="move-modal">
      <h3>Select Target Folder</h3>
      <div 
        v-for="folder in folderItems" 
        :key="folder.id" 
        class="folder-option" 
        @click="handleFolderClick(folder.id, folder)"
      >
        {{ folder.name }}
      </div>
      <button @click="confirmMove">Confirm Move</button>
      <button @click="cancelMove">Cancel</button>
    </div>

    <input type="file" multiple ref="selectedFiles" style="display: none" @change="uploadFiles"/>
    <button @click="$refs.selectedFiles.click()">Upload Files</button>
    <input type="text" v-model="newFolderName" placeholder="Enter new folder name" />
    <button @click="createFolder">Create Folder</button>
    <button @click="initiateMove">Move</button>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';
import { library } from '@fortawesome/fontawesome-svg-core';
import { faFolder, faFile } from '@fortawesome/free-solid-svg-icons';

library.add(faFolder, faFile);
const route = useRoute();
const router = useRouter();
const items = ref([]);
const currentFolderId = ref('home');
const selectedFiles = ref(null);
const newFolderName = ref('');
const isMoving = ref(false);
const selectedItems = ref([]);
const moveModalVisible = ref(false);
const targetFolderId = ref('');
const folderItems = ref([]);

onMounted(() => {
  if(route.params.folderId) {
    currentFolderId.value = route.params.folderId;
    fetchFolderItems(currentFolderId.value);
  } else {
    fetchMainFolder();
  }
});

watch(() => route.params.folderId, async (newFolderId) => {
  if (newFolderId) {
    currentFolderId.value = newFolderId;
    await fetchFolderItems(newFolderId);
  } else {
    fetchMainFolder();
  }
});

const fetchMainFolder = async () => {
  const token = localStorage.getItem('token');
  try {
    const response = await axios.get('http://192.168.0.18:8081/folder/all', {
      headers: {
        Authorization: `Bearer ${token}`,
        
      }
    });
    
    currentFolderId.value = Object.keys(response.data)[0];
    
    items.value = response.data[currentFolderId.value];
  } catch (error) {
    console.error("Error fetching main folder:", error);
  }
};

const handleItemClick = (item) => {
  if (item.fileType === 'FOLDER') {
    if (isMoving.value) {
      // If in move mode, set the target folder ID but don't navigate away.
      targetFolderId.value = item.id;
      // Fetch the items within the folder to allow for deeper navigation.
      fetchFolderItemsForMove(item.id);
    } else {
      currentFolderId.value = item.id;
      fetchFolderItems(item.id);
      router.push(`/home/${item.id}`);
    }
  }
};
const handleFolderClick = (folderId, folder) => {
    setTargetFolder(folderId);
    handleItemClick(folder);
};

const showErrorPopup = (error) => {
  if (error.response && error.response.data) {
    const { exception, message, code } = error.response.data;
    // Here you can use whatever method you're using to display popups.
    alert(`Error: ${exception}\nMessage: ${message}\nCode: ${code}`);
  } else {
    alert("An unexpected error occurred.");
  }
};
const fetchFolderItems = async (folderId) => {
  console.log("Fetching folder items for:", folderId);
  const token = localStorage.getItem('token');
  try {
    const response = await axios.get(`http://192.168.0.18:8081/folder/files/${folderId}`, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
    currentFolderId.value = Object.keys(response.data)[0];
    items.value = response.data[currentFolderId.value];
  } catch (error) {
      console.error("Error fetching folder items:", error);
      showErrorPopup(error);
    }
};

const deleteFolder = async (folderId) => {
  const token = localStorage.getItem('token');
  try {
    await axios.delete(`http://192.168.0.18:8081/folder/delete/${folderId}`, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
    fetchFolderItems(currentFolderId.value);
  } catch (error) {
      console.error("Error fetching folder items:", error);
      showErrorPopup(error);
    }
};
const deleteFile = async (fileName) => {
  const token = localStorage.getItem('token');
  try {
    await axios.delete(`http://192.168.0.18:8081/file/delete`, {
      headers: {
        Authorization: `Bearer ${token}`
      },
      params: {
        fileName: fileName,
        folderId: currentFolderId.value
      }
    });
    fetchFolderItems(currentFolderId.value);
  } catch (error) {
      console.error("Error fetching folder items:", error);
      showErrorPopup(error);
    }
};

const uploadFiles = async () => {
    const filesToUpload = selectedFiles.value.files;


    if (!filesToUpload || filesToUpload.length === 0) return;

    const formData = new FormData();    
    for (let i = 0; i < filesToUpload.length; i++) {
        formData.append("files", filesToUpload[i]);
        console.log(filesToUpload[i]);
    }
    formData.append("folderId", currentFolderId.value);
    console.log(formData);

    const token = localStorage.getItem('token');

    try {
        await axios.post('http://192.168.0.18:8081/file/upload', formData, {
            headers: {
                Authorization: `Bearer ${token}`,
            }
        });
        fetchFolderItems(currentFolderId.value);
    } catch (error) {
      console.error("Error fetching folder items:", error);
      showErrorPopup(error);
    }
};

const createFolder = async () => {
    if (!newFolderName.value) return;

    const token = localStorage.getItem('token');

    try {
        await axios.post('http://192.168.0.18:8081/folder/create', {
            name: newFolderName.value,
            folderId: currentFolderId.value
        }, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
        fetchFolderItems(currentFolderId.value);
    } catch (error) {
      console.error("Error fetching folder items:", error);
      showErrorPopup(error);
    }
};

const initiateMove = () => {
  fetchMainFolderForMove();
  isMoving.value = true;
  moveModalVisible.value = true;
};

const confirmMove = async () => {
  if (!selectedItems.value.length || !targetFolderId.value) {
    console.error("No items selected or target folder not set for move!");
    return;
  }
  const filesToMove = selectedItems.value.join(',');

  const token = localStorage.getItem('token');

  try {
    await axios.put('http://192.168.0.18:8081/file/move', null, {
      params: {
        files: filesToMove,
        currentFolder: currentFolderId.value,
        newFolder: targetFolderId.value
      },
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
    fetchFolderItems(currentFolderId.value);
    isMoving.value = false;
    moveModalVisible.value = false;
  } catch (error) {
      console.error("Error moving files:", error);
      showErrorPopup(error);
  }
};


const setTargetFolder = (folderId) => {
  targetFolderId.value = folderId;
  console.log(targetFolderId);
};
const fetchMainFolderForMove = async () => {
  const token = localStorage.getItem('token');
  try {
    const response = await axios.get('http://192.168.0.18:8081/folder/all', {
      headers: {
        Authorization: `Bearer ${token}`,
      }
    });
    targetFolderId.value = Object.keys(response.data)[0];
    // Extract all folders from the returned data
    let allFolders = [];
    for (let key in response.data) {
      allFolders = allFolders.concat(response.data[key].filter(item => item.fileType === 'FOLDER'));
    }
    
    folderItems.value = allFolders;
    console.log(allFolders);
    
  } catch (error) {
    console.error("Error fetching main folder for move:", error);
    showErrorPopup(error);
  }
};
const fetchFolderItemsForMove = async (folderId) => {
  const token = localStorage.getItem('token');
  try {
    const response = await axios.get(`http://192.168.0.18:8081/folder/files/${folderId}`, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
    // Filter only the folders.
    folderItems.value = response.data[Object.keys(response.data)[0]].filter(item => item.fileType === 'FOLDER');
  } catch (error) {
    console.error("Error fetching folder items for move:", error);
    showErrorPopup(error);
  }
};

const cancelMove = () => {
  moveModalVisible.value = false;
  isMoving.value = false;
  selectedItems.value = [];
  targetFolderId.value = '';
};
</script>

<style lang="scss" scoped>
.home-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 20px;
    max-width: 1000px;
    margin: 0 auto;
    background-color: #f7f7f7;
    border-radius: 8px;
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.15);

    .item {
        display: flex;
        align-items: center;
        width: 100%;
        max-width: 500px;
        background-color: #ffffff;
        border: 1px solid #e0e0e0;
        border-radius: 4px;
        padding: 10px;
        margin: 10px 0;
        cursor: pointer;
        transition: background-color 0.3s, box-shadow 0.3s;

        &:hover {
            background-color: #f5f5f5;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        }

        .folder-text,
        .file-text {
            font-size: 1.2em;
            font-weight: 600;
            margin-right: 15px;
        }

        .folder-text {
            color: red;
        }

        .file-text {
            color: blue;
        }

        button {
            margin-left: auto;
            padding: 5px 10px;
            background-color: #4CAF50;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            transition: background-color 0.3s;

            &:hover {
                background-color: #45a049;
            }
        }
    }

    input[type="file"],
    input[type="text"] {
        margin-top: 15px;
        padding: 10px;
        border: 1px solid #dcdcdc;
        border-radius: 4px;
        outline: none;

        &:focus {
            border-color: #aaa;
        }
    }

    button {
        margin-top: 15px;
        padding: 10px 15px;
        background-color: #333;
        color: white;
        border: none;
        border-radius: 4px;
        cursor: pointer;
        transition: background-color 0.3s;

        &:hover {
            background-color: #555;
        }
    }

    // Move Modal Styles
    .move-modal {
      position: fixed;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      padding: 20px;
      background-color: white;
      border: 1px solid #dcdcdc;
      border-radius: 8px;
      box-shadow: 0 2px 6px rgba(0, 0, 0, 0.3);
      z-index: 1000;
      max-width: 400px;

      h3 {
        margin-bottom: 15px;
      }

      .folder-option {
        padding: 10px;
        border: 1px solid #dcdcdc;
        border-radius: 4px;
        margin-bottom: 10px;
        cursor: pointer;
        transition: background-color 0.2s;

        &:hover {
          background-color: #f5f5f5;
        }
      }
    }
}
</style>
