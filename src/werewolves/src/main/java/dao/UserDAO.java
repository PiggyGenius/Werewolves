package dao;

import java.sql.*;
import javax.sql.DataSource;
import javax.annotation.Resource;
import java.util.ArrayList;
import model.*;

public class UserDAO {
	private final DataSource dataSource;

	public UserDAO(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public User login(String user, String password){
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		boolean valid = false;
		try {
			connection = dataSource.getConnection();
			statement = connection.prepareStatement("SELECT username FROM Client WHERE username = ? AND password = ?");
			statement.setString(1, user);
			statement.setString(2, password);
			resultSet = statement.executeQuery();
			valid = resultSet.next();
		} catch(SQLException se){
			System.out.println(se.getMessage());
		} finally {
			try { resultSet.close(); } catch(Exception e){ /* ignored */}
			try { statement.close(); } catch(Exception e){ /* ignored */}
			try { connection.close(); } catch(Exception e){ /* ignored */}
			return valid ? new User(user) : null;
		}
	}

	public User createAccount(String user, String password){
		Connection connection = null;
		PreparedStatement statement = null;
		Boolean valid = false;
		try {
			connection = this.dataSource.getConnection();
			statement = connection.prepareStatement("INSERT INTO Client VALUES(?, ?)");
			statement.setString(1, user);
			statement.setString(2, password);
			statement.executeUpdate();
			valid = true;
		} catch(SQLException se){
			System.out.println(se.getMessage());
		} finally {
			try { statement.close(); } catch(Exception e){ /* ignored */}
			try { connection.close(); } catch(Exception e){ /* ignored */}
			return valid ? new User(user) : null;
		}
	}

	public UserInfos getUserInfos(int gameId, String username) {
		UserInfos result = null;
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			connection = this.dataSource.getConnection();
			statement = connection.prepareStatement("SELECT role, dead, nighttime, fortuneTellerUsed, contaminatorUsed, spiritualistUsed FROM UserGame, Game WHERE UserGame.gameId = Game.gameId AND Game.gameId = ? AND UserGame.username = ?");
			statement.setInt(1, gameId);
			statement.setString(2, username);

			rs = statement.executeQuery();
			rs.next();

			Role role = Role.fromString(rs.getString(1));
			// SPECIAL POWER
			Boolean powerUsed = null;
			if (role == Role.FortuneTeller) {
				powerUsed = rs.getBoolean(4);
			} else if (role == Role.Contaminator) {
				powerUsed = rs.getBoolean(5);
			} else if (role == Role.Spiritualist) {
				powerUsed = rs.getBoolean(6);
			}

			// DEAD (eventually chosen by spiritualist)
			int dead = rs.getInt(2);
			if (dead == 0) {
				result = new UserInfos(role, false, false, rs.getBoolean(3), powerUsed);
			} else if (dead == 1) {
				result = new UserInfos(role, true, false, rs.getBoolean(3), powerUsed);
			} else if (dead == 2) {
				result = new UserInfos(role, true, true, rs.getBoolean(3), powerUsed);
			} else {
				result = null;
			}
		} catch(SQLException se) {
			result = null;
			System.out.println(se.getMessage());
		} finally {
			try {rs.close();} catch(Exception e) {/* ignored */}
			try {connection.close();} catch(Exception e) {/* ignored */}
			try {statement.close();} catch(Exception e) {/* ignored */}
		}
		return result;
	}

	public UserInfosMap getUserInfosMap(int gameId) {
		UserInfosMap result = null;

		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			connection = this.dataSource.getConnection();
			statement = connection.prepareStatement("SELECT username, role, dead FROM UserGame WHERE gameId = ?");
			statement.setInt(1, gameId);

			rs = statement.executeQuery();
			result = new UserInfosMap();
			while(rs.next()) {
				// we don't need to know if it's been chosen, if it's nighttime or if it has used his power
				result.put(rs.getString(1), new UserInfos(Role.fromString(rs.getString(2)), rs.getBoolean(3), null, null, null));
			}
		} catch(SQLException se) {
			result = null;
			System.out.println(se.getMessage());
		} finally {
			try {rs.close();} catch(Exception e) {/* ignored */}
			try {connection.close();} catch(Exception e) {/* ignored */}
			try {statement.close();} catch(Exception e) {/* ignored */}
		}
		return result;
	}

	public boolean specialPowerSpiritualist(int gameId, String target) {
		boolean error = false;

		Connection connection = null;
		PreparedStatement statement = null;
		try {
			connection = this.dataSource.getConnection();
			statement = connection.prepareStatement("UPDATE UserGame SET dead = 2 WHERE gameId = ? AND username = ?");
			statement.setInt(1, gameId);
			statement.setString(2, target);
			statement.executeUpdate();
		} catch(SQLException se) {
			error = true;
			System.out.println(se.getMessage());
		} finally {
			try {connection.close();} catch(Exception e) {/* ignored */}
			try {statement.close();} catch(Exception e) {/* ignored */}
		}
		return error;
	}


	public Role specialPowerFortuneTeller(int gameId, String target) {
		UserInfos infos = this.getUserInfos(gameId, target);

		Connection connection = null;
		PreparedStatement statement = null;
		try {
			connection = this.dataSource.getConnection();
			statement = connection.prepareStatement("UPDATE Game SET fortuneTellerUsed = 1 WHERE gameId = ?");
			statement.setInt(1, gameId);
			statement.executeUpdate();
		} catch(SQLException se) {
			infos = null;
			System.out.println(se.getMessage());
		} finally {
			try {connection.close();} catch(Exception e) {/* ignored */}
			try {statement.close();} catch(Exception e) {/* ignored */}
		}
		if (infos == null) {
			return null;
		}
		return infos.getRole();
	}


	public boolean specialPowerContaminator(int gameId, String target) {
		boolean error = false;

		Connection connection = null;
		PreparedStatement statement = null;
		try {
			connection = this.dataSource.getConnection();
			statement = connection.prepareStatement("UPDATE UserGame SET role = 'WEREWOLF' WHERE gameId = ? AND username = ?");
			statement.setInt(1, gameId);
			statement.setString(2, target);
			statement.executeUpdate();
		} catch(SQLException se) {
			error = true;
			System.out.println(se.getMessage());
		} finally {
			try {connection.close();} catch(Exception e) {/* ignored */}
			try {statement.close();} catch(Exception e) {/* ignored */}
		}
		return error;
	}
}
