import { getToken } from './jwt.js';

async function fetchMessages() {
    const token = getToken();
    try {
        const response = await fetch(`/getMessages?token=${token}`, {
            method: 'GET',
            headers: {
                'Authorization': 'Bearer ' + token,
                'Content-Type': 'application/json'
            }
        });
        if (response.ok) {
            const messages = await response.json();
            displayMessages(messages);
        } else {
            alert('Kunde inte hämta meddelanden.');
        }
    } catch (error) {
        console.error('Fel vid hämtning av meddelanden:', error);
    }
}
function displayMessages(messages) {
    const messagesList = document.getElementById('messages-list');
    messagesList.innerHTML = '';
    messages.forEach(message => {
        const messageDiv = document.createElement('div');
        messageDiv.classList.add('message');
        const messageContent = document.createElement('p');
        messageContent.textContent = message.content;
        const deleteButton = document.createElement('button');
        deleteButton.textContent = 'Ta bort';
        deleteButton.addEventListener('click', () => deleteMessage(message.id));
        messageDiv.appendChild(messageContent);
        messageDiv.appendChild(deleteButton);
        messagesList.appendChild(messageDiv);
    });
}
async function deleteMessage(messageId) {
    const token = getToken();
    try {
        const response = await fetch(`/deleteMessage/${messageId}?token=${token}`, {
            method: 'DELETE',
            headers: {
                'Authorization': 'Bearer ' + token,
                'Content-Type': 'application/json'
            }
        });
        if (response.ok) {
            alert('Meddelande borttaget!');
            await fetchMessages();
        } else {
            alert('Kunde inte ta bort meddelandet.');
        }
    } catch (error) {
        console.error('Fel vid borttagning av meddelande:', error);
    }
}
document.getElementById('logout-btn').addEventListener('click', () => {
    localStorage.removeItem('jwtToken');
    window.location.href = '/';
});
document.getElementById('write-btn').addEventListener('click', () => {
    window.location.href = `/writeMessage?token=${getToken()}`;
});
document.addEventListener('DOMContentLoaded', fetchMessages);