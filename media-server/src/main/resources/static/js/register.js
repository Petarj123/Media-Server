async function performRegistration() {
    const username = document.getElementById('username').value;
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirmPassword').value;

    if (password !== confirmPassword) {
        alert('Passwords do not match!');
        return;
    }

    try {
        const response = await fetch('http://localhost:8080/api/auth/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                username,
                email,
                password,
                confirmPassword
            }),
        });

        if (response.status === 201) {
            alert('Registration successful!');
            window.location.href = 'http://localhost:8080/';
        } else {
            const data = await response.json();
            alert('Registration failed: ' + data.message);
        }
    } catch (error) {
        alert('There was an error during registration.');
        console.error('Error:', error);
    }
}

document.getElementById('registerForm').addEventListener('submit', (event) => {
    event.preventDefault();
    performRegistration();
});
