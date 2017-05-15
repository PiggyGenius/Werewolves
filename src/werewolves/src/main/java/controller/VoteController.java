
package controller;

import java.io.*;
import javax.sql.DataSource;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.annotation.Resource;

import dao.VoteDAO;
import model.*;
import model.vote.*;

/** VoteController controller */
@WebServlet(urlPatterns = {"/voteController"})
public class VoteController extends HttpServlet {

	@Resource(name="jdbc/werewolves")
	public DataSource ds;

	/** 
	 * @param request Http request that called the servlet
	 * @param response Http response to be to client
	 * @throws ServletException General error on servlet
	 * @throws IOException Input/Output error
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		try {
			String action = request.getParameter("action");
			VoteDAO dao = new VoteDAO(ds);

			if (action.equals("getPlayers")) { // get list of players with their votes
				UserVoteMap votes = dao.getVotes(Integer.parseInt(request.getParameter("gameId")), 
						((User)request.getSession().getAttribute("user")).getUsername());
				if (votes == null)
					throw new ServletException();
				response.getWriter().write(votes.toJSON());

			} else if (action.equals("vote")) {
				boolean error = dao.voteForUser(
						Integer.parseInt(request.getParameter("gameId")),
						request.getParameter("target"),
						((User)request.getSession().getAttribute("user")).getUsername());
				if (error)
					throw new ServletException();

			} else {
				throw new ServletException();
			}

		} catch (Exception e) {
			response.setContentType("text/html");
			response.setCharacterEncoding("UTF-8");
			response.sendError(400);
		}
	}
}
