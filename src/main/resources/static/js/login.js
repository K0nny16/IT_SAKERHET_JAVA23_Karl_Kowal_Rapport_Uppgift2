import { saveToken } from './jwt.js';
document.getElementById('login-form').addEventListener('submit', async function (event) {
    event.preventDefault();
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    try {
        const response = await fetch('http://localhost:8080/login-user', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ email, password })
        });

        if (response.ok) {
            const data = await response.json();
            const jwtToken = data.token;
            saveToken(jwtToken);
            alert('Inloggning lyckades! JWT har sparats.');
            //window.location.href = '/messages/write';
        } else {
            const errorData = await response.json();
            alert(errorData.message || 'Inloggning misslyckades. Kontrollera dina uppgifter.');
        }
    } catch (error) {
        console.error('Ett fel inträffade vid inloggningen:', error);
        alert('Ett fel inträffade vid inloggningen. Försök igen senare.');
    }
});
document.getElementById('register-btn').addEventListener('click', function () {
    window.location.href = '/register';
});
