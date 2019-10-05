package external;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import entity.Item;
import entity.Item.ItemBuilder;

/**
 * Utilizes API provided by TicketMaster 
 * @author tianqix
 *
 */
public class TicketMasterAPI {
	private static final String URL = "https://app.ticketmaster.com/discovery/v2/events.json";
	private static final String DEFAULT_KEYWORD = ""; // no preference by default
	private static final String API_KEY = "fGGgzL0OF1u7GsMSwZAJfiIRVGu9YGe5";
	
	/**
	 * This class provides methods to issue queries through TicketMaster API and fetch events 
	 * based on location and keyword, and methods to fetch specific fields we will need
	 * @param lat	Latitude of the center of searching area
	 * @param lon 	Longitude of the center of searching area
	 * @param keyword	Keyword for search
	 * @return A list of items boiled down from the response body
	 */
	public List<Item> search(double lat, double lon, String keyword) {
		if (keyword == null) {
			keyword = DEFAULT_KEYWORD;
		}
		
		try {
			keyword = URLEncoder.encode(keyword, "UTF-8"); // In case there are spaces in the keyword -> "%20"
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		// "apikey=qqPuP6n3ivMUoT9fPgLepkRMreBcbrjV&latlong=37,-120&keyword=event&radius=50"

		String query = String.format("apikey=%s&latlong=%s,%s&keyword=%s&radius=%s", API_KEY, lat, lon, keyword, 50);
		String url = URL + "?" + query;
//		url += "&size=1";
		
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setRequestMethod("GET");
			
			int responseCode = connection.getResponseCode();
			System.out.println("Sending request to url: " + url);
			System.out.println("Response code: " + responseCode);
			
			if (responseCode != 200) {
				return new ArrayList();
			}
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			StringBuilder response = new StringBuilder();
			
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
			reader.close();
			JSONObject obj = new JSONObject(response.toString());
			
			if (!obj.isNull("_embedded")) {
				JSONObject embedded = obj.getJSONObject("_embedded");
				return getItemList(embedded.getJSONArray("events"));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ArrayList();
		
	}
	
	/**
	 * Converts JSONArray to a list of Item objects
	 * @param events
	 * @return
	 * @throws JSONException
	 */
	private List<Item> getItemList(JSONArray events) throws JSONException {
		List<Item> itemList = new ArrayList<>();
		for (int i = 0; i < events.length(); ++i) {
			JSONObject event = events.getJSONObject(i);
			
			Item.ItemBuilder builder = new ItemBuilder();
			if (!event.isNull("id")) {
				builder.setItemId(event.getString("id"));
			}
			if (!event.isNull("name")) {
				builder.setName(event.getString("name"));
			}
			if (!event.isNull("url")) {
				builder.setUrl(event.getString("url"));
			}
			if (!event.isNull("distance")) {
				builder.setDistance(event.getDouble("distance"));
			}
			if (!event.isNull("rating")) {
				builder.setRating(event.getDouble("rating"));
			}
			
			builder.setAddress(getAddress(event));
			builder.setCategories(getCategories(event));
			builder.setImageUrl(getImageURL(event));
			
			itemList.add(builder.build());
		}

		return itemList;
	}
	
	/**
	 * Fetches the first address information found in event JSONObject (an event could be held at multiple places)
	 * @param event
	 * @return	event address
	 * @throws JSONException
	 */
	public String getAddress(JSONObject event) throws JSONException {
		if (!event.isNull("_embedded")) {
			JSONObject embedded = event.getJSONObject("_embedded");
			if (!embedded.isNull("venues")) {
				// There could be multiple venues (thus stored in a array)
				JSONArray venues = embedded.getJSONArray("venues");
				
				/* Iterate and return the first venue with non-empty address*/
				for (int i = 0; i < venues.length() ; i++) {
					JSONObject venue = venues.getJSONObject(i);
					StringBuilder addrBuilder = new StringBuilder();
					
					// Get street address
					if (!venue.isNull("address")) {
						JSONObject address = venue.getJSONObject("address");
						/* Get address line by line */
						if (!address.isNull("line1")) {
							addrBuilder.append(address.getString("line1"));
						}
						if (!address.isNull("line2")) {
							addrBuilder.append(",");
							addrBuilder.append(address.getString("line2"));
						}
						if (!address.isNull("line3")) {
							addrBuilder.append(",");
							addrBuilder.append(address.getString("line3"));
						}
					}
					// Get city
					if (!venue.isNull("city")) {
						JSONObject city = venue.getJSONObject("city");
						if (!city.isNull("name")) {
							addrBuilder.append(",");
							addrBuilder.append(city.getString("name"));
							
						}
					}
					
					// Assemble the address and return (if not empty) -> will return the first non-empty address
					String addressStr = addrBuilder.toString();
					if (!addressStr.equals("")) {
						return addressStr;
					}
				}
			}
		}
		
		return "";	// No venue is availabe if this line is reached
	}
	
	/**
	 * Fetches place information from an even JSONObject. 
	 * Place has the information on where the event happens. It can be set if there is no venue.
	 * @param event
	 * @return
	 * @throws JSONException
	 */
	/* Get place (if there's no venue) */
	public String getPlace(JSONObject event) throws JSONException {
		if (!event.isNull("place")) {
			JSONObject place = event.getJSONObject("place");
			StringBuilder addrBuilder = new StringBuilder();
			
			// Get street address
			if (!place.isNull("address")) {
				JSONObject address = new JSONObject();
				/* Get address line by line */
				if (!address.isNull("line1")) {
					addrBuilder.append(address.getString("line1"));
				}
				if (!address.isNull("line2")) {
					addrBuilder.append(",");
					addrBuilder.append(address.getString("line2"));
				}
				if (!address.isNull("line3")) {
					addrBuilder.append(",");
					addrBuilder.append(address.getString("line3"));
				}						
			}
			
			// Get city
			if (!place.isNull("city")) {
				JSONObject city = place.getJSONObject("city");
				if (!city.isNull("name")) {
					addrBuilder.append(",");
					addrBuilder.append(city.getString("name"));
					
				}
			}
			
			// Assemble the address and return
			return addrBuilder.toString();
		}
		return "";
	}
	
	/**
	 * Fetches image URL from an event JSONObject
	 * @param event
	 * @return
	 * @throws JSONException
	 */
	public String getImageURL(JSONObject event) throws JSONException {
		if (!event.isNull("images")) {
			// Could be multiple images, stored as array (defined in API doc)
			JSONArray images = event.getJSONArray("images");
			for (int i = 0; i < images.length(); i++) {
				JSONObject image = images.getJSONObject(i);
				if (!image.isNull("url")) {
					String url = image.getString("url");
					if (!url.equals("")) {
						return url;
					}
				}
			}
		}
		
		return ""; // No image URL is available if this line is reached
	}
	
	/**
	 * Fetches category information from an event JSONObject
	 * @param event
	 * @return a set of categories
	 * @throws JSONException
	 */
	public Set<String> getCategories(JSONObject event) throws JSONException {
		Set<String> categories = new HashSet<>();
		if (!event.isNull("classifications")) {
			JSONArray classifications = event.getJSONArray("classifications");
			for (int i = 0; i < classifications.length(); i++) {
				JSONObject classification = classifications.getJSONObject(i);
				if (!classification.isNull("segment")) {
					JSONObject segment = classification.getJSONObject("segment");
					if (!segment.isNull("name")) {
						String name = segment.getString("name");
						if (!name.equals("")) {
							categories.add(name);
						}
					}
					
				}
			}
		}
		return categories;
	}

	
	/**
	 * This method is for testing use
	 * @param lat
	 * @param lon
	 */
	private void queryAPI(double lat, double lon) {
		List<Item> events = search(lat, lon, null);

		for (Item event : events) {
			System.out.print(event.toJSONObject());
		}
	}
	
	/**
	 * Main entry for TicketMaster API requests.
	 */
	public static void main(String[] args) {
		TicketMasterAPI tmApi = new TicketMasterAPI();
		// Mountain View, CA
		// tmApi.queryAPI(37.38, -122.08);
		// London, UK
		// tmApi.queryAPI(51.503364, -0.12);
		// Houston, TX
		tmApi.queryAPI(29.682684, -95.295410);
	}



}

