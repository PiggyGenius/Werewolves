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

		// clean vote stuff eventually
        var idList = document.getElementById("list-players-voted");
		var votesTable = idList.getElementsByClassName("listVote");
		while (votesTable.firstChild) {
			votesTable.removeChild(votesTable.firstChild);
		}
        var idListToVote = document.getElementById("list-players-to-vote");
		var noVotesList = idListToVote.getElementsByClassName("listNoVote");
		while (noVotesList.firstChild) {
			noVotesList.removeChild(noVotesList.firstChild);
		}
	}
}

function insertInPlayersTable(user) {
	var elt = document.getElementById("user" + user.target);
	if (elt) {
		if (user.dead === true && elt.getElementsByClassName("player-status").innerHTML !== "Dead") {
			elt.getElementsByClassName("player-status").innerHTML = "Dead";
			finishVotes(user.target);
		}
	} else {
		var divPlayer = document.createElement("div");
        divPlayer.className="player";
        var divUser = document.createElement("div");
        divUser.id="user" + user.target;
        var divPlayerName = document.createElement("div");
        divPlayerName.className="player-name";
        var playerName = document.createElement("h3");
        playerName.innerHTML=user.target;
        var spanPlayerStatus = document.createElement("span");
        spanPlayerStatus.className="player-status";
        spanPlayerStatus.innerHTML = ((user.dead === true) ? "Dead" : "Alive");
        
        divPlayerName.appendChild(playerName);
        divPlayer.appendChild(divPlayerName);
        divPlayer.appendChild(spanPlayerStatus);
        divUser.appendChild(divPlayer);
        document.getElementById("players-list").appendChild(divUser);
	}
}


function finishVotes(user) {
	var list = document.querySelectorAll('div.vote-btn');
	for (var i = 0; i < list.length; i++) {
		list[i].disabled = true;
	}
    document.getElementsByClassName("vote-btn").disabled;
	if (user !== null) {
		alert(user + " just passed away.");
	}
}


function insertInVotePlayers(user) {
	var voteEntry = document.getElementById("voteEntry" + user.target);
	if (voteEntry) {
		// update the votes count
		voteEntry.getElementsByClassName("nb-vote").innerHTML = user.vote.count;
		// update the state of the button
		if (user.vote.hasVoted) {
			voteEntry.getElementsByClassName("vote-btn").disabled = true;
		}
	} else {
		// drop it from the list of no votes (eventually)
        var noVoteList = document.getElementById("listNoVote");
		var noVote = document.getElementById("noVote" + user.target);
		if (noVote) {
			noVoteList.removeChild(noVote);
		}
		// insert in in the list of current votes
        var divPlayer = document.createElement("div");
        divPlayer.className="player";
        var divUser = document.createElement("div");
        divUser.id="voteEntry" + user.target;
        var divPlayerName = document.createElement("div");
        divPlayerName.className="player-name";
        var playerName = document.createElement("h3");
        playerName.innerHTML=user.target;
        var spanNbVote = document.createElement("span");
        spanNbVote.className="nb-vote";
        spanNbVote.innerHTML = user.vote.count;
        var divBtn = document.createElement("div");
        divBtn.className="btn";
        var voteForm = document.createElement("form");
		voteForm.method = "post";
		voteForm.action = "javascript:voteForUser(\"" + user.target + "\")";
		voteForm.acceptCharset = "UTF-8";
		var voteInput = document.createElement("input");
		voteInput.type = "submit";
		voteInput.value = "Vote";
		voteInput.className = "vote-btn";
		if (user.vote.hasVoted) {
			voteInput.disabled = true;
		}
        
        voteForm.appendChild(voteInput);
        divBtn.appendChild(voteForm);
        divPlayerName.appendChild(playerName);
        
        divPlayer.appendChild(divPlayerName);
        divPlayer.appendChild(spanNbVote);
        divPlayer.appendChild(divBtn);
        
        divUser.appendChild(divPlayer);
        
        var votedList = document.getElementById("listVote").appendChild(divUser);
	}
}


function insertInNoVotePlayers(user) {
	if (user.dead === false && !document.getElementById("noVote" + user.target)) {
        var divPlayer = document.createElement("div");
        divPlayer.className="player";
        var divUser = document.createElement("div");
        divUser.id="noVote" + user.target;
        var divPlayerName = document.createElement("div");
        divPlayerName.className="player-name";
        var playerName = document.createElement("h3");
        playerName.innerHTML=user.target;
        var divBtn = document.createElement("div");
        divBtn.className="btn";
        var voteForm = document.createElement("form");
		voteForm.method = "post";
		voteForm.action = "javascript:voteForUser(\"" + user.target + "\")";
		voteForm.acceptCharset = "UTF-8";
		var voteInput = document.createElement("input");
		voteInput.type = "submit";
		voteInput.value = "Propose Vote";
		voteInput.className = "vote-btn";
        
        voteForm.appendChild(voteInput);
        divBtn.appendChild(voteForm);
        divPlayerName.appendChild(playerName);
        
        divPlayer.appendChild(divPlayerName);
        divPlayer.appendChild(divBtn);
        
        divUser.appendChild(divPlayer);
        
        document.getElementById("listNoVote").appendChild(divUser);
	}
}
