import java.net.HttpURLConnection;
import java.net.URL;
import java.io.*;
import java.util.*;

public class Request {

	String baseUrl;
	String apiKey;
	String apiSecret;
	Signature sign = new Signature();

	public Request(String baseUrl, String apiKey, String apiSecret) {
		this.baseUrl = baseUrl;
		this.apiKey = apiKey;
		this.apiSecret = apiSecret;
	}

	private void printResponse(HttpURLConnection con) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		System.out.println(response.toString());
	}

	private void printError(HttpURLConnection con) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getErrorStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		System.out.println(response.toString());
	}

	private String getTimeStamp() {
		long timestamp = System.currentTimeMillis();
		return "timestamp=" + String.valueOf(timestamp);
	}

	//concatenate query parameters
	private String joinQueryParameters(HashMap<String,String> parameters) {
		String urlPath = "";
		boolean isFirst = true;

		for (Map.Entry mapElement : parameters.entrySet()) {
			if (isFirst) {
				isFirst = false;
				urlPath += (String)mapElement.getKey() + "=" + (String)mapElement.getValue();
			} else {
				urlPath += "&" + (String)mapElement.getKey() + "=" + (String)mapElement.getValue();
			}
		}
		return urlPath;
	}

	private void send(URL obj, String httpMethod) throws Exception {
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		if (httpMethod != null) {
			con.setRequestMethod(httpMethod);
		}
		//add API_KEY to header content
		con.setRequestProperty("X-MBX-APIKEY", apiKey);

		int responseCode = con.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) { // success
			printResponse(con);
		} else {
			printError(con);
		}

	}

	public void sendPublicRequest(HashMap<String,String> parameters, String urlPath) throws Exception {
		String queryPath = joinQueryParameters(parameters);
		URL obj = new URL(baseUrl + urlPath + "?" + queryPath);
		System.out.println("url:" + obj.toString());

		send(obj, null);		
	}

	public void sendSignedRequest(HashMap<String,String> parameters, String urlPath, String httpMethod) throws Exception {
		String queryPath = "";
		String signature = "";
		if (!parameters.isEmpty()) {
			queryPath += joinQueryParameters(parameters) + "&" + getTimeStamp();
		} else {
			queryPath += getTimeStamp();
		}
		try {
			signature = sign.getSignature(queryPath, apiSecret);
		}
		catch (Exception e) {
			System.out.println("Please Ensure Your Secret Key Is Set Up Correctly! " + e);
			System.exit(0);
		}
		queryPath += "&signature=" + signature;

		URL obj = new URL(baseUrl + urlPath + "?" + queryPath);
		System.out.println("url:" + obj.toString());

		send(obj, httpMethod);
	}
}