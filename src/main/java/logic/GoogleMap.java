package logic;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

import json.JSONArray;
import json.JSONException;
import json.JSONObject;
import model.LatLng;
import model.Route;


/*
 * TODO:
 * 		- Zusammenfassen und zurückgeben einer Route
 * 		- Berechnung des CO2 Wertes und der Kosten
 * 		- Parsing distance und duration
 * 
 */
public class GoogleMap implements Callable<Route>{

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Start");
		//calcRoute("Innsbruck", "Wien", GoogleMapModi.walking);
	}

	private String str_origin = null;
	private String str_dest = null;
	private LatLng latlng_origin = null;
	private LatLng latlng_dest = null;
	private static Enum mode = null;
	
	public GoogleMap(String origin, String dest, Enum mode){
		this.mode = mode;
		this.str_origin = origin;
		this.str_dest = dest;
	}
	
	public GoogleMap(LatLng origin, LatLng dest, Enum mode){
		this.mode = mode;
		this.latlng_origin = origin;
		this.latlng_dest = dest;
	}

	private static JSONObject calcRoute(String origin, String dest) {
		// Origin of route
		String str_origin = "origin=" + origin;

		// Destination of route
		String str_dest = "destination=" + dest;

		String url = getDirectionsUrlSub(str_origin, str_dest) + "&mode=" +mode.toString();
		System.out.println("URL: " + url);
		
		
		JSONObject jObject = null;
		try {
			jObject = readJsonFromUrl(url);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return jObject;
	}

	private static JSONObject calcRoute(LatLng origin, LatLng dest) {
		// Origin of route
		String str_origin = "origin=" + origin.latitude + ","
				+ origin.longitude;

		// Destination of route
		String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

		String url = getDirectionsUrlSub(str_origin, str_dest)+"&mode=" +mode.toString();

		// Start downloading json data from Google Directions API
		JSONObject jObject = null;
		try {
			jObject = readJsonFromUrl(url);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return jObject;
	}

	private static String getDirectionsUrlSub(String origin, String dest) {

		// Sensor enabled
		String sensor = "sensor=false";

		// Building the parameters to the web service
		String parameters = origin + "&" + dest + "&" + sensor;

		// Output format
		String output = "json";

		// Building the url to the web service
		String url = "https://maps.googleapis.com/maps/api/directions/"
				+ output + "?" + parameters;

		return url;
	}
	

	private static JSONObject readJsonFromUrl(String url) throws IOException, JSONException{
		InputStream is = new URL(url).openStream();
		try{
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			JSONObject json= new JSONObject(jsonText);
			return json;
		} finally {
			is.close();
		}
	}

	private static String readAll(Reader rd) throws IOException{
		StringBuilder sb = new StringBuilder();
		int cp;
		while((cp = rd.read()) != -1){
			sb.append((char) cp);
		}
		return sb.toString();
	} 

	private static ArrayList<ArrayList<LatLng>> getPolyline(JSONObject jObject){
		DirectionsJSONParser parser = new DirectionsJSONParser();
		List<List<HashMap<String, String>>> routes = parser.parse(jObject);
		ArrayList<LatLng> points = null;
		ArrayList<ArrayList<LatLng>> polyline = new ArrayList<ArrayList<LatLng>>();

		for(int i = 0; i < routes.size(); ++i){
			points = new ArrayList<LatLng>();
			List<HashMap<String, String>> path = routes.get(i);
			for(int j = 0; j < path.size(); ++j){
				HashMap<String, String> point = path.get(j);
				double lat = Double.parseDouble(point.get("lat"));
				double lng = Double.parseDouble(point.get("lng"));
				LatLng position = new LatLng(lat, lng);
				points.add(position);
			}
			polyline.add(points);
		}

//		return polyline;
		return null;
	}

	
	private static int getDuration(JSONObject jObject){
		int duration = -1;
		if(jObject != null)
		{
			JSONArray jar = jObject.getJSONArray("routes");
			JSONArray jal = ( (JSONObject)jar.get(0)).getJSONArray("legs");
			duration = (Integer) ((JSONObject) ((JSONObject)jal.get(0)).get("duration")).get("value");
		}
		
		return duration;
	}
	
	private static int getDistance(JSONObject jObject){
		int distance = -1;
		if(jObject != null)
		{
			JSONArray jar = jObject.getJSONArray("routes");
			JSONArray jal = ( (JSONObject)jar.get(0)).getJSONArray("legs");
			distance = (Integer) ((JSONObject) ((JSONObject)jal.get(0)).get("distance")).get("value");
		}
		
		return distance;
	}
	
	@Override
	public Route call() throws Exception {
		// TODO Auto-generated method stub
		Route route = null;
		JSONObject jObject = null;
		
		if(str_origin != null && str_dest != null)
		{
			jObject = calcRoute(str_origin, str_dest);
		}
		else if(latlng_dest != null && latlng_origin != null)
		{
			jObject = calcRoute(latlng_origin, latlng_dest);
		}
		
		int duration = getDuration(jObject);
		int distance = getDistance(jObject);
		
//		int duration = 0;
//		int distance = 0;
		
		ArrayList<ArrayList<LatLng>> polyline = getPolyline(jObject);
		double CO2 = 0.0;
		double costs = 0.0;
		
		
		route = new Route(duration, distance, mode.toString(), polyline, CO2, costs, new Date());
		
		return route;
	}
}
