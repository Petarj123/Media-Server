<template>
    <div class="register-container">
        <h2>Register</h2>
        <form @submit.prevent="handleSubmit">
            <input type="text" v-model="username" placeholder="Username" required />
            <input type="email" v-model="email" placeholder="Email" required />
            <input type="password" v-model="password" placeholder="Password" required />
            <input type="password" v-model="confirmPassword" placeholder="Confirm Password" required />
            <button type="submit">Register</button>
        </form>
        <button @click="handleLoginRedirect">Already have an account? Login</button>
    </div>
</template>

<script setup>
import { ref } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';

const router = useRouter();
const username = ref('');
const email = ref('');
const password = ref('');
const confirmPassword = ref('');

async function handleSubmit() {
    if (password.value !== confirmPassword.value) {
        alert('Passwords do not match!');
        return;
    }

    try {
        const response = await axios.post('http://192.168.0.18:8081/api/auth/register', {
            username: username.value,
            email: email.value,
            password: password.value,
            confirmPassword: confirmPassword.value
        });

        if (response.status === 201) {
            router.push('/');
        } else {
            alert('Registration failed!');
        }
    } catch (error) {
        if (error.response && error.response.data) {
            const exceptionResponse = error.response.data;
            alert(`${exceptionResponse.exception}: ${exceptionResponse.message}`);
        } else {
            alert('Error during registration:', error);
        }
    }
}

function handleLoginRedirect() {
    router.push('/');
}
</script>

<style lang="scss" scoped>
.register-container {
    width: 300px;
    margin: 0 auto;
    padding: 20px;
    border: 1px solid #e0e0e0;
    border-radius: 8px;
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);

    h2 {
        text-align: center;
        margin-bottom: 20px;
    }

    form {
        display: flex;
        flex-direction: column;

        input {
            padding: 10px;
            margin-bottom: 10px;
            border: 1px solid #e0e0e0;
            border-radius: 4px;
        }

        button {
            padding: 10px;
            border: none;
            border-radius: 4px;
            background-color: #4CAF50;
            color: white;
            cursor: pointer;
            transition: background-color 0.3s;

            &:hover {
                background-color: #45a049;
            }
        }
    }

    button {
        padding: 10px;
        border: none;
        border-radius: 4px;
        background-color: #f44336;
        color: white;
        cursor: pointer;
        transition: background-color 0.3s;
        margin-top: 10px;

        &:hover {
            background-color: #d32f2f;
        }
    }
}
</style>