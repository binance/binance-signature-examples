import java.net.HttpURLConnection;
import java.net.URL;
import java.io.*;
import java.util.*;

public class Request {

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

	public void send(URL obj, String httpMethod, String apiKey) throws Exception {
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
}