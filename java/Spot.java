import java.net.HttpURLConnection;
import java.net.URL;
import java.io.*;
import java.util.*;
import java.time.Instant;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Spot {
	private static final String API_KEY = System.getenv("BINANCE_API_KEY");
	private static final String API_SECRET = System.getenv("BINANCE_API_SECRET");
	HashMap<String,String> parameters = new HashMap<String,String>();
	Request httpRequest;

	public Spot() {
		String baseUrl = "https://api.binance.com";
		httpRequest = new Request(baseUrl, API_KEY, API_SECRET);
	}
	
	/*
	Allow changing of base url to connect to testnet environment
	*/
	public Spot(String baseUrl) {
		httpRequest = new Request(baseUrl, API_KEY, API_SECRET);
	}

	public void klines(String symbol, String interval, String limit) throws Exception {
		parameters.put("symbol",symbol);
    	parameters.put("interval",interval);
    	parameters.put("limit",limit);
    	httpRequest.sendPublicRequest(parameters, "/api/v3/klines");
    	parameters.clear();

	}

	public void account() throws Exception {
		httpRequest.sendSignedRequest(parameters, "/api/v3/account", "GET");
	}

	public void order(String symbol, String side, String type, String timeInForce, String quantity, String price) throws Exception {
		parameters.put("symbol", symbol);
    	parameters.put("side", side);
    	parameters.put("type", type);
    	parameters.put("timeInForce", timeInForce);
    	parameters.put("quantity", quantity);
    	parameters.put("price", price);
    	httpRequest.sendSignedRequest(parameters, "/api/v3/order", "POST");
    	parameters.clear();
	}

	public void assetTransfer(String type, String asset, String amount) throws Exception {
		parameters.put("type", type);
    	parameters.put("asset", asset);
    	parameters.put("amount", amount);
    	httpRequest.sendSignedRequest(parameters, "/sapi/v1/asset/transfer", "POST");
    	parameters.clear();
	}

	public void futuresTransfer(String asset, String amount, String type) throws Exception {
		parameters.put("asset", asset);
    	parameters.put("amount", amount);
    	parameters.put("type", type);
    	httpRequest.sendSignedRequest(parameters, "/sapi/v1/futures/transfer", "POST");
    	parameters.clear();
	}
}