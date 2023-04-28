import base64
import requests
import time
from cryptography.hazmat.primitives.serialization import load_pem_private_key

""" This is a very simple script working on Binance API which demonstrates how to use an Ed25519 Keypair to sign the payload.
Ensure that the Public Ed25519 key is registered as an API Key pair on Binance.

Provide the API key and the path to the Ed25519 private key file.

```python

python ed25519_signature.py

```

"""

# Set up authentication
API_KEY='YOUR_API_KEY'
PRIVATE_KEY_PATH='./private_key.pem'

# Load the private key
# Generally we recommend using a strong password for improved security.
with open(PRIVATE_KEY_PATH, 'rb') as f:
    private_key = load_pem_private_key(data=f.read(),
                                       password=None)

# Set up the request parameters
params = {
    'symbol':       'BNBUSDT',
    'side':         'SELL',
    'type':         'LIMIT',
    'timeInForce':  'GTC',
    'quantity':     '1.0000000',
    'price':        '300.20',
}

# Timestamp the request
timestamp = int(time.time() * 1000)
params['timestamp'] = timestamp

# Sign the request
payload = '&'.join([f'{param}={value}' for param, value in params.items()])
signature = base64.b64encode(private_key.sign(payload.encode('ASCII')))
params['signature'] = signature

# Send the request
headers = {
    'X-MBX-APIKEY': API_KEY,
}
response = requests.post(
    'https://testnet.binance.vision/api/v3/order',
    headers=headers,
    data=params,
)
print(response.json())