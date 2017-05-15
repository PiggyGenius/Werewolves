
package dao;

import java.io.*;
import javax.sql.DataSource;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import javax.annotation.Resource;
import java.sql.*;

import model.*;
import model.vote.*;

public class VoteDAO {

	private DataSource ds;

	public VoteDAO(DataSource ds) {
		this.ds = ds;
	}

	/* idea:
	   create the map for each user with a vote, set isDead
	   update the votes
	*/
	public UserVoteMap getVotes(int gameId, String user) {
		UserVoteMap votes = new UserVoteMap();

		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			connection = ds.getConnection();

			statement = connection.prepareStatement("SELECT username, dead, voter FROM UserGame G LEFT OUTER JOIN UserVote V ON G.username = V.target AND G.gameId = V.gameId WHERE G.gameId = ?");
			statement.setInt(1, gameId);
			rs = statement.executeQuery();

			Vote currVote;
			String target;
			String voter;
			while (rs.next()) {
				target = rs.getString(1);
				voter = rs.getString(3);

				// if it exists, nothing to do
				if (votes.containsKey(target)) {
					currVote = votes.get(target);
				// else, we create a new vote for this user
				} else {
					currVote = new Vote();
					if (rs.getInt(2) == 1 || rs.getInt(2) == 2) {
						currVote.setIsDead();
					}
					votes.put(target, currVote);
				}
				// eventually, increment the vote count and set it as hasVoted
				if (voter != null) {
					currVote.inc();
					if (voter.equals(user)) {
						currVote.setHasVoted();
					}
				}
			}

		} catch (SQLException se) {
			votes = null;
			System.out.println(se.getMessage());
		} finally {
			try { rs.close(); } catch(Exception e){ /* ignored */}
			try { statement.close(); } catch(Exception e){ /* ignored */}
			// try { statement1.close(); } catch(Exception e){ [> ignored <]}
			// try { statement2.close(); } catch(Exception e){ [> ignored <]}
			try { connection.close(); } catch(Exception e){ /* ignored */}
		}
		return votes;

	}


	public boolean voteForUser(int gameId, String target, String voter) {
		Connection connection = null;
		CallableStatement statement = null;
		boolean error = false;
		try {
			connection = ds.getConnection();
			statement = connection.prepareCall("{call voteInsert(?, ?, ?)}");
			statement.setInt(1, gameId);
			statement.setString(2, target);
			statement.setString(3, voter);
			statement.executeUpdate();
		} catch(Exception e) {
			error = true;
			e.printStackTrace();
			System.out.println(e.getMessage());
		} finally {
			try { statement.close(); } catch(Exception e){/* ignored */}
			try { connection.close(); } catch(Exception e){/* ignored */}
			return error;
		}
	}
}
