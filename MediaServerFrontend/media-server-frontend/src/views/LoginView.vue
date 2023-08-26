<template>
    <div class="login-wrapper">
        <div class="login-container">
            <h2>Login</h2>
            <form @submit.prevent="handleSubmit">
                <input type="text" v-model="usernameOrEmail" placeholder="Username or Email" required />
                <input type="password" v-model="password" placeholder="Password" required />
                <button type="submit">Login</button>
                <router-link to="/register" class="register-btn">Register</router-link>
            </form>
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'
import router from '@/router';

const usernameOrEmail = ref('')
const password = ref('')

async function handleSubmit() {
    try {
        const response = await axios.post('http://192.168.0.18:8081/api/auth/login', {
            usernameOrEmail: usernameOrEmail.value,
            password: password.value
        })

        if (response.status === 200 && response.data && response.data.token) {
            localStorage.setItem('token', response.data.token);
            router.push('/home');
        } else {
            alert('Login failed!')
        }
    } catch (error) {
        if (error.response && error.response.data) {
            const exceptionResponse = error.response.data;
            alert(`${exceptionResponse.exception}: ${exceptionResponse.message}`);
        } else {
            alert('Error during login:', error);
        }
    }
}
</script>

<style lang="scss" scoped>
.login-wrapper {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 100vh;
    background-color: #f7f8fc;

    .login-container {
        width: 90%;
        max-width: 450px; // Made it a bit bigger for larger screens
        margin: 0 auto;
        padding: 20px;
        border: 1px solid #e0e0e0;
        border-radius: 8px;
        box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);

        h2 {
            text-align: center;
            margin-bottom: 20px;
        }

        form {
            display: flex;
            flex-direction: column;

            input {
                padding: 12px;  // Increased padding slightly
                margin-bottom: 10px;
                border: 1px solid #e0e0e0;
                border-radius: 4px;
            }

            button {
                padding: 12px;  // Increased padding slightly
                margin-bottom: 10px;  // Added margin to separate from Register button
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

            .register-btn {
                display: block;
                width: 100%;
                text-align: center;
                padding: 12px;
                border: none;
                border-radius: 4px;
                background-color: #f0f0f0;
                color: #333;
                text-decoration: none;
                transition: background-color 0.3s;

                &:hover {
                    background-color: #ddd;
                }
            }
        }
    }
}
</style>