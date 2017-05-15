package controller;

import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.sql.DataSource;
import javax.annotation.Resource;

/** Login controller */
@WebServlet(urlPatterns = {"/logout"})
public class Logout extends HttpServlet {

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

		request.getSession().setAttribute("login", false);
		HttpSession session = request.getSession();
		if(session != null){
			session.invalidate();
			request.getRequestDispatcher("index.html").forward(request, response);
		}
	}
}
