var userName = window.prompt("Enter your name", "some user");
//var userName = "lol";

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

function appendMessage(message) {
    var fromNow = moment(message.time).format('HH:mm:ss');
    var $message = $(`<li class="clearfix">
        <div class="message-data ${message.from == userName ? 'align-left': 'align-right'}">
        <span class="message-data-name">${message.from}</span>
        <span class="message-data-time">${fromNow}</span>
    </div>
    <div class="message ${message.from == userName ? 'my-message': 'other-message float-right'}">
        ${message.message}
    </div>
    </li>`);
    var $messages = $('#messages');
    $messages.append($message);
    $messages.scrollTop($messages.prop("scrollHeight"));
}

function getPreviousMessages() {
    $.get('/chat').done(messages => messages.forEach(appendMessage));
}

function sendMessage() {
    var $messageInput = $('#messageInput');
    var message = {message: $messageInput.val(), from: userName};
    $messageInput.val('');
    post('/chat', message);
}

function onNewMessage(result) {
    var message = JSON.parse(result.body);
    appendMessage(message);
}

function connectWebSocket() {
    var socket = new SockJS('/chatWS');
    stompClient = Stomp.over(socket);
    //stompClient.debug = null;
    stompClient.connect({}, (frame) => {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/messages', onNewMessage);
    });
}

getPreviousMessages();
connectWebSocket();