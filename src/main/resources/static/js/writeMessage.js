import { getToken } from './jwt.js';

document.addEventListener('DOMContentLoaded', () => {
    const token = getToken();
    if (!token) {
        alert('Du måste vara inloggad för att skriva ett meddelande.');
        window.location.href = '/';
    }
});
document.getElementById('message-form').addEventListener('submit', async function (event) {
    event.preventDefault();
    const messageContent = document.getElementById('message').value;
    const token = getToken();
    try {
        const response = await fetch('/api/messages', {
            method: 'POST',
            headers: {
                'Authorization': 'Bearer ' + token,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ content: messageContent })
        });
        if (response.ok) {
            alert('Meddelande skickat!');
            window.location.href = '/messages.html';
        } else {
            alert('Kunde inte skicka meddelandet. Kontrollera om du är inloggad.');
        }
    } catch (error) {
        console.error('Ett fel inträffade vid skickning av meddelande:', error);
    }
});
