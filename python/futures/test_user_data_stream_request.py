"""
Listens to COIN-M Futures Testnet account's data stream and creates 3 requests to receive position updates.
Feel free to adapt this script for other types of request or other changes more suitable for your use case.

Instructions:
    1. Set up your account's api key as BINANCE_API_KEY environment variable;
    2. python test_user_data_stream.py (or python3)
"""


import websockets
import asyncio
import json
from datetime import datetime
import os
import requests


print("Creating listen key...")
api_key = os.getenv("BINANCE_API_KEY")
base_endpoint = "https://testnet.binancefuture.com"
listen_key_json = requests.post(
    base_endpoint + "/dapi/v1/listenKey", headers={"X-MBX-APIKEY": api_key}
)
listen_key = json.loads(listen_key_json.text)["listenKey"]

print("Preparing user data stream...")
stream_endpoint = f"wss://dstream.binancefuture.com/ws/{listen_key}"
request_name = "@position"
my_id = 123


async def monitor():
    counter = 0
    async with websockets.connect(stream_endpoint) as ws:

        print("User Data Stream connected!")
        while True:
            payload = json.dumps(
                {
                    "method": "REQUEST",
                    "params": [f"{listen_key}{request_name}"],
                    "id": my_id,
                }
            )

            print(datetime.now().strftime("%H:%M:%S"), "Send @position request")
            await ws.send(payload)

            try:
                resp = json.loads(await asyncio.wait_for(ws.recv(), timeout=10))
                if not resp["result"]:
                    print(resp)
                    print(
                        datetime.now().strftime("%H:%M:%S"),
                        f'Received Empty Results with response id={resp["id"]}, correct api key!',
                    )
                elif "id" in resp:
                    print(
                        datetime.now().strftime("%H:%M:%S"),
                        f'Received response with id={resp["id"]}',
                    )
                else:
                    # Not request's response but pushed event update
                    print(datetime.now().strftime("%H:%M:%S"), f"Received Event update")
                    pass
            except asyncio.TimeoutError:
                print("No response after 10s: timeout!")

            # Wait 3s to send next request
            await asyncio.sleep(3)

            # Finish after sending 3 requests
            counter += 1
            if counter == 3:
                break


if __name__ == "__main__":
    loop = asyncio.get_event_loop()
    asyncio.set_event_loop(loop)
    loop.run_until_complete(monitor())
