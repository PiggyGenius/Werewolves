package controller;

import java.io.*;

import javax.sql.DataSource;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.annotation.Resource;

import dao.UserDAO;
import dao.GameBoardDAO;

import model.User;
import model.Game;
import model.GameBoard;


/** GameBoard controller */
@WebServlet(urlPatterns = {"/gameBoard"})
public class GameBoardFetcher extends HttpServlet {
	@Resource(name="jdbc/werewolves")
	private DataSource dataSource;

	/** 
	 * @param request Http request that called the servlet
	 * @param response Http response to be to client
	 * @throws ServletException General error on servlet
	 * @throws IOException Input/Output error
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/plain");

		String username = null;
		if(request.getSession().getAttribute("user") != null){
			username = ((User) request.getSession().getAttribute("user")).getUsername();
		}
		int maxId = Integer.parseInt(request.getParameter("maxId"));
		int action = Integer.parseInt(request.getParameter("action"));
		GameBoardDAO gameBoardDAO = new GameBoardDAO(dataSource, username);
		GameBoard gameBoard = null;
		if(action == 1)
			gameBoard = gameBoardDAO.getPublicGameBoard(maxId);
		else if(action == 2)
			gameBoard = gameBoardDAO.getGameBoard(maxId);
		else
			gameBoard = gameBoardDAO.getRunningGameBoard(maxId);
		response.getWriter().write(gameBoard.toJSON());
	}
}
