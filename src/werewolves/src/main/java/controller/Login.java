package controller;

import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.sql.DataSource;
import javax.annotation.Resource;
import dao.UserDAO;
import model.User;

/** Login controller */
@WebServlet(urlPatterns = {"/login"})
public class Login extends HttpServlet {
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
		request.setCharacterEncoding("UTF-8");


		if(request.getSession().getAttribute("user") != null)
			request.getRequestDispatcher("/WEB-INF/gameboard.jsp").forward(request, response);
		else {
			UserDAO userDAO = new UserDAO(dataSource);
			PrintWriter out = response.getWriter();
			User user = userDAO.login(request.getParameter("login"), request.getParameter("password"));
			if(user == null)
				request.getRequestDispatcher("/WEB-INF/login_error.html").forward(request, response);
			else {
				request.getSession().setAttribute("user", user);
				request.getSession().setAttribute("login", true);
				request.getRequestDispatcher("/WEB-INF/gameboard.jsp").forward(request, response);
			}
		}
	}

	/** 
	 * Verify that the (username, password) is correct and belongs to the database.
	 * @param request Http request that called the servlet
	 * @param response Http response to be to client
	 * @throws ServletException General error on servlet
	 * @throws IOException Input/Output error
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");

		if(request.getSession().getAttribute("user") != null)
			request.getRequestDispatcher("/WEB-INF/gameboard.jsp").forward(request, response);
		else {
			UserDAO userDAO = new UserDAO(dataSource);
			PrintWriter out = response.getWriter();
			User user = userDAO.login(request.getParameter("login"), request.getParameter("password"));
			if(user == null)
				request.getRequestDispatcher("/WEB-INF/login_error.html").forward(request, response);
			else {
				request.getSession().setAttribute("user", user);
				request.getRequestDispatcher("/WEB-INF/gameboard.jsp").forward(request, response);
			}
		}
	}
}
