import java.net.HttpURLConnection;
import java.net.URL;
import java.io.*;
import java.util.*;
import java.time.Instant;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Spot {
	private static final String BASE_URL = "https://api.binance.com";
	private static final String API_KEY = System.getenv("BINANCE_API_KEY");
	private static final String API_SECRET = System.getenv("BINANCE_API_SECRET");
	private static HashMap<String,String> parameters = new HashMap<String,String>();
	private static final String HMAC_SHA256 = "HmacSHA256";

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

	//concatenate query parameters
	private String joinQueryParameters() {
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

	private String getTimeStamp() {
		long timestamp = System.currentTimeMillis();
		return "timestamp=" + String.valueOf(timestamp);
	}

	//convert byte array to hex string
	private String bytesToHex(byte[] bytes) {   
        final char[] hexArray = "0123456789abcdef".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0, v; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

	private String getSignature(byte[] data, byte[] key) {
		byte[] hmacSha256 = null;
		try {
			SecretKeySpec secretKeySpec = new SecretKeySpec(key, HMAC_SHA256);
		    Mac mac = Mac.getInstance(HMAC_SHA256);
		    mac.init(secretKeySpec);
		    hmacSha256 = mac.doFinal(data);
		} catch (Exception e) {
	      	throw new RuntimeException("Failed to calculate hmac-sha256", e);
	    }
	    return bytesToHex(hmacSha256);
	}

	private void executeHTTPRequest(URL obj, String httpMethod) throws Exception {
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		if (httpMethod != null) {
			con.setRequestMethod(httpMethod);
		}
		//add API_KEY to header content
		con.setRequestProperty("X-MBX-APIKEY", API_KEY);

		int responseCode = con.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) { // success
			printResponse(con);
		} else {
			printError(con);
		}

	}

	private void sendPublicRequest(String urlPath) throws Exception {
		String queryPath = joinQueryParameters();
		URL obj = new URL(BASE_URL + urlPath + "?" + queryPath);
		System.out.println("url:" + obj.toString());

		executeHTTPRequest(obj, null);		
	}

	private void sendSignedRequest(String httpMethod, String urlPath) throws Exception {
		String queryPath = "";
		if (!parameters.isEmpty()) {
			queryPath += joinQueryParameters() + "&" + getTimeStamp();
		} else {
			queryPath += getTimeStamp();
		}

		String signature = getSignature(queryPath.getBytes(), API_SECRET.getBytes());
		queryPath += "&signature=" + signature;

		URL obj = new URL(BASE_URL + urlPath + "?" + queryPath);
		System.out.println("url:" + obj.toString());

		executeHTTPRequest(obj, httpMethod);
		

	}

    public static void main(String args[]) throws Exception {
    	Spot spot = new Spot();

    	/*
		### public data endpoint, call send_public_request #####
		get klines
		*/
    	parameters.put("limit","10");
    	parameters.put("interval","1d");
    	parameters.put("symbol","BTCUSDT");
    	spot.sendPublicRequest("/api/v3/klines");
    	parameters.clear();

    	/*
		get account informtion
		if you can see the account details, then the API key/secret is correct
		*/
    	spot.sendSignedRequest("GET", "/api/v3/account");

    	/*
		place an order
		if you see order response, then the parameters setting is correct
		*/
    	parameters.put("symbol", "BNBUSDT");
    	parameters.put("side", "BUY");
    	parameters.put("type", "LIMIT");
    	parameters.put("timeInForce", "GTC");
    	parameters.put("quantity", "1");
    	parameters.put("price", "50");
    	spot.sendSignedRequest("POST", "/api/v3/order");
    	parameters.clear();

    	/*
		transfer funds
		*/
    	parameters.put("fromEmail", "");
    	parameters.put("toEmail", "");
    	parameters.put("asset", "USDT");
    	parameters.put("amount", "0.1");
    	spot.sendSignedRequest("POST", "/sapi/v1/asset/transfer");
    	parameters.clear();

    	/*
		New Future Account Transfer (FUTURES)
		*/
    	parameters.put("asset", "USDT");
    	parameters.put("amount", "0.01");
    	parameters.put("type", "2");
    	spot.sendSignedRequest("POST", "/sapi/v1/futures/transfer");
    	parameters.clear();

    }
}