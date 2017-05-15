"use strict";

// GET CURRENT VOTES #################################################################

function handlePlayersInfos(data) {
	if (isNight !== undefined && role !== undefined && (!isNight || role === "WEREWOLF" || role === "CONTAMINATOR")) {
		// if vote session, insert in votes and in players list
		var user;
		for (var i=0; i<data.users.length; i++) {
			user = data.users[i];
			insertInPlayersTable(user);
			if (user.vote !== null) {
				insertInVotePlayers(user);
			} else {
				insertInNoVotePlayers(user);
			}
		}
	} else {
		// if not a vote session, insert only in players list
		for (var i=0; i<data.users.length; i++) {
			insertInPlayersTable(data.users[i]);
		}
		cleanVotes();
	}
}

//in page :
//- list of all players : A
//- list of players with no vote : B
//- list of players with vote : C
//
//	get list of all players with votes eventually
//	for each player in that list:
//		insert it in A, update status (eventually)
//		if vote:
//			if in C:
//				update count/button
//			else:
//				insert in C, update count/button
//				drop it from B (eventually)
//		else:
//			insert in B (eventually)

// data = { "users": [
//	{"target":user1, "dead":0, "vote": null},
//	{"target":user2, "dead":1, "vote": null},
//	{"target":user3, "dead":0, "vote": {"count":12, "hasVoted":0}},
//	{"target":user4, "dead":0, "vote": {"count":4, "hasVoted":1}}
//	]}

function insertInPlayersTable(user) {
	var elt = document.getElementById("player" + user.target);
	if (elt) {
		if (user.dead === true && elt.getElementsByTagName("td")[1].innerHTML !== "DEAD") {
			elt.getElementsByTagName("td")[1].innerHTML = "DEAD";
			finishVotes(user.target);
		}
	} else {
		var row = document.getElementById("playersTable").insertRow(-1);
		row.id = "player" + user.target;
		row.insertCell(0).innerHTML = user.target;
		row.insertCell(1).innerHTML = ((user.dead === true) ? "DEAD" : "");
	}
}


function finishVotes(user) {
	var list = document.querySelectorAll('[id^="voteButton"]');
	for (var i = 0; i < list.length; i++) {
		list[i].disabled = true;
	}
	document.getElementById("startNewVoteButton").disabled = true;

	if (user !== null) {
		alert(user + " just passed away.");
	}
}

function cleanVotes() {
	var votesTable = document.getElementById("votesTable");
	while (votesTable.firstChild) {
		votesTable.removeChild(votesTable.firstChild);
	}
	var noVotesList = document.getElementById("playersNoVoteList");
	while (noVotesList.firstChild) {
		noVotesList.removeChild(noVotesList.firstChild);
	}
	document.getElementById("startNewVoteButton").disabled = false;
}


function insertInVotePlayers(user) {
	var voteEntry = document.getElementById("voteEntry" + user.target);
	if (voteEntry) {
		// update the votes count
		voteEntry.getElementsByTagName("td")[1].innerHTML = user.vote.count;
		// update the state of the button
		if (user.vote.hasVoted) {
			document.getElementById("voteButton" + user.target).disabled = true;
		}
	} else {
		// drop it from the list of no votes (eventually)
		var noVote = document.getElementById("option" + user.target);
		if (noVote) {
			document.getElementById("playersNoVoteList").removeChild(noVote);
		}
		// insert in in the list of current votes
		var newRow = votesTable.insertRow(-1);
		newRow.id = "voteEntry" + user.target;
		newRow.insertCell(0).innerHTML = user.target;
		newRow.insertCell(1).innerHTML = user.vote.count;
		var voteForm = newRow.insertCell(2).appendChild(document.createElement("form"));
		voteForm.method = "post";
		voteForm.action = "javascript:voteForUser(\"" + user.target + "\")";
		// voteForm.name = user.target;
		voteForm.acceptCharset = "UTF-8";
		var voteInput = voteForm.appendChild(document.createElement("input"));
		voteInput.type = "submit";
		voteInput.value = "Vote";
		voteInput.id = "voteButton" + user.target;
		if (user.vote.hasVoted) {
			voteInput.disabled = true;
		}
	}
}


function insertInNoVotePlayers(user) {
	if (user.dead === false && !document.getElementById("option" + user.target)) {
		var option = document.createElement("option");
		option.value = user.target;
		option.id = "option" + user.target;
		option.appendChild(document.createTextNode(user.target));
		document.getElementById("playersNoVoteList").appendChild(option);
	}
}

