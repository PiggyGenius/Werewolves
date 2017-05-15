
// VARIABlES #########################################################################

var currentChatType;
var gameId;
var lastMsg;
var chatTypes = ["VILLAGE", "WEREWOLF", "SPIRITUAL"];
var authorizedChatTypes = [[false, false], [false, false], [false, false]];
var currentChats = JSON.parse(JSON.stringify(authorizedChatTypes));
var printableChatTypes = ["Village square", "Werewolves den", "Spiritual room"];

var username;
var isNight;
var isDead;
var role;

var insertionLocked = false;

// SWITCH BETWEEN CHATS ##############################################################

function updateAuthorizations(data) {
	authorizedChatTypes[0][0] = data.VILLAGE.read;
	authorizedChatTypes[0][1] = data.VILLAGE.write;
	authorizedChatTypes[1][0] = data.WEREWOLF.read;
	authorizedChatTypes[1][1] = data.WEREWOLF.write;
	authorizedChatTypes[2][0] = data.SPIRITUAL.read;
	authorizedChatTypes[2][1] = data.SPIRITUAL.write;
	if (ArraysNotEqual(authorizedChatTypes, currentChats)) {
		updateChats();
	}
}


function updateChats() {
	var list = document.getElementById("chatList");

	// clean the list
	while (list.firstChild) {
		list.removeChild(list.firstChild);
	}

	// fill the list
	var option;
	for (var i = 0; i < 3; i++) {
		if (authorizedChatTypes[i][0]) {
			option = list.appendChild(document.createElement("option"));
			option.id = "chatOption" + chatTypes[i];
			option.value = chatTypes[i];
			option.appendChild(document.createTextNode(printableChatTypes[i]));
		}
	}
	// currentChats = authorizedChatTypes.slice();
	currentChats = JSON.parse(JSON.stringify(authorizedChatTypes));
	switchChat();
}


function ArraysNotEqual(a1, a2) {
	for (var i = 0; i < 3; i++) {
		for (var j = 0; j < 2; j++) {
			if (a1[i][j] !== a2[i][j]) {
				return true;
			}
		}
	}
	return false;
}


function switchChat() {
	currentChatType = document.getElementById("chatList").value;
	// clean message list
	var msgList = document.getElementById("msgList");
	while (msgList.firstChild) {
		msgList.removeChild(msgList.firstChild);
	}
	lastMsg = 0;
	// disable button if read only
	document.getElementById("messageButton").disabled = (
			(currentChatType == "VILLAGE" && ! authorizedChatTypes[0][1])
		||	(currentChatType == "WEREWOLF" && ! authorizedChatTypes[1][1])
		||	(currentChatType == "SPIRITUAL" && ! authorizedChatTypes[2][1]));
}


// HANDLE MESSAGES ###################################################################

function insertNewMessages(data) {
	// if no new message, exit
	if (data.lastId === 0) {
		return;
	}

	// update last message id
	lastMsg = data.lastId;

	var msg;
	var msgList = document.getElementById("msgList");

	// add received messages
	for (var i=0; i<data.messages.length; i++) {
		msg = data.messages[i];
		var li = document.createElement("li");
		li.appendChild(document.createTextNode(msg.author + " [" + msg.date + "]: " + msg.message));
		msgList.appendChild(li);
	}
}
