async function navigateToHomeWithToken(token) {
    try {
        const response = await fetch('http://localhost:8080/home', {
            method: 'GET',
            headers: {
                'Authorization': 'Bearer ' + token
            },
        });

        if (response.ok) {
            const html = await response.text();
            document.open();
            document.write(html);
            document.close();
        } else {
            alert('Error accessing /home.');
        }
    } catch (error) {
        alert('There was an error navigating to /home.');
        console.error('Error:', error);
    }
}

async function performLogin() {
    const usernameOrEmail = document.getElementById('usernameOrEmail').value;
    const password = document.getElementById('password').value;

    try {
        const response = await fetch('http://localhost:8080/api/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ usernameOrEmail, password }),
        });

        if (response.ok) {
            const data = await response.json();
            if (data && data.token) {
                // Save the token to local storage
                localStorage.setItem('token', data.token);

                // Navigate to /home with the token in the header
                navigateToHomeWithToken(data.token);
            } else {
                alert('Login failed: Token was not provided.');
            }
        } else {
            alert('Login failed: Incorrect username/email or password.');
        }
    } catch (error) {
        alert('There was an error logging in.');
        console.error('Error:', error);
    }
}

document.getElementById('loginForm').addEventListener('submit', (event) => {
    event.preventDefault();
    performLogin();
});
