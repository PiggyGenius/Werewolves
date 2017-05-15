package controller;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.annotation.Resource;

/** GameLobby controller */
@WebServlet(urlPatterns = {"/loginUser"})
public class LoginUser extends HttpServlet {

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
			if(request.getSession().getAttribute("login") != null)
				response.getWriter().write((boolean) request.getSession().getAttribute("login") ? "true" : "false");
			else
				response.getWriter().write("false");
		}
		else
			response.getWriter().write("false");
	}
}
