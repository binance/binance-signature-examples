"""
Cancels a batch of orders giving origClientOrderIdList and symbol
More details: https://binance-docs.github.io/apidocs/delivery/en/#cancel-all-open-orders-trade
Instructions:
    1. Set up your account's api key as BINANCE_API_KEY environment variable
    2. Set up your account's api secret key as BINANCE_API_SECRET environment variable
    3. python cancel_batch_orders.py (or python3)
"""


from urllib.parse import urlencode, urljoin
import requests
import hmac
import hashlib
import time
import os

# set your API key/secret here
api_key = ""
secret_key = ""

# base_endpoint = 'https://dapi.binance.com' # production base url
base_url = "https://testnet.binancefuture.com"  # testnet base url
symbol = "BTCUSD_PERP"


def _signature(params):
    params_query_string = urlencode(params)
    return hmac.new(
        secret_key.encode("utf-8"), params_query_string.encode("utf-8"), hashlib.sha256
    ).hexdigest()


def _delete(url, params):
    headers = {"X-MBX-APIKEY": api_key}
    params_encoded = urlencode(params)
    response = requests.delete(url, headers=headers, params=params_encoded)
    print(response.json())


def run():
    """
    Sends a batch of ClientOrderIds to cancel those orders.
    Expected to receive the below as there's no ClientOrderId with "my_id_1" and "my_id_2"
    [{'code': -2011, 'msg': 'Unknown order sent.'}, {'code': -2011, 'msg': 'Unknown order sent.'}]
    """
    params = {
        "symbol": symbol,
        "origClientOrderIdList": '["my_id_1","my_id_2"]',  # max length 10
        "timestamp": time.time_ns() // 1000000,  # milliseconds
    }
    params["signature"] = _signature(params)
    url = urljoin(base_url, "/dapi/v1/batchOrders")
    return _delete(url, params)


run()
