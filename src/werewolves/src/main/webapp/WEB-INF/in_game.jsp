<!DOCTYPE html>

<html>
	<head>
		<title>Werewolves - Chat</title>
        <link rel="stylesheet" href="in_game.css" />
        <link rel="stylesheet" href="CSS/in-game.css" />
                <!-- CSS Header -->
        <link rel="stylesheet" href="CSS/header.css" />
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		<%@ page import="model.User" %>

		<script src="jquery.js"></script>
		<script type="text/javascript" src="chat.js"></script>
		<script type="text/javascript" src="user.js"></script>
		<script type="text/javascript" src="vote.js"></script>
	</head>
		<body>
            <script>
                function updateScroll(){
                    var element = document.getElementById("msgList");
                    element.scrollTop = element.scrollHeight;
                }
                setInterval(updateScroll, 1000);
            </script>

            <header class="header">
                <script src="jquery.js"></script>
                <script src="header.js"></script>
            </header>
            
		<div id="info-bar">
		<%-- USER # (DONE) ################################################ --%>
		<h1> <%= ((User)session.getAttribute("user")).getUsername() %> - User information</h1>
            <p>Role : </p><div id="playerRole"></div> 
		<br>
            <p>Eliminated :</p> <div id="playerDead"></div>
		<br>
            <p>Time : </p><div id="gameTime"></div>
		<br>

		<%-- SPECIAL POWERS # (DONE) ###################################### --%>
		<div id="specialPowers"></div>

		<script>
function specialPowerChoice() {
	// store chosen values
	var select = document.getElementById("specialPowersList");
	var target = select.value;

	// handle the choice
	$.post("inGameController", {action:"specialPower"+role, gameId:gameId, target:target}, displayRoleFortuneTeller, "json")
		//.fail(function() {alert("Error while submitting special power's choice.");})
		.done(cleanSpecialPowers)
		;
}

function cleanSpecialPowers() {
	var select = document.getElementById("specialPowersList");
	while (select.firstChild) {
		select.removeChild(select.firstChild);
	}
	document.getElementById("specialPowersButton").disabled = true;
}

function displayRoleFortuneTeller(data) {
	if (role === "FORTUNETELLER") {
		alert("The role of " + data.username + " is " + data.role + ".");
	}
}
		</script>

		</div>

		<script>
function getPlayerInfos() {
	$.post("inGameController", {action:"getInfos", gameId:gameId}, insertPlayerInfos, "json")
		//.fail(function() {alert("Error while retrieving user role.");})
		.always(function() {setTimeout(getPlayerInfos, 500);})
		;
	// setTimeout(getPlayerInfos, 1000);
}

function checkEndOfGame() {
	$.post("inGameController", {action:"checkEndOfGame", gameId:gameId}, handleEndOfGame, "json")
		//.fail(function() {alert("Error while retrieving if the game ended.");})
		.always(function() {setTimeout(checkEndOfGame, 500);})
		;
}
		</script>


		<div class="container-inGame">

		<div class="chat">
		<%-- CHAT # (DONE) ################################################ --%>
		<h1>Chat</h1>

		<form method="post" accept-charset="UTF-8" id="chatForm">
			<select id="chatList" onchange="javascript:switchChat()">
			</select>
		</form>

		<ul id="msgList">
		</ul>

		<!-- form calls a JS function that handles the input -->
		<!-- better practice : no action, the form is handled by a JS event listener below ? -->
		<form method="post" action="javascript:submitNewMessage()"
					  accept-charset="UTF-8" id="msgForm">
			<input type="text" placeholder="Your message" autocomplete="off"
					 autofocus="on" name="msg" maxlength="300" size="35" id="msgField"/>
			<input type="submit" name="submit" id="messageButton" value="Send"/>
		</form>
		</div>

<script>
"use strict";

function checkAuthorizedChats() {
	$.post("chatController", {action:"getAuthorizedChats", gameId:gameId}, updateAuthorizations, "json")
		//.fail(function() {alert("Error while checking chat authorizations.");})
		.always(function() {setTimeout(checkAuthorizedChats, 500);})
		;
	// setTimeout(checkAuthorizedChats, 500);
}

function submitNewMessage() {
	// get the message
	var msg = document.getElementById("msgField").value;
	// clean the form
	document.getElementById("msgForm").reset();
	// submit the message
	$.post("chatController", {action:"submitMessage", message:msg, gameId:gameId, chat:currentChatType})
		//.fail(function() {alert("Error while submitting the message.");})
		;
}


function checkNewMessages() {
	// retrieve new messages
	// we give the last messageId, the controller returns only the messages after this one
	$.post("chatController", {action:"getNewMessages", lastMessage:lastMsg, gameId:gameId, chat:currentChatType}, insertNewMessages, "json")
		//.fail(function() {alert("Error while retrieving new messages.");})
		.always(function() {setTimeout(checkNewMessages, 500);})
		;
}


</script>

		<div class="dashboard">
		<%-- VOTE # (DONE) ################################################ --%>

		<h1>Vote</h1>

		<%-- current votes --%>
		<table id="votesTable">
		</table>

		<%-- start a new vote --%>
		<form method="post" action="javascript:startNewVote()" accept-charset="UTF-8" id="voteForm">
			<select id="playersNoVoteList">
			</select>
			<input type="submit" id="startNewVoteButton" value="Start vote"/>
		</form>
		</div>

		<div id="players-list">
		<h1>Players list</h1>
		<%-- list of all the players --%>
		<table id="playersTable">
		</table>
		</div>
		</div>

		<script>
"use strict";
						
function voteForUser(user) {
	$.post("voteController", {action:"vote", gameId:gameId, target:user})
		//.fail(function() {alert("Error while voting for the user.");})
		.done(function () {document.getElementById("voteButton" + user).disabled = true;})
		;
}

function startNewVote() {
 	var target = document.getElementById("playersNoVoteList").value;
	if (target !== "") {
		$.post("voteController", {action:"vote", gameId:gameId, target:target})
			//.fail(function() {alert("Error while submitting new vote.");})
			;
	}
}

function updatePlayers() {
	$.post("voteController", {action:"getPlayers", gameId:gameId}, handlePlayersInfos, "json")
		//.fail(function() {alert("Error while retrieving current votes.");})
		.always(function() {setTimeout(updatePlayers, 500);})
		;
}
		</script>

		<script>
$(document).ready(function () {
	lastMsg = ${lastMessage};
	gameId = ${game};
	username = "<%= ((User)session.getAttribute("user")).getUsername() %>";
	getPlayerInfos();
	checkAuthorizedChats();
	checkNewMessages();
	updatePlayers();
	checkEndOfGame();
});
		</script>



	</body>
</html>
