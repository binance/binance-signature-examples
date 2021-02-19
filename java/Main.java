

public class Main {
	public static void main(String args[]) throws Exception {
    	Spot spot = new Spot();
    	/*
		### public data endpoint, call send_public_request #####
		get klines
		*/

    	spot.klines("BTCUSDT", "1d", "10");

    	/*
		get account informtion
		if you can see the account details, then the API key/secret is correct
		*/

    	spot.account();

     	/*
		place an order
		if you see order response, then the parameters setting is correct
		*/

    	spot.order("BNBUSDT", "BUY", "LIMIT", "GTC", "1", "100");

     	/*
		transfer funds
		*/

    	spot.assetTransfer("MAIN_C2C", "USDT", "0.1");

     	/*
		New Future Account Transfer (FUTURES)
		*/

    	spot.futuresTransfer("USDT", "0.01", "2");

    }
}