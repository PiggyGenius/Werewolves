var lobbyTable = document.createElement("div");
lobbyTable.id="lobby-table";
var runningTable = document.createElement("div");
runningTable.id="running-table";
document.body.appendChild(lobbyTable);
document.body.appendChild(runningTable);

var maxId = 0;
var maxLobbyId = 0;
var maxRunningId = 0;

function processLobbyData(data){
	processData(data, lobbyTable, true);
}

function processRunningData(data){
	processData(data, runningTable, false);
}

function processData(data, gamesTable, lobby){
	var json_data = JSON.parse(data);
	if(lobby === true)
		maxLobbyId = json_data.maxId;
	else
		maxRunningId = json_data.maxId;

	var gameBoard = json_data.gameBoard;
	for(var i=0; i<gameBoard.length; i++){
		var game = gameBoard[i];
		var divGame = document.createElement("div");
		divGame.className="game";

		var spanGameTitle = document.createElement("span");
		spanGameTitle.className="game-title";

		var spanGameDuration = document.createElement("span");
		spanGameDuration.className="game-durations";

		var ul_duration = document.createElement("ul");
		var li_day = document.createElement("li");
		var li_night = document.createElement("li");

		var spanGameRole = document.createElement("span");
		spanGameRole.className="game-roles";

		var ul_role1 = document.createElement("ul");
		var ul_role2 = document.createElement("ul");
		var li_ww = document.createElement("li");
		var li_cont = document.createElement("li");
		var li_fort = document.createElement("li");
		var li_spir = document.createElement("li");
		var li_ins = document.createElement("li");

		var spanGameStart = document.createElement("span");
		spanGameStart.className="game-start";

		var form = document.createElement('form');
		var submit = document.createElement('input');
		var gameId = document.createElement('input');

		li_day.innerHTML = "Day's Duration : " + game.dayLength;
		ul_duration.appendChild(li_day);
		li_night.innerHTML = "Night's Duration : " + game.nightLength;
		ul_duration.appendChild(li_night);
		spanGameDuration.appendChild(ul_duration);

		spanGameTitle.innerHTML = game.username;

		li_ww.innerHTML = "Werewolves: " + game.werewolf*100 +"%";
		li_spir.innerHTML = "Spiritualist: " + game.spiritualist*100 + "%";
		li_fort.innerHTML = "Fortuneteller: " + game.fortuneTeller*100 + "%";
		li_cont.innerHTML = "Contamination: " + game.contamination*100 + "%";
		li_ins.innerHTML = "Insomniac: " + game.insomniac*100 + "%";

		ul_role1.appendChild(li_ww);
		ul_role1.appendChild(li_spir);
		ul_role1.appendChild(li_fort);
		ul_role2.appendChild(li_cont);
		ul_role2.appendChild(li_ins);

		spanGameRole.appendChild(ul_role1);
		spanGameRole.appendChild(ul_role2);

		//spanGameStart.innerHTML = game.gameStart + " at " + game.dayStart;
		spanGameStart.innerHTML = game.gameStart + " at " + ('0'+(Math.trunc(game.dayStart))).slice(-2) + "h" + ('0'+(Math.trunc((game.dayStart - Math.trunc(game.dayStart))*100))).slice(-2);


		if(!lobby){
			var spanGameJoin = document.createElement("span");
			spanGameJoin.className="game-join";
			submit.className="game-join-btn"
			form.setAttribute('method', 'get');
			form.setAttribute('action', 'inGameController');
			submit.setAttribute('type', 'submit');
			submit.setAttribute('value', 'Resume');
			gameId.setAttribute('type', 'hidden');
			gameId.setAttribute('value', game['gameId']);
			gameId.name = 'gameId';
			submit.name = 'submit';
			form.appendChild(submit);
			form.appendChild(gameId);
			spanGameJoin.appendChild(form);
			divGame.appendChild(spanGameJoin);
		}

		divGame.appendChild(spanGameTitle);
		divGame.appendChild(spanGameDuration);
		divGame.appendChild(spanGameStart);
		divGame.appendChild(spanGameRole);

		gamesTable.appendChild(divGame);
	}                 
}

(function fetchGameBoard(){
	$.get('gameBoard', {'maxId': maxLobbyId, 'action': 2}, processLobbyData);
	$.get('gameBoard', {'maxId': maxRunningId, 'action': 3}, processRunningData)
	.always(function() {setTimeout(fetchGameBoard, 1000);})
	;
})();
