import {deleteToken, getToken} from './jwt.js';

document.getElementById('message-form').addEventListener('submit', async function (event) {
    event.preventDefault();
    const messageContent = document.getElementById('message').value;
    const token = getToken();
    try {
        const response = await fetch('/sendMessage', {
            method: 'POST',
            headers: {
                'Authorization': 'Bearer ' + token,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ content: messageContent })
        });
        if (response.ok) {
            alert('Meddelande skickat!');
            document.getElementById("message").value = "";

        } else {
            alert('Kunde inte skicka meddelandet.');
        }
    } catch (error) {
        console.error('Ett fel inträffade vid skickning av meddelande:', error);
    }
});
document.getElementById("logout-btn").addEventListener("click",function (){
    deleteToken()
    window.location.href = "/logout"
});
document.getElementById("write-btn").addEventListener("click",function (){
    window.location.href =`/writeMessage?token=${getToken()}`
})
document.getElementById("messages-btn").addEventListener("click",function (){
    window.location.href = `/messagesPage?token=${getToken()}`
})