package org.json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;


//TAKEN FROM: http://stackoverflow.com/questions/4308554/simplest-way-to-read-json-from-a-url-in-java

/*
 * Essentially takes in a URL and scrapes a JSON from it. Used to parse response from getAttributes call
 */
public class JsonReader {

	private static final Logger log = Logger.getLogger(JsonReader.class.getName());

	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}
	
	// URL in, JSON object out
	public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			JSONObject json = new JSONObject(jsonText);
			return json;
		} finally {
			is.close();
		}
	}

	public static void main(String[] args) throws IOException, JSONException {
		JSONObject json = readJsonFromUrl("https://sre.vip.ebay.com/DependencySvc/load.do?qryType=getpooldependencyJSON&poolName=v3suncore");

		JSONArray test = json.getJSONArray("data");
		HashMap<String, ArrayList<String> > map = new HashMap<String, ArrayList<String> >();
		for (int i = 0; i < test.length(); i++) {
			JSONObject curr = test.getJSONObject(i);
			//System.out.println("Attribute Type: " + attribute.get("attributeType") + ". Value: " + attribute.get("attributeValue"));
		}
//		System.out.println(json.get("attributes"));
	}
}