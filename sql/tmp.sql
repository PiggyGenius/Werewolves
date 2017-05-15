INSERT INTO game(username, contamination, fortuneTeller, spiritualist, werewolf, insomniac, gameStart, dayLength, nightLength, minPlayer) VALUES('piggy', 1,1,1,0.3,1,TO_DATE('08/04/2017 21:31', 'dd/mm/yyyy hh24:mi'), 0.01, 0.02, 1);
INSERT INTO gameLobby(username, gameId) values('meanstreet', 8);
INSERT INTO gameLobby(username, gameId) values('a', 8);
INSERT INTO gameLobby(username, gameId) values('Raymond', 8);
INSERT INTO gameLobby(username, gameId) values('Michel', 8);
INSERT INTO gameLobby(username, gameId) values('Tifany73', 8);
INSERT INTO gameLobby(username, gameId) values('Carole', 8);
INSERT INTO gameLobby(username, gameId) values('Bruce', 8);
CALL startGame(8);

DROP PROCEDURE startGame;
DELETE FROM UserGame WHERE GameId = 8;
DELETE FROM Game WHERE gameId = 8;
SELECT * from UserGame;
