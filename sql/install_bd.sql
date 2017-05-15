
CREATE TABLE Client(
	username VARCHAR2(20) PRIMARY KEY,
	password VARCHAR2(20) NOT NULL
);

CREATE TABLE Game(
	gameId NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	username VARCHAR2(20) REFERENCES Client(username),
	minPlayer INTEGER DEFAULT 5 NOT NULL,
	maxPlayer INTEGER DEFAULT 20 NOT NULL,
	dayStart NUMBER(4, 2) DEFAULT 8.0 NOT NULL,
	dayLength NUMBER(4, 2) DEFAULT 14.0 NOT NULL,
	nightLength NUMBER(4, 2) DEFAULT 10.0 NOT NULL,
	gameStart DATE DEFAULT SYSDATE + 1 NOT NULL,
	contamination NUMBER(6, 4) DEFAULT 0.0 NOT NULL,
	insomniac NUMBER(6, 4) DEFAULT 0.0 NOT NULL,
	fortuneTeller NUMBER(6, 4) DEFAULT 0.0 NOT NULL,
	spiritualist NUMBER(6, 4) DEFAULT 0.0 NOT NULL,
	werewolf NUMBER(6, 4) DEFAULT 1/3 NOT NULL,
	nightTime NUMBER(1) DEFAULT 0 NOT NULL,
	gameDone NUMBER(1) DEFAULT 0 NOT NULL,
	fortuneTellerUsed NUMBER(1) DEFAULT 0 NOT NULL,
	contaminatorUsed NUMBER(1) DEFAULT 0 NOT NULL,
	spiritualistUsed NUMBER(1) DEFAULT 0 NOT NULL
);

CREATE TABLE Message(
	messageId NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	content VARCHAR2(4000) NOT NULL,
	context VARCHAR2(9) CHECK (context IN ('WEREWOLF', 'VILLAGE', 'SPIRITUAL')),
	time DATE DEFAULT SYSDATE,
	username VARCHAR2(20) REFERENCES Client(username),
	gameId INTEGER REFERENCES Game(gameId),
	archived NUMBER(1) DEFAULT 0
);

CREATE TABLE GameLobby(
	gameId INTEGER REFERENCES Game(gameId),
	username VARCHAR2(20) REFERENCES Client(username),
	lobbyId NUMBER GENERATED ALWAYS AS IDENTITY,
	CONSTRAINT pk_game_lobby PRIMARY KEY (username, gameId)
);

CREATE TABLE UserGame(
	username VARCHAR2(20) REFERENCES Client(username),
	gameId INTEGER REFERENCES Game(gameId),
	role VARCHAR2(13) CHECK (role IN ('WEREWOLF', 'HUMAN', 'SPIRITUALIST', 'CONTAMINATOR', 'FORTUNETELLER', 'INSOMNIAC')),
	dead NUMBER(1) DEFAULT 0, -- 0 if alive, 1 if dead, 2 if dead chosen by spiritualist
	lobbyId NUMBER GENERATED ALWAYS AS IDENTITY,
	CONSTRAINT pk_user_game PRIMARY KEY (username, gameId)
);

CREATE TABLE UserVote(
	gameId INTEGER REFERENCES Game(gameId),
	target VARCHAR2(20) REFERENCES Client(username),
	voter VARCHAR2(20) REFERENCES Client(username),
	CONSTRAINT pk_user_vote PRIMARY KEY (gameId, target, voter),
	CONSTRAINT fk_voter_gameId FOREIGN KEY (gameId, voter) REFERENCES UserGame(gameId, username), /* voter is a player, and the game is active */
	CONSTRAINT fk_target_gameId FOREIGN KEY (gameId, target) REFERENCES UserGame(gameId, username) /* target is a player, and the game is active */
);

CREATE OR REPLACE TRIGGER spiritualistTrigger
AFTER UPDATE OF dead ON UserGame FOR EACH ROW
BEGIN
	IF :new.dead = 2 THEN
		UPDATE Game SET spiritualistUsed = 1 WHERE gameId = :new.gameId;
	END IF;
END;
/

CREATE OR REPLACE TRIGGER contaminatorTrigger
AFTER UPDATE OF role ON UserGame FOR EACH ROW
BEGIN
	UPDATE Game SET contaminatorUsed = 1 WHERE gameId = :new.gameId;
END;
/

CREATE OR REPLACE TRIGGER gameLobbyInsert
AFTER INSERT ON Game FOR EACH ROW
BEGIN
	INSERT INTO gameLobby(gameId, username) VALUES(:new.gameId, :new.username);
END;
/

CREATE OR REPLACE PACKAGE TYPES AS
	TYPE MESSAGECURSOR IS REF CURSOR;
END TYPES;
/

CREATE OR REPLACE FUNCTION getNewMessages(p_username VARCHAR2, p_gameId NUMBER, p_maxId NUMBER, p_context VARCHAR2) RETURN TYPES.MESSAGECURSOR IS
	c_message TYPES.MESSAGECURSOR;
	v_dead UserGame.dead%TYPE;
	v_gameDone Game.gameDone%TYPE;
BEGIN
	SELECT dead INTO v_dead FROM UserGame WHERE gameId = p_gameId AND username = p_username;
	SELECT gameDone INTO v_gameDone FROM Game WHERE gameId = p_gameId;
	IF v_dead = 1 OR v_gameDone = 1 THEN
		OPEN c_message FOR
		SELECT messageId, content, time, username FROM Message WHERE gameId = p_gameId AND context = p_context AND messageId > p_maxId ORDER BY messageId;
	ELSE
		OPEN c_message FOR
		SELECT messageId, content, time, username FROM Message WHERE gameId = p_gameId AND archived = 0 AND context = p_context AND messageId > p_maxId ORDER BY messageId;
	END IF;
	RETURN(c_message);
END getNewMessages;
/

CREATE OR REPLACE PROCEDURE leaveGame(p_client VARCHAR2, p_gameId NUMBER) IS
	v_username Client.username%TYPE;
BEGIN
	SELECT username INTO v_username FROM Game WHERE gameId = p_gameId;
	IF v_username = p_client THEN
		DELETE FROM gameLobby WHERE gameId = p_gameId;
		DELETE FROM game WHERE gameId = p_gameId;
	ELSE
		DELETE FROM gameLobby WHERE gameId = p_gameId AND username = p_client;
	END IF;
END leaveGame;
/

CREATE OR REPLACE PROCEDURE startGame(p_gameId NUMBER) IS
	v_index NUMBER := 0;
	v_roleIndex NUMBER := 0;
	v_playerCount NUMBER;
	v_humanCount NUMBER;
	v_werewolfCount NUMBER;
	v_contamination NUMBER(6, 4);
	v_insomniac NUMBER(6, 4);
	v_fortuneTeller NUMBER(6, 4);
	v_spiritualist NUMBER(6, 4);
	v_werewolf NUMBER(6, 4);
	TYPE ROLE_STRING IS VARRAY(3) OF VARCHAR2(13);
	TYPE ROLE_TYPE IS TABLE OF VARCHAR2(13);
	t_role ROLE_TYPE := ROLE_TYPE();
	-- TODO: Fix probabilities for each role
	a_role ROLE_STRING := ROLE_STRING('INSOMNIAC', 'SPIRITUALIST', 'FORTUNETELLER');
BEGIN
	SELECT contamination, insomniac, fortuneTeller, spiritualist, werewolf INTO v_contamination, v_insomniac, v_fortuneTeller, v_spiritualist, v_werewolf FROM Game WHERE gameId = p_gameId;
	SELECT COUNT(username) INTO v_playerCount FROM GameLobby WHERE gameId = p_gameId;
	v_werewolfCount := CEIL(v_playerCount * v_werewolf);
	IF v_werewolfCount = 0 THEN
		v_werewolfCount := 1;
	END IF;
	v_humanCount := v_playerCount - v_werewolfCount;
	t_role.EXTEND(v_playerCount + 1);

	v_index := v_index + 1;
	IF(DBMS_RANDOM.value < v_contamination) THEN
		t_role(v_index) := 'CONTAMINATOR';
	ELSE
		t_role(v_index) := 'WEREWOLF';
	END IF;

	WHILE v_index < v_werewolfCount LOOP
		v_index := v_index +1;
		t_role(v_index) := 'WEREWOLF';
	END LOOP;

	WHILE v_index < v_playerCount AND v_roleIndex < 3 LOOP
		v_index := v_index + 1;
		v_roleIndex := v_roleIndex + 1;
		IF DBMS_RANDOM.value < v_insomniac THEN
			t_role(v_index) := a_role(v_roleIndex);
		ELSE
			t_role(v_index) := 'HUMAN';
		END IF;
	END LOOP;

	WHILE v_index < v_playerCount LOOP
		v_index := v_index + 1;
		t_role(v_index) := 'HUMAN';
	END LOOP;

	v_index := 0;
	FOR rec IN (SELECT username FROM GameLobby WHERE gameId = p_gameId ORDER BY DBMS_RANDOM.RANDOM) LOOP
		v_index := v_index + 1;
		DELETE FROM gameLobby WHERE gameId = p_gameId AND username = rec.username;
		INSERT INTO UserGame(username, gameId, role) VALUES(rec.username, p_gameId, t_role(v_index));
	END LOOP;
END startGame;
/

CREATE OR REPLACE FUNCTION checkEnd(p_gameId NUMBER) RETURN VARCHAR IS
	v_playerCount NUMBER;
	v_humanCount NUMBER;
	v_werewolfCount NUMBER;
BEGIN
	SELECT COUNT(username) INTO v_playerCount FROM UserGame WHERE gameId = p_gameId AND dead = 0;
	SELECT COUNT(username) INTO v_humanCount FROM UserGame WHERE gameId = p_gameId AND dead = 0 AND (role = 'HUMAN' OR role = 'SPIRITUALIST' OR role = 'FORTUNETELLER' OR role = 'INSOMNIAC');
	SELECT COUNT(username) INTO v_werewolfCount FROM UserGame WHERE gameId = p_gameId AND dead = 0 AND (role = 'WEREWOLF' OR role = 'CONTAMINATOR');
	IF v_playerCount = v_humanCount OR v_playerCount = v_werewolfCount THEN
		UPDATE Game SET gameDone = 1 WHERE gameId = p_gameId;
		IF v_playerCount = v_humanCount THEN
			RETURN('HUMAN');
		ELSIF v_playerCount = v_werewolfCount THEN
			RETURN('WEREWOLF');
		END IF;
	ELSE
		RETURN('CONTINUE');
	END IF;
END checkEnd;
/

CREATE OR REPLACE PROCEDURE voteInsert(p_gameId INTEGER, p_target VARCHAR2, p_voter VARCHAR2) IS
	v_targetCount NUMBER;
	v_playerCount NUMBER;
	v_targetPlayer UserVote.target%TYPE;
	v_nightTime Game.nightTime%TYPE;
BEGIN
	INSERT INTO UserVote VALUES(p_gameId, p_target, p_voter);
	SELECT COUNT(target) INTO v_targetCount FROM UserVote WHERE gameId = p_gameId AND target = p_target GROUP BY target;
	SELECT nightTime INTO v_nightTime FROM Game WHERE gameId = p_gameId;
	IF v_nightTime = 0 THEN
		SELECT TRUNC(COUNT(username)/2) INTO v_playerCount FROM UserGame WHERE gameId = p_gameId AND dead = 0;
	ELSE
		SELECT TRUNC(COUNT(username)/2) INTO v_playerCount FROM UserGame WHERE gameId = p_gameId AND dead = 0 AND (role = 'WEREWOLF' OR role = 'CONTAMINATOR');
	END IF;
	IF v_targetCount > v_playerCount THEN
		UPDATE UserGame SET dead = 1 WHERE gameId = p_gameId AND username = p_target;
	END IF;
END voteInsert;
/

--  INSERT INTO Client VALUES('piggy', 'piggy');
--  INSERT INTO Client VALUES('meanstreet', 'retard');
--  INSERT INTO Client VALUES('a', 'a');
--  INSERT INTO Client VALUES('Raymond', 'hemorroides');
--  INSERT INTO Client VALUES('Michel', 'cacahuete');
--  INSERT INTO Client VALUES('Tifany73', 'farapopiaire');
--  INSERT INTO Client VALUES('Carole', 'pimentdespelette');
--  INSERT INTO Client VALUES('Bruce', 'Springsteen');
--  INSERT INTO Client VALUES('Rene', 'stethoscope');

--  INSERT INTO Game(username) VALUES('piggy');
--  INSERT INTO Game(username) VALUES('piggy');
--  INSERT INTO Game(username) VALUES('meanstreet');
--  INSERT INTO Game(username) VALUES('meanstreet');
--  INSERT INTO Game(username) VALUES('meanstreet');
--  INSERT INTO Game(username) VALUES('a');
--  INSERT INTO Game(username) VALUES('a');

--  --  Test for game 1
--  INSERT INTO UserGame(username, gameId, role, dead) VALUES('meanstreet', 1, 'FORTUNETELLER', 0);
--  INSERT INTO UserGame(username, gameId, role, dead) VALUES('a', 1, 'HUMAN', 0);
--  INSERT INTO UserGame(username, gameId, role, dead) VALUES('piggy', 1, 'WEREWOLF', 0);
--  INSERT INTO UserGame(username, gameId, role, dead) VALUES('Michel', 1, 'HUMAN', 0);
--  INSERT INTO UserGame(username, gameId, role, dead) VALUES('Tifany73', 1, 'HUMAN', 0);
--  INSERT INTO UserGame(username, gameId, role, dead) VALUES('Bruce', 1, 'WEREWOLF', 0);
--  INSERT INTO UserGame(username, gameId, role, dead) VALUES('Raymond', 1, 'WEREWOLF', 1);
--  INSERT INTO UserGame(username, gameId, role, dead) VALUES('Carole', 1, 'HUMAN', 1);

--  INSERT INTO UserVote VALUES(1, 'piggy', 'a');
--  INSERT INTO UserVote VALUES(1, 'piggy', 'meanstreet');
--  INSERT INTO UserVote VALUES(1, 'piggy', 'piggy');
--  INSERT INTO UserVote VALUES(1, 'Tifany73', 'a');
--  INSERT INTO UserVote VALUES(1, 'Tifany73', 'Bruce');
--  INSERT INTO UserVote VALUES(1, 'Tifany73', 'piggy');
--  INSERT INTO UserVote VALUES(1, 'Tifany73', 'meanstreet');
--  INSERT INTO UserVote VALUES(1, 'meanstreet', 'Tifany73');

--  INSERT INTO Message(content, context, username, gameId, archived) VALUES ('A demain', 'VILLAGE', 'meanstreet', 1, 1);
--  INSERT INTO Message(content, context, username, gameId, archived) VALUES ('Bonne nuit les boloss', 'VILLAGE', 'piggy', 1, 1);
--  INSERT INTO Message(content, context, username, gameId, archived) VALUES ('Salut', 'VILLAGE', 'meanstreet', 1, 0);
--  INSERT INTO Message(content, context, username, gameId, archived) VALUES ('Yo retard', 'VILLAGE', 'piggy', 1, 0);
--  INSERT INTO Message(content, context, username, gameId, archived) VALUES ('Salut les gars !', 'VILLAGE', 'a', 1, 0);
--  INSERT INTO Message(content, context, username, gameId, archived) VALUES ('Ta gueule toi', 'VILLAGE', 'piggy', 1, 0);

--  INSERT INTO Message(content, context, username, gameId, archived) VALUES ('Pas degueu cette humaine', 'WEREWOLF', 'meanstreet', 1, 1);
--  INSERT INTO Message(content, context, username, gameId, archived) VALUES ('Je t avais dit que ça irait bien avec une sauce barbecue', 'WEREWOLF', 'piggy', 1, 1);
--  INSERT INTO Message(content, context, username, gameId, archived) VALUES ('A demain', 'WEREWOLF', 'meanstreet', 1, 1);
--  INSERT INTO Message(content, context, username, gameId, archived) VALUES ('Tcho', 'WEREWOLF', 'piggy', 1, 1);
--  INSERT INTO Message(content, context, username, gameId, archived) VALUES ('On va bien les niquer', 'WEREWOLF', 'meanstreet', 1, 0);
--  INSERT INTO Message(content, context, username, gameId, archived) VALUES ('ez, get rekt', 'WEREWOLF', 'piggy', 1, 0);



INSERT INTO Client VALUES('Bruce', 'Springsteen');
INSERT INTO Client VALUES('Tracy', 'Chapman');
INSERT INTO Client VALUES('Alice', 'Cooper');
INSERT INTO Client VALUES('Paul', 'McCartney');
INSERT INTO Client VALUES('Michael', 'Jackson');
INSERT INTO Client VALUES('Phil', 'Collins');
INSERT INTO Client VALUES('Eric', 'Clapton');
INSERT INTO Client VALUES('Mick', 'Jagger');
INSERT INTO Client VALUES('Manu', 'Chao');
INSERT INTO Client VALUES('Robert', 'Cray');
INSERT INTO Client VALUES('Jim', 'Morrison');
INSERT INTO Client VALUES('Muddy', 'Waters');
INSERT INTO Client VALUES('Lenny', 'Kravitz');
INSERT INTO Client VALUES('Mark', 'Knopfler');
INSERT INTO Client VALUES('Rory', 'Gallagher');
INSERT INTO Client VALUES('Keith', 'Richards');
INSERT INTO Client VALUES('Ray', 'Charles');
INSERT INTO Client VALUES('Peter', 'Gabriel');
INSERT INTO Client VALUES('Stevie', 'Wonder');
INSERT INTO Client VALUES('Popa', 'Chubby');
INSERT INTO Client VALUES('Joe', 'Satriani');

-- TEST FOR GAME 1 ---------------------------------------------------------
INSERT INTO Game(username, minPlayer, maxPlayer, dayStart, dayLength, nightLength, gameStart, contamination, insomniac, fortuneTeller, spiritualist, werewolf, nightTime, gameDone, fortuneTellerUsed, contaminatorUsed, spiritualistUsed) VALUES ('Bruce', 5, 30, 8.0, 0.01, 0.01, TO_DATE('10/04/2017 08:00', 'dd/mm/yyyy hh24:mi'), 1.0, 1.0, 1.0, 1.0, 1/3, 1, 0, 0, 0, 1);
-- 

INSERT INTO GameLobby(username, gameId) VALUES('Phil', 1);
INSERT INTO GameLobby(username, gameId) VALUES('Joe', 1);
INSERT INTO GameLobby(username, gameId) VALUES('Manu', 1);
INSERT INTO GameLobby(username, gameId) VALUES('Ray', 1);
INSERT INTO GameLobby(username, gameId) VALUES('Mick', 1);
INSERT INTO GameLobby(username, gameId) VALUES('Mark', 1);
INSERT INTO GameLobby(username, gameId) VALUES('Eric', 1);
INSERT INTO GameLobby(username, gameId) VALUES('Lenny',	1);
INSERT INTO GameLobby(username, gameId) VALUES('Keith',	1);
INSERT INTO GameLobby(username, gameId) VALUES('Peter',	1);
INSERT INTO GameLobby(username, gameId) VALUES('Michael', 1);
INSERT INTO GameLobby(username, gameId) VALUES('Alice',	1);
INSERT INTO GameLobby(username, gameId) VALUES('Jim', 1);
INSERT INTO GameLobby(username, gameId) VALUES('Rory', 1);
INSERT INTO GameLobby(username, gameId) VALUES('Popa', 1);
INSERT INTO GameLobby(username, gameId) VALUES('Robert', 1);
INSERT INTO GameLobby(username, gameId) VALUES('Tracy', 1);
INSERT INTO GameLobby(username, gameId) VALUES('Muddy', 1);
INSERT INTO GameLobby(username, gameId) VALUES('Stevie', 1);
INSERT INTO GameLobby(username, gameId) VALUES('Paul', 1);


INSERT INTO Game(username, minPlayer, maxPlayer, dayStart, dayLength, nightLength, gameStart, contamination, insomniac, fortuneTeller, spiritualist, werewolf, nightTime, gameDone, fortuneTellerUsed, contaminatorUsed, spiritualistUsed) VALUES ('Bruce', 5, 30, 8.0, 0.01, 0.01, TO_DATE('10/04/2018 08:00', 'dd/mm/yyyy hh24:mi'), 1.0, 1.0, 1.0, 1.0, 1/3, 1, 0, 0, 0, 1);
INSERT INTO Game(username, minPlayer, maxPlayer, dayStart, dayLength, nightLength, gameStart, contamination, insomniac, fortuneTeller, spiritualist, werewolf, nightTime, gameDone, fortuneTellerUsed, contaminatorUsed, spiritualistUsed) VALUES ('Stevie', 5, 30, 8.0, 0.01, 0.01, TO_DATE('10/04/2018 08:00', 'dd/mm/yyyy hh24:mi'), 1.0, 1.0, 1.0, 1.0, 1/3, 1, 0, 0, 0, 1);
INSERT INTO Game(username, minPlayer, maxPlayer, dayStart, dayLength, nightLength, gameStart, contamination, insomniac, fortuneTeller, spiritualist, werewolf, nightTime, gameDone, fortuneTellerUsed, contaminatorUsed, spiritualistUsed) VALUES ('Paul', 5, 30, 8.0, 0.01, 0.01, TO_DATE('10/04/2018 08:00', 'dd/mm/yyyy hh24:mi'), 1.0, 1.0, 1.0, 1.0, 1/3, 1, 0, 0, 0, 1);
INSERT INTO Game(username, minPlayer, maxPlayer, dayStart, dayLength, nightLength, gameStart, contamination, insomniac, fortuneTeller, spiritualist, werewolf, nightTime, gameDone, fortuneTellerUsed, contaminatorUsed, spiritualistUsed) VALUES ('Tracy', 5, 30, 8.0, 0.01, 0.01, TO_DATE('10/04/2018 08:00', 'dd/mm/yyyy hh24:mi'), 1.0, 1.0, 1.0, 1.0, 1/3, 1, 0, 0, 0, 1);
INSERT INTO Game(username, minPlayer, maxPlayer, dayStart, dayLength, nightLength, gameStart, contamination, insomniac, fortuneTeller, spiritualist, werewolf, nightTime, gameDone, fortuneTellerUsed, contaminatorUsed, spiritualistUsed) VALUES ('Bruce', 5, 30, 8.0, 0.01, 0.01, TO_DATE('10/04/2018 08:00', 'dd/mm/yyyy hh24:mi'), 1.0, 1.0, 1.0, 1.0, 1/3, 1, 0, 0, 0, 1);
