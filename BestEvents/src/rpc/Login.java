package rpc;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;

/**
 * Servlet implementation class Login
 */
@WebServlet("/login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 *	Handles HTTP requests to login - check if a valid session exists for userId provided in URL
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DBConnection connection = DBConnectionFactory.getConnection();
		try {
			HttpSession session = request.getSession(false);
			JSONObject obj = new JSONObject();
			if (session != null) {
				String userId = session.getAttribute("user_id").toString();
				String name = session.getAttribute("name").toString();
				obj.put("status", "OK").put("user_id", userId).put("name", name);
			} else {
				response.setStatus(430);
				obj.put("status", "Session Invalid");
			}
			
			RpcHelper.writeJsonObject(response, obj);
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			connection.close();
		}
	}

	/**
	 * Handles HTTP requests to login - verify credentials
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DBConnection connection = DBConnectionFactory.getConnection();
		try {
			JSONObject input = RpcHelper.readJSONObject(request);
			String userId = input.getString("user_id");
			String password = input.getString("password");
			JSONObject obj = new JSONObject();
			if (connection.verifyLogin(userId, password)) {
				HttpSession session = request.getSession();
				session.setAttribute("user_id", userId);
				session.setAttribute("name", connection.getFullname(userId));
				session.setMaxInactiveInterval(600);
				obj.put("status", "OK").put("user_id", userId).put("name", connection.getFullname(userId));
				
			} else {
				response.setStatus(401);
				obj.put("status", "User Doesn't Exist");
			}	
			RpcHelper.writeJsonObject(response, obj);
			System.out.println(obj);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			connection.close();
		}
	}

}
