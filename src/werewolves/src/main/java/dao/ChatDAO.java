package dao;

import java.io.*;
import javax.sql.DataSource;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import javax.annotation.Resource;
import java.sql.*;
import java.util.EnumMap;

import model.*;
import model.chat.*;

import oracle.jdbc.OracleTypes;


public class ChatDAO {

	private DataSource ds;

	public ChatDAO(DataSource ds) {
		this.ds = ds;
	}

	public boolean submitMessage(String content, ChatRoomType type, String author, int gameId) {
		boolean error = false;

		Connection connection = null;
		PreparedStatement statement = null;
		try {
			connection = ds.getConnection();
			statement = connection.prepareStatement("INSERT INTO Message(content, context, username, gameId, archived) VALUES(?, ?, ?, ?, ?)");
			statement.setString(1, content);
			statement.setString(2, type.getName());
			statement.setString(3, author);
			statement.setInt(4, gameId);
			statement.setInt(5, 0);

			statement.executeUpdate();
		} catch (SQLException se){
			error = true;
			System.out.println(se.getMessage());
		} finally {
			try { statement.close(); } catch(Exception e){ /* ignored */}
			try { connection.close(); } catch(Exception e){ /* ignored */}
		}
		return error;
	}

	public ChatRoom getNewMessages(int lastMsg, int gameId, ChatRoomType type, String user) {
		ChatRoom room = null;

		Connection connection = null;
		CallableStatement statement = null;
		ResultSet rs = null;

		try {
			connection = ds.getConnection();
			statement = connection.prepareCall("{? = call getNewMessages(?, ?, ?, ?)}");
			statement.registerOutParameter(1, OracleTypes.CURSOR);
			statement.setString(2, user);
			statement.setInt(3, gameId);
			statement.setInt(4, lastMsg);
			statement.setString(5, type.getName());

			statement.execute();
			rs = (ResultSet)statement.getObject(1);

			room = new ChatRoom(type);
			while (rs.next()) {
				// add message to the ChatRoom
				room.addMessage(new ChatMessage(rs.getString(4), rs.getDate(3), rs.getString(2), rs.getInt(1)));
				// the lastId is automatically set in the chatRoom
			}
		} catch (SQLException se){
			room = null;
			System.out.println(se.getMessage());
			se.printStackTrace();
		} finally {
			try { rs.close(); } catch(Exception e){/* ignored */}
			try { statement.close(); } catch(Exception e){/* ignored */}
			try { connection.close(); } catch(Exception e){/* ignored */}
		}
		return room;
	}

	public ChatRoomAuthorization getAuthorizedChats(String username, int gameId) {

		ChatRoomAuthorization result = null;
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			connection = ds.getConnection();
			statement = connection.prepareStatement("SELECT nighttime, dead, role, gamedone FROM UserGame U, Game G WHERE U.gameId = G.gameId AND G.gameId = ? AND U.username = ?");
			statement.setInt(1, gameId);
			statement.setString(2, username);

			rs = statement.executeQuery();
			rs.next();

			boolean isDay = !rs.getBoolean(1);
			int dead = rs.getInt(2);
			boolean isDead = (dead != 0);
			boolean spiritualChosen = (dead == 2);
			Role role = Role.fromString(rs.getString(3));
			boolean gameFinished = rs.getBoolean(4);

			// create authorizations, all at false by default
			result = new ChatRoomAuthorization();
			for (ChatRoomType type : ChatRoomType.values()) {
				result.put(type, new ReadWriteAuthorization(false, false));
			}

			// DEAD OR FINISHED
			if (isDead || gameFinished) {
				result.get(ChatRoomType.VILLAGE).read();
				result.get(ChatRoomType.WEREWOLF).read();
				if (spiritualChosen && !gameFinished) {
					result.get(ChatRoomType.SPIRITUAL).readWrite();
				} else {
					result.get(ChatRoomType.SPIRITUAL).read();
				}
				// DAY
			} else if (isDay) {
				result.get(ChatRoomType.VILLAGE).readWrite();
				// NIGHT
			} else {
				if (role == Role.Werewolf || role == Role.Contaminator) {
					result.get(ChatRoomType.WEREWOLF).readWrite();
				} else if (role == Role.Insomniac) {
					result.get(ChatRoomType.WEREWOLF).read();
				} else if (role == Role.Spiritualist) {
					result.get(ChatRoomType.SPIRITUAL).readWrite();
				}
			}

		} catch (SQLException se){
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
