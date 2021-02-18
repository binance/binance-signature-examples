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
	private Signature sign = new Signature();
	private Request httpRequest = new Request();

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

	private void sendPublicRequest(String urlPath) throws Exception {
		String queryPath = joinQueryParameters();
		URL obj = new URL(BASE_URL + urlPath + "?" + queryPath);
		System.out.println("url:" + obj.toString());

		httpRequest.send(obj, null, API_KEY);		
	}

	private void sendSignedRequest(String httpMethod, String urlPath) throws Exception {
		String queryPath = "";
		if (!parameters.isEmpty()) {
			queryPath += joinQueryParameters() + "&" + getTimeStamp();
		} else {
			queryPath += getTimeStamp();
		}
		String signature = sign.getSignature(queryPath, API_SECRET);
		queryPath += "&signature=" + signature;

		URL obj = new URL(BASE_URL + urlPath + "?" + queryPath);
		System.out.println("url:" + obj.toString());

		httpRequest.send(obj, httpMethod, API_KEY);
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