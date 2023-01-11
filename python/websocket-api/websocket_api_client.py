"""
	This is a demo python script to show how to connect to Binance Spot Websocket API server,
	and how to send most common messages. This example already includes 3 messages:
	- server_time_message
	- account_status_message
	- place_order_message

	You can just call each of message, it should work out of the box.
	It should also very convenient to be modified to call other endpoints.

	Notes:
	- websokcet-client package is required, you can install by: pip install websocket-client
	- API key and secret are required for endpoints that require signature
	- The document: https://github.com/binance/binance-spot-api-docs/blob/master/web-socket-api.md

"""

import websocket
import json
import time
import hashlib
import hmac
from urllib.parse import urlencode

# If you like to run in debug mode
websocket.enableTrace(False)

# TODO add your own API key and secret
apiKey = ""
apiSecret = ""


""" === ultil functions === """
def hashing(query_string):
    return hmac.new(
        apiSecret.encode("utf-8"), query_string.encode("utf-8"), hashlib.sha256
    ).hexdigest()


def get_timestamp():
    return int(time.time() * 1000)

""" === end of util functions === """

def server_time_message():
	"""
		The message to get server time
	"""

	return {
	  "id": "187d3cb2-942d-484c-8271-4e2141bbadb1",
	  "method": "time"
	}


def account_status_message():
	"""
		The message to get user account
	"""

	timestamp = get_timestamp()
	payload = {
		"apiKey": apiKey,
		"timestamp": timestamp
	}

	return {
	  "id": "605a6d20-6588-4cb9-afa0-b0ab087507ba",
	  "method": "account.status",
	  "params": {
	    "apiKey": apiKey,
	    "signature": hashing(urlencode(payload)),
	    "timestamp": timestamp
	  }
	}



def place_order_message():
	timestamp = get_timestamp()
	payload = {
	  "apiKey": apiKey,
	  "newClientOrderId": "websocket_api_test_1",
	  "price": "13.5640",
	  "quantity": 1,
	  "side": "BUY",
	  "symbol": "SOLUSDT",
	  "timeInForce": "GTC",
	  "timestamp": timestamp,
	  "type": "LIMIT"
	}
	signature = hashing(urlencode(payload))
	payload['signature'] = signature

	return {
		"id": "56374a46-3061-486b-a311-99ee972eb648",
		"method": "order.place",
		"params": payload
	}

def on_open(wsapp):
	print("connection open")

	# TODO: choose which message you like to send to server

	# To get the server time
	message_to_server = json.dumps(server_time_message())

	# To get account details, uncomment this line
	# message_to_server = json.dumps(account_status_message())

	# to place an order, uncomment this line
	# message_to_server = json.dumps(place_order_message())


	print("sending message to server:")
	print(message_to_server)
	wsapp.send(message_to_server)

def on_message(wsapp, message):
	print("Receiving message from server:")
	print(message)

def on_error(wsapp, error):
	print(error)

def on_close(wsapp, close_status_code, close_msg):
	print("Connection close")
	print(close_status_code)
	print(close_msg)

def on_ping(wsapp, message):
	print("received ping from server")

def on_pong(wsapp, message):
	print("received pong from server")

if __name__ == "__main__":
	wsapp = websocket.WebSocketApp("wss://ws-api.binance.com/ws-api/v3",
									on_message=on_message,
									on_open=on_open,
									on_error=on_error,
									on_ping=on_ping,
									on_pong=on_pong)
	wsapp.run_forever()
