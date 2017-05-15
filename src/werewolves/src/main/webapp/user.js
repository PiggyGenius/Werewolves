
function insertPlayerInfos(data) {
	var update = false;
	if (data.dead !== isDead) {
		update = true;
		isDead = data.dead;
		document.getElementById("playerDead").innerHTML = ((isDead === true) ? "Dead" : "Alive");
	}
	if (data.role !== role) {
		update = true;
		role = data.role;
		document.getElementById("playerRole").innerHTML = role;
	}
	if (data.night !== isNight) {
		update = true;
		isNight = data.night;
		document.getElementById("gameTime").innerHTML = (isNight ? "Night" : "Day");
	}

	if (update && !isDead) {
		cleanVotes();
		var powers = document.getElementById("specialPowers");
		if (isNight) { // add a special power
			if (powers.innerHTML === "" && !data.powerUsed && (role === "SPIRITUALIST" || role === "CONTAMINATOR" || role === "FORTUNETELLER")) {
				$.post("inGameController", {action:"getPlayers", gameId:gameId}, insertSpecialPowers, "json")
					//.fail(function() {alert("Error while retrieving the list of players.");})
					;
			}
		} else { // remove eventually the special powers
			powers.innerHTML = "";
		}
	}
}

function insertSpecialPowers(data) {
	var powers = document.getElementById("specialPowers");

	// add list of users with a "Choose" button
	var form = powers.appendChild(document.createElement("form"));
	form.method = "post";
	form.acceptCharset = "UTF-8";
	form.action = "javascript:specialPowerChoice()";

	var select = form.appendChild(document.createElement("select"));
	select.id = "specialPowersList";
	var input = form.appendChild(document.createElement("input"));
	input.type = "submit";
	input.id = "specialPowersButton";
	input.value = "Choose";

	// fill according to the special power
	var option;
	var user;
	// spiritualist : at night, allow to choose one dead player to discuss with
	// contaminator : at night, allow to choose one alive player to make werewolf
	// fortune teller : at night, allow to choose one alive player and know his role
	for (var i = 0; i < data.users.length; i++) {
		user = data.users[i];
		if (((role === "SPIRITUALIST" && user.infos.dead) || ((role === "CONTAMINATOR" || role === "FORTUNETELLER") && ! user.infos.dead)) && user.target !== username) {
			option = select.appendChild(document.createElement("option"));
			option.value = user.target;
			option.appendChild(document.createTextNode(user.target));
		}
	}
}

var endAlreadyDisplayed = false;

function handleEndOfGame(data) {
	if (data.result !== "CONTINUE" && ! endAlreadyDisplayed) {
		alert("End of game !\n Team <" + data.result + "> wins.");
		finishVotes(null);
		endAlreadyDisplayed = true;
	}
}

