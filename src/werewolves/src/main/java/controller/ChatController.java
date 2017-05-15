
package controller;

import java.io.*;
import javax.sql.DataSource;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.annotation.Resource;
import dao.ChatDAO;
import model.*;
import model.chat.*;

/** ChatController controller */
@WebServlet(urlPatterns = {"/chatController"})
public class ChatController extends HttpServlet {

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
			ChatDAO dao = new ChatDAO(this.ds);

			String action = request.getParameter("action");
			if (action.equals("getNewMessages")) { // get new messages
				String chat = request.getParameter("chat");
				if (chat != null && !chat.equals("")) {
					ChatRoom newMessages = dao.getNewMessages(
						Integer.parseInt(request.getParameter("lastMessage")),
						Integer.parseInt(request.getParameter("gameId")),
						ChatRoomType.fromString(chat),
						((User)request.getSession().getAttribute("user")).getUsername());
					if (newMessages == null)
						throw new ServletException();
					response.getWriter().write(newMessages.toJSON());
				} else {
					response.getWriter().write("{\"lastId\": 0}");
				}

			} else if (action.equals("submitMessage")) { // submit a message
				boolean error = dao.submitMessage(request.getParameter("message"), 
						ChatRoomType.fromString(request.getParameter("chat")),
						((User)request.getSession().getAttribute("user")).getUsername(),
						Integer.parseInt(request.getParameter("gameId")));
				if (error)
					throw new ServletException();

			} else if (action.equals("getAuthorizedChats")) { // get the list of authorized chats
				ChatRoomAuthorization chatRooms = dao.getAuthorizedChats(
						((User)request.getSession().getAttribute("user")).getUsername(),
						Integer.parseInt(request.getParameter("gameId")));
				if (chatRooms == null)
					throw new ServletException();
				response.getWriter().write(chatRooms.toJSON());

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
