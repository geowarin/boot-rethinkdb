function post(url, data) {
    var stringified = data ? JSON.stringify(data) : null
    return $.ajax({
        type: 'POST',
        url: url,
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        data: stringified
    })
}

function getMessages() {
    return $.get('/chat').done(function (messages) {
        messages.forEach(function (message) {
            $('#messages').append($('<div />').text(message.from + ": " + message.message))
        })
    });
}

function sendMessage() {
    var message = {message: $('#messageInput').val(), from: 'aUser'};
    post('/chat', message);
}

function connectWebSocket() {
    var socket = new SockJS('/chatWS');
    stompClient = Stomp.over(socket);
    stompClient.debug = null;
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/messages', function (result) {
            var message = JSON.parse(result.body);
            console.log(result.body);
            $('#messages').append($('<div />').text(message.from + ": " + message.message))
        });
    });
}

getMessages();
connectWebSocket();