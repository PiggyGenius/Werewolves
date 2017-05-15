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
@WebServlet(urlPatterns = {"/create_account"})
public class CreateAccount extends HttpServlet {
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
		UserDAO userDAO = new UserDAO(dataSource);
		User user = userDAO.createAccount(request.getParameter("login"), request.getParameter("password"));
		PrintWriter out = response.getWriter();
		out.println("SUCCESS");
		response.setContentType("text/html");
		out.println("</br><a href=\"login.html\">login</a>");
	}
}
