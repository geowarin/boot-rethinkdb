
function post(url, data) {
    return $.ajax({
        type: 'POST',
        url: url,
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        data: JSON.stringify(data)
    })
}

function getMessages() {
    return $.get('/chat').done(function(messages) {
        messages.forEach(function(message) {
            $('#messages').append($('<div />').text(message.message))
        })
    });
}

function sendMessage() {
    var message = {message: $('#messageInput').val(), from: 'aUser'};
    post('/chat', message).done(function(data) {
        console.log(data)
    });
}

getMessages();