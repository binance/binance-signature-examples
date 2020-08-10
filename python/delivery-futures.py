import hmac
import time
import hashlib
import requests
import json
from urllib.parse import urlencode, quote

""" This is a very simple script working on Binance API

- work with USER_DATA endpoint with no third party dependency
- work with testnet

Provide the API key and secret, and it's ready to go

Because USER_DATA endpoints require signature:
- call `send_signed_request` for USER_DATA endpoints
- call `send_public_request` for public endpoints

```python

python delivery-futures.py

```

"""

KEY = ''
SECRET = ''
# BASE_URL = 'https://dapi.binance.com' # production base url
BASE_URL = 'https://testnet.binancefuture.com' # testnet base url


''' ======  begin of functions, you don't need to touch ====== '''

def hashing(query_string):
    return hmac.new(SECRET.encode('utf-8'), query_string.encode('utf-8'), hashlib.sha256).hexdigest()

def get_timestamp():
    return int(time.time() * 1000)

def dispatch_request(http_method):
    session = requests.Session()
    session.headers.update({
        'Content-Type': 'application/json;charset=utf-8',
        'X-MBX-APIKEY': KEY
    })
    return {
        'GET': session.get,
        'DELETE': session.delete,
        'PUT': session.put,
        'POST': session.post,
    }.get(http_method, 'GET')


def send_signed_request(http_method, url_path, payload={}):
    query_string = urlencode(payload)
    query_string = query_string.replace('%27', '%22')

    if query_string:
        query_string = "{}&timestamp={}".format(query_string, get_timestamp())
    else:
        query_string = 'timestamp={}'.format(get_timestamp())

    url = BASE_URL + url_path + '?' + query_string + '&signature=' + hashing(query_string)
    print(url)
    params = {'url': url, 'params': {}}
    response = dispatch_request(http_method)(**params)
    return response.json()

# used for sending public data request
def send_public_request(url_path, payload={}):
    query_string = urlencode(payload, True)
    url = BASE_URL + url_path
    if query_string:
        url = url + '?' + query_string
    print("{}".format(url))
    response = dispatch_request('GET')(url=url)
    return response.json()

''' ======  end of functions ====== '''

### public data endpoint, call send_public_request #####
# get klines
response = send_public_request('/dapi/v1/time')
print(response)


### USER_DATA endpoints, call send_signed_request #####
# get acount info
response = send_signed_request('GET', '/dapi/v1/account')
print(response)

# place an order
# if you see order response, then the parameters setting is correct
# if it has response from server saying some parameter error, please adjust the parameters according the market.
params = {
    "symbol": "BTCUSD_200925",
    "side": "BUY",
    "type": "LIMIT",
    "timeInForce": "GTC",
    "quantity": 1,
    "price": "9000"
}

response = send_signed_request('POST', '/dapi/v1/order', params)
print(response)

# create batch orders
# if you see order response, then the parameters setting is correct
# if it has response from server saying some parameter error, please adjust the parameters according the market.
params = {
    "batchOrders": [
        {
            "symbol": "BTCUSD_200925",
            "side": "BUY",
            "type": "LIMIT",
            "timeInForce": "GTC",
            "quantity": "1",
            "price": "9000"
        },
        {
            "symbol": "BTCUSD_200925",
            "side": "BUY",
            "type": "LIMIT",
            "timeInForce": "GTC",
            "quantity": "1",
            "price": "9000"
        }
    ]
}
response = send_signed_request('POST', '/dapi/v1/batchOrders', params)
print(response)
