package rpc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * This is a utility class to handle all rpc parsing operations for IO purpose, 
 * so that duplicate code is avoided in other places.
 * @author tianqix
 *
 */
public class RpcHelper {
	/**
	 * Writes a JSON array to HTTP response
	 * @param response
	 * @param array
	 * @throws IOException
	 */
	public static void writeJsonArray(HttpServletResponse response, JSONArray array) throws IOException{
		response.setContentType("application/json");
		response.setHeader("Access-Control-Allow-Origin", "*");
		PrintWriter out = response.getWriter();	
		out.print(array);
		out.close();
	}
	
	/**
	 * Writes a JSON object to HTTP response
	 * @param response
	 * @param obj
	 * @throws IOException
	 */
	public static void writeJsonObject(HttpServletResponse response, JSONObject obj) throws IOException {		
		response.setContentType("application/json");
		response.setHeader("Access-Control-Allow-Origin", "*");
		PrintWriter out = response.getWriter();	
		out.print(obj);
		out.close();
	}
	
	/**
	 * Parses a JSONObject from a HTTP request
	 * @param request
	 * @return
	 */
	public static JSONObject readJSONObject(HttpServletRequest request) {
		StringBuilder sb = new StringBuilder();
	  	try (BufferedReader reader = request.getReader()) {
	  		String line = null;
	  		while((line = reader.readLine()) != null) {
	  			sb.append(line);
	  		}
	  		return new JSONObject(sb.toString());
	  		
	  	 } catch (Exception e) {
	  	  		e.printStackTrace();
	  	 }
	  	return new JSONObject();
	}



	
}
