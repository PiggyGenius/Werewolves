package dao;

import java.sql.*;
import javax.sql.DataSource;
import javax.annotation.Resource;
import java.lang.Exception;
import model.User;
import model.GameBoard;
import model.Game;
import model.RunningGame;
import model.IngameUser;

public class GameBoardDAO {
	private final DataSource dataSource;
	private String user;

	public GameBoardDAO(DataSource dataSource){
		this.dataSource = dataSource;
	}

	public GameBoardDAO(DataSource dataSource, String user){
		this.dataSource = dataSource;
		this.user = user;
	}

	public GameBoard getGameBoard(int maxId){
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		GameBoard gameBoard = new GameBoard();
		try {
			connection = dataSource.getConnection();
			statement = connection.prepareStatement("SELECT * FROM Game G JOIN GameLobby L ON G.gameId = L.gameId WHERE L.lobbyId > ? AND L.username = ?");
			statement.setInt(1, maxId);
			statement.setString(2, this.user);
			resultSet = statement.executeQuery();
			int lobbyId = 0;
			while(resultSet.next()){
				gameBoard.add(new Game(resultSet.getInt(1),
							resultSet.getString(2), resultSet.getInt(3),
							resultSet.getInt(4), resultSet.getFloat(5),
							resultSet.getFloat(6),resultSet.getFloat(7),
							resultSet.getDate(8), resultSet.getFloat(9),
							resultSet.getFloat(10), resultSet.getFloat(11),
							resultSet.getFloat(12), resultSet.getFloat(13),
							resultSet.getBoolean(14))); 
				lobbyId = resultSet.getInt(21);
				maxId = maxId < lobbyId ? lobbyId : maxId;
			}
			gameBoard.setMaxId(maxId);
		} catch(SQLException e){
			System.out.println(e.getMessage());
		} finally {
			try { resultSet.close(); } catch(Exception e){ /* ignored */}
			try { statement.close(); } catch(Exception e){ /* ignored */}
			try { connection.close(); } catch(Exception e){ /* ignored */}
			return gameBoard;
		}
	}

	public GameBoard getPublicGameBoard(int maxId){
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		GameBoard gameBoard = new GameBoard();
		try {
			connection = dataSource.getConnection();
			statement = connection.prepareStatement("SELECT * FROM Game NATURAL JOIN GameLobby WHERE lobbyId > ?");
			statement.setInt(1, maxId);
			resultSet = statement.executeQuery();
			int lobbyId = 0;
			while(resultSet.next()){
				gameBoard.add(new Game(resultSet.getInt(1),
							resultSet.getString(2), resultSet.getInt(3),
							resultSet.getInt(4), resultSet.getFloat(5),
							resultSet.getFloat(6),resultSet.getFloat(7),
							resultSet.getDate(8), resultSet.getFloat(9),
							resultSet.getFloat(10), resultSet.getFloat(11),
							resultSet.getFloat(12), resultSet.getFloat(13),
							resultSet.getBoolean(14))); 
				lobbyId = resultSet.getInt(19);
				maxId = maxId < lobbyId ? lobbyId : maxId;
			}
			gameBoard.setMaxId(maxId);
		} catch(SQLException se){
			System.out.println(se.getMessage());
		} finally {
			try { resultSet.close(); } catch(Exception e){ /* ignored */}
			try { statement.close(); } catch(Exception e){ /* ignored */}
			try { connection.close(); } catch(Exception e){ /* ignored */}
			return gameBoard;
		}
	}

	public GameBoard getRunningGameBoard(int maxId){
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		GameBoard gameBoard = new GameBoard();
		try {
			connection = dataSource.getConnection();
			statement = connection.prepareStatement("SELECT * FROM Game G JOIN UserGame U ON G.gameId = U.gameId WHERE U.lobbyId > ? AND U.username = ?");
			statement.setInt(1, maxId);
			statement.setString(2, this.user);
			resultSet = statement.executeQuery();
			int lobbyId = 0;
			while(resultSet.next()){
				gameBoard.add(new RunningGame(resultSet.getInt(1),
							resultSet.getString(2), resultSet.getInt(3),
							resultSet.getInt(4), resultSet.getFloat(5),
							resultSet.getFloat(6),resultSet.getFloat(7),
							resultSet.getDate(8), resultSet.getFloat(9),
							resultSet.getFloat(10), resultSet.getFloat(11),
							resultSet.getFloat(12), resultSet.getFloat(13),
							resultSet.getBoolean(14),
							new IngameUser(resultSet.getString(19), resultSet.getString(21), resultSet.getBoolean(22)))); 
				lobbyId = resultSet.getInt(23);
				maxId = maxId < lobbyId ? lobbyId : maxId;
			}
			gameBoard.setMaxId(maxId);
		} catch(SQLException se){
			System.out.println(se.getMessage());
		} finally {
			try { resultSet.close(); } catch(Exception e){ /* ignored */}
			try { statement.close(); } catch(Exception e){ /* ignored */}
			try { connection.close(); } catch(Exception e){ /* ignored */}
			return gameBoard;
		}
	}
}
