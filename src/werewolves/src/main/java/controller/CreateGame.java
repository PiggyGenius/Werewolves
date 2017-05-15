package controller;

import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.sql.DataSource;
import javax.annotation.Resource;

import model.Game;
import model.User;
import dao.GameDAO;

/** Login controller */
@WebServlet(urlPatterns = {"/createGame"})
public class CreateGame extends HttpServlet {
	@Resource(name="jdbc/werewolves")
	private DataSource dataSource;

	/** 
	 * Verify that the (username, password) is correct and belongs to the database.
	 * @param request Http request that called the servlet
	 * @param response Http response to be to client
	 * @throws ServletException General error on servlet
	 * @throws IOException Input/Output error
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/plain");

		if(request.getSession() != null){
			if(request.getSession().getAttribute("login") != null){
				if(!(boolean) request.getSession().getAttribute("login"))
					request.getRequestDispatcher("connection.html").forward(request, response);
			}
			else
				request.getRequestDispatcher("connection.html").forward(request, response);
		}
		else
			request.getRequestDispatcher("connection.html").forward(request, response);
		//if(request.getSession().getAttribute("user") == null){
			//request.getRequestDispatcher("connection.html").forward(request, response);
		//}
		String username = ((User) request.getSession().getAttribute("user")).getUsername();
		int minPlayer = Integer.parseInt(request.getParameter("minPlayer"));
		int maxPlayer = Integer.parseInt(request.getParameter("maxPlayer"));
		float dayStart = Float.parseFloat(request.getParameter("dayHours")) +
			(Float.parseFloat(request.getParameter("dayMinutes")) / 100);
		float dayLength = Float.parseFloat(request.getParameter("dayLengthHours")) +
			(Float.parseFloat(request.getParameter("dayLengthMinutes")) / 100);
		float nightLength = Float.parseFloat(request.getParameter("nightLengthHours")) +
			(Float.parseFloat(request.getParameter("nightLengthMinutes")) / 100);
		String gameStart = request.getParameter("gameDate");
		float contamination = Float.parseFloat(request.getParameter("contamination"));
		float insomniac = Float.parseFloat(request.getParameter("insomniac"));
		float fortuneTeller = Float.parseFloat(request.getParameter("fortuneTeller"));
		float spiritualist = Float.parseFloat(request.getParameter("spiritualist"));
		float werewolf = Float.parseFloat(request.getParameter("werewolf"));

		GameDAO gameDAO = new GameDAO(dataSource, username);
		Game game = gameDAO.createGame(minPlayer, maxPlayer, dayStart, dayLength, nightLength, gameStart, contamination, insomniac, fortuneTeller, spiritualist, werewolf, false);

		request.getRequestDispatcher("/WEB-INF/gameboard.jsp").forward(request, response);
	}
}
