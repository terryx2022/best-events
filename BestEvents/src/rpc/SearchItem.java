package rpc;

// IO
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

// JAVAX
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

// JSON
import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;
import external.TicketMasterAPI;

import org.json.JSONArray;

/**
 * Servlet implementation class SearchItem
 */
@WebServlet("/search")
public class SearchItem extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchItem() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * Handles HTTP requests to search nearby events
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// To enforce the login process allow access only if session exists
		HttpSession session = request.getSession(false);
		if (session == null) {
			response.setStatus(403);
			return;
		}
		String userId = session.getAttribute("user_id").toString(); 
		
		
		double lat = Double.parseDouble(request.getParameter("lat"));
		double lon = Double.parseDouble(request.getParameter("lon"));
		String keyword = request.getParameter("keyword");
		
		DBConnection connection = DBConnectionFactory.getConnection();
          try {
        	  // calls .searchItem() through interface DBConnection, returns the events and saves to database
        	  List<Item> items = connection.searchItems(lat, lon, keyword);
        	  Set<String> favoritedItemIds = connection.getFavoriteItemIds(userId);
        	  // returns events to front-end
        	  JSONArray array = new JSONArray();
        	  for (Item item : items) {
        		  JSONObject obj = item.toJSONObject();
        		  obj.put("favorite", favoritedItemIds.contains(item.getItemId()));
        		  array.put(obj);
        	  }
        	  RpcHelper.writeJsonArray(response, array);
        	  
          } catch (Exception e) {
        	  e.printStackTrace();
          } finally {
        	 connection.close();
          }
	}

}
