package controller;

import java.io.*;

import javax.sql.DataSource;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.annotation.Resource;

import dao.UserDAO;
import dao.GameBoardDAO;
import dao.GameDAO;

import model.User;
import model.Game;
import model.GameBoard;


/** GameLobby controller */
@WebServlet(urlPatterns = {"/gameLobby"})
public class GameLobby extends HttpServlet {
	@Resource(name="jdbc/werewolves")
	private DataSource dataSource;

	/** 
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


		String username = null;
		if(request.getSession().getAttribute("user") != null){
			username = ((User) request.getSession().getAttribute("user")).getUsername();
		}
		String action = request.getParameter("submit");
		int gameId = Integer.parseInt(request.getParameter("gameId"));
		GameDAO gameDAO = new GameDAO(dataSource, username);
		if(action.equals("Join")){
			if(username == null){
				request.getRequestDispatcher("connection.html").forward(request, response);
			}
			else {
				if(gameDAO.checkMaxPlayer(gameId)){
					gameDAO.joinGame(gameId);
					request.getRequestDispatcher("/WEB-INF/gameboard.jsp").forward(request, response);
				}
				else
					request.getRequestDispatcher("index.html").forward(request, response);
			}
		}
		else if(action.equals("leave")){
			request.getRequestDispatcher("/WEB-INF/game_board.jsp").forward(request, response);
		}
	}
}
