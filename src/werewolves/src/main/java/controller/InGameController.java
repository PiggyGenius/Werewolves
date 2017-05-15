
package controller;

import java.io.*;
import javax.sql.DataSource;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.annotation.Resource;

import model.*;
import dao.UserDAO;
import dao.GameDAO;

/** InGameController controller */
@WebServlet(urlPatterns = {"/inGameController"})
public class InGameController extends HttpServlet {

	@Resource(name="jdbc/werewolves")
	public DataSource ds;

	/** 
	 * @param request Http request that called the servlet
	 * @param response Http response to be to client
	 * @throws ServletException General error on servlet
	 * @throws IOException Input/Output error
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		try {

			int gameId = Integer.parseInt(request.getParameter("gameId"));
			String username = ((User)request.getSession().getAttribute("user")).getUsername();

			request.setAttribute("game", gameId);
			request.setAttribute("lastMessage", 0);

			request.getRequestDispatcher("/WEB-INF/in_game.jsp").forward(request, response);
		} catch (Exception e) {}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		try {
			UserDAO dao = new UserDAO(this.ds);
			String action = request.getParameter("action");
			if (action.equals("getInfos")) {
				UserInfos infos = dao.getUserInfos(
						Integer.parseInt(request.getParameter("gameId")),
						((User)request.getSession().getAttribute("user")).getUsername());
				if (infos == null)
					throw new ServletException();
				response.getWriter().write(infos.toJSON());

			} else if (action.equals("getPlayers")) {
				UserInfosMap infosMap = dao.getUserInfosMap(
						Integer.parseInt(request.getParameter("gameId")));
				if (infosMap == null)
					throw new ServletException();
				response.getWriter().write(infosMap.toJSON());

			} else if (action.equals("checkEndOfGame")) {
				String result = (new GameDAO(this.ds)).checkEnd(
						Integer.parseInt(request.getParameter("gameId")));
				if (result == null)
					throw new ServletException();
				response.getWriter().write("{\"result\": \"" + result + "\"}");

			} else if (action.equals("specialPowerSPIRITUALIST")) {
				boolean error = dao.specialPowerSpiritualist(
						Integer.parseInt(request.getParameter("gameId")),
						request.getParameter("target"));
				if (error)
					throw new ServletException();
				response.getWriter().write("{}");

			} else if (action.equals("specialPowerFORTUNETELLER")) {
				String target = request.getParameter("target");
				Role role = dao.specialPowerFortuneTeller(
						Integer.parseInt(request.getParameter("gameId")), target);
				if (role == null)
					throw new ServletException();
				response.getWriter().write("{\"username\": \"" + target + "\", \"role\": \"" + role + "\"}");

			} else if (action.equals("specialPowerCONTAMINATOR")) {
				boolean error = dao.specialPowerContaminator(
						Integer.parseInt(request.getParameter("gameId")),
						request.getParameter("target"));
				if (error)
					throw new ServletException();
				response.getWriter().write("{}");
			}
		} catch (Exception e) {
			response.setContentType("text/html");
			response.setCharacterEncoding("UTF-8");
			response.sendError(400);
		}
	}
}
