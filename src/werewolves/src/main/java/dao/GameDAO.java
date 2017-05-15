package dao;

import java.util.concurrent.TimeUnit;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.io.IOException;

import java.sql.*;
import javax.sql.DataSource;
import javax.annotation.Resource;

import model.User;
import model.Game;

public class GameDAO {
	private final DataSource dataSource;
	private String user;

	public GameDAO(DataSource dataSource, String user){
		this.dataSource = dataSource;
		this.user = user;
	}

	public GameDAO(DataSource dataSource){
		this.dataSource = dataSource;
	}

	public Game createGame(int minPlayer, int maxPlayer, float dayStart, float dayLength, float nightLength, String gameStart, float contamination, float insomniac, float fortuneTeller, float spiritualist, float werewolf, boolean nightTime){
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		Game game = null;
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			java.util.Date parsed = format.parse(gameStart);
			Date gameDate = new Date(parsed.getTime() + TimeUnit.HOURS.toMillis((int) dayStart) + TimeUnit.MINUTES.toMillis((int) ((dayStart - (int) dayStart) * 100)));

			connection = dataSource.getConnection();
			String generatedColumns[] = {"gameId"};
			statement = connection.prepareStatement("INSERT INTO Game(username, minPlayer, maxPlayer, dayStart, dayLength, nightLength, gameStart, contamination, insomniac, fortuneTeller, spiritualist, werewolf, nightTime) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", generatedColumns);
			statement.setString(1, this.user);
			statement.setInt(2, minPlayer);
			statement.setInt(3, maxPlayer);
			statement.setFloat(4, dayStart);
			statement.setFloat(5, dayLength);
			statement.setFloat(6, nightLength);
			statement.setDate(7, gameDate);
			statement.setFloat(8, contamination);
			statement.setFloat(9, insomniac);
			statement.setFloat(10, fortuneTeller);
			statement.setFloat(11, spiritualist);
			statement.setFloat(12, werewolf);
			statement.setBoolean(13, nightTime);

			int affectedRows = statement.executeUpdate();
			if(affectedRows == 0){
				System.out.println("No affected rows");
			}
			resultSet = statement.getGeneratedKeys();
			resultSet.next();
			game = new Game(resultSet.getInt(1), this.user, minPlayer, maxPlayer, dayStart, dayLength, nightLength, gameDate, contamination, insomniac, fortuneTeller, spiritualist, werewolf, nightTime);
		} catch(SQLException se){
			System.out.println(se.getMessage());
		} finally {
			try { resultSet.close(); } catch(Exception e){ /* ignored */}
			try { statement.close(); } catch(Exception e){ /* ignored */}
			try { connection.close(); } catch(Exception e){ /* ignored */}
			return game;
		}
	}

	public void joinGame(int gameId){
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			connection = dataSource.getConnection();
			statement = connection.prepareStatement("INSERT INTO GameLobby(gameId, username) VALUES(?, ?)");
			statement.setInt(1, gameId);
			statement.setString(2, this.user);
			statement.executeUpdate();
		} catch(SQLException se){
			System.out.println(se.getMessage());
		} finally {
			try { statement.close(); } catch(Exception e){ /* ignored */}
			try { connection.close(); } catch(Exception e){ /* ignored */}
		}
	}


	public boolean checkMinPlayer(int gameId, int minPlayer){
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		boolean result = false;
		try {
			connection = dataSource.getConnection();
			statement = connection.prepareStatement("SELECT gameId FROM gameLobby WHERE gameId = ? HAVING COUNT(username) >= ? GROUP BY gameId"); 
			statement.setInt(1, gameId);
			statement.setInt(2, minPlayer);
			resultSet = statement.executeQuery();
			result = resultSet.next();
		} catch(SQLException se){
			System.out.println(se.getMessage());
		} finally {
			try { resultSet.close(); } catch(Exception e){ /* ignored */}
			try { statement.close(); } catch(Exception e){ /* ignored */}
			try { connection.close(); } catch(Exception e){ /* ignored */}
			return result;
		}
	}

	public boolean checkMaxPlayer(int gameId){
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		boolean result = false;
		try {
			connection = dataSource.getConnection();
			statement = connection.prepareStatement("SELECT gameId FROM gameLobby WHERE gameId = ? HAVING COUNT(username) < (SELECT maxPlayer FROM Game WHERE gameId = ?) GROUP BY gameId"); 
			statement.setInt(1, gameId);
			statement.setInt(2, gameId);
			resultSet = statement.executeQuery();
			result = resultSet.next();
		} catch(SQLException se){
			System.out.println(se.getMessage());
		} finally {
			try { resultSet.close(); } catch(Exception e){ /* ignored */}
			try { statement.close(); } catch(Exception e){ /* ignored */}
			try { connection.close(); } catch(Exception e){ /* ignored */}
			return result;
		}
	}

	public void changePeriod(int gameId, boolean night){
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			connection = dataSource.getConnection();
			statement = connection.prepareStatement("UPDATE Game SET nightTime = ? WHERE gameId = ?");
			statement.setInt(1, night ? 1 : 0);
			statement.setInt(2, gameId);
			statement.executeUpdate();
		} catch(SQLException se){
			System.out.println(se.getMessage());
		} finally {
			try { statement.close(); } catch(Exception e){ /* ignored */}
			try { connection.close(); } catch(Exception e){ /* ignored */}
		}
	}

	public void resetPowers(int gameId){
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			connection = dataSource.getConnection();
			statement = connection.prepareStatement("UPDATE Game SET fortuneTellerUsed = 0, contaminatorUsed = 0, spiritualistUsed = 0 WHERE gameId = ?");
			statement.setInt(1, gameId);
			statement.executeUpdate();
		} catch(SQLException se){
			System.out.println(se.getMessage());
		} finally {
			try { statement.close(); } catch(Exception e){ /* ignored */}
			try { connection.close(); } catch(Exception e){ /* ignored */}
		}
	}

	public void archiveMessages(int gameId, boolean archived){
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			connection = dataSource.getConnection();
			statement = connection.prepareStatement("UPDATE Message SET archived = ? WHERE gameId = ?");
			statement.setInt(1, archived ? 1 : 0);
			statement.setInt(2, gameId);
			statement.executeUpdate();
		} catch(SQLException se){
			System.out.println(se.getMessage());
		} finally {
			try { statement.close(); } catch(Exception e){ /* ignored */}
			try { connection.close(); } catch(Exception e){ /* ignored */}
		}
	}

	public void resetSpirit(int gameId){
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			connection = dataSource.getConnection();
			statement = connection.prepareStatement("UPDATE UserGame SET dead = 1 WHERE gameId = ? AND username = (SELECT username FROM UserGame WHERE gameId = ? AND dead = 2)");
			statement.setInt(1, gameId);
			statement.setInt(2, gameId);
			statement.executeUpdate();
		} catch(SQLException se){
			System.out.println(se.getMessage());
		} finally {
			try { statement.close(); } catch(Exception e){ /* ignored */}
			try { connection.close(); } catch(Exception e){ /* ignored */}
		}
	}

	public void deleteVotes(int gameId){
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			connection = dataSource.getConnection();
			statement = connection.prepareStatement("DELETE FROM UserVote WHERE gameId = ?");
			statement.setInt(1, gameId);
			statement.executeUpdate();
		} catch(SQLException se){
			System.out.println(se.getMessage());
		} finally {
			try { statement.close(); } catch(Exception e){ /* ignored */}
			try { connection.close(); } catch(Exception e){ /* ignored */}
		}
	}

	public String checkEnd(int gameId){
		Connection connection = null;
		CallableStatement statement = null;
		String output = null;
		try {
			connection = dataSource.getConnection();
			statement = connection.prepareCall("{? = call checkEnd(?)}");
			statement.registerOutParameter(1, Types.VARCHAR);
			statement.setInt(2, gameId);
			statement.execute();
			output = statement.getString(1);
		} catch(SQLException se){
			System.out.println(se.getMessage());
		} finally {
			try { statement.close(); } catch(Exception e){ /* ignored */}
			try { connection.close(); } catch(Exception e){ /* ignored */}
			return output;
		}
	}

	public void startGame(int gameId){
		Connection connection = null;
		CallableStatement statement = null;
		try {
			connection = dataSource.getConnection();
			statement = connection.prepareCall("{call startGame(?)}");
			statement.setInt(1, gameId);
			statement.execute();
		} catch(SQLException se){
			System.out.println(se.getMessage());
		} finally {
			try { statement.close(); } catch(Exception e){ /* ignored */}
			try { connection.close(); } catch(Exception e){ /* ignored */}
		}
	}

	public void leaveGame(String username, int gameId){
		Connection connection = null;
		CallableStatement statement = null;
		try {
			connection = dataSource.getConnection();
			statement = connection.prepareCall("{call leaveGame(?, ?)}");
			statement.setString(1, username);
			statement.setInt(2, gameId);
			statement.execute();
		} catch(SQLException se){
			System.out.println(se.getMessage());
		} finally {
			try { statement.close(); } catch(Exception e){ /* ignored */}
			try { connection.close(); } catch(Exception e){ /* ignored */}
		}
	}

	public Boolean isDay(int gameId) {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		Boolean result = true;
		try {
			connection = dataSource.getConnection();
			statement = connection.prepareStatement("SELECT nightTime FROM Game WHERE gameId = ?");
			statement.setInt(1, gameId);
			rs = statement.executeQuery();
			rs.next();
			if (rs.getInt(1) == 1) {
				result = false;
			}
		} catch(SQLException se){
			result = null;
			System.out.println(se.getMessage());
		} finally {
			try { rs.close(); } catch(Exception e){ /* ignored */}
			try { statement.close(); } catch(Exception e){ /* ignored */}
			try { connection.close(); } catch(Exception e){ /* ignored */}
		}
		return result;
	}
}
