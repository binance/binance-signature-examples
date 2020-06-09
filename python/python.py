import hmac
import hashlib


def hashing(query_string):
    return hmac.new(secret.encode('utf-8'), query_string.encode('utf-8'), hashlib.sha256).hexdigest()

query_string = 'timestamp=1578963600000'
secret = 'NhqPtmdSJYdKjVHjA7PZj4Mge3R5YNiP1e3UZjInClVN65XAbvqqM6A7H5fATj0j'

signature = hashing(query_string)

print("hashing the string: ")
print(query_string)
print("and return:")
print(signature)
print("\n")

another_string = 'asset=ETH&address=0x6915f16f8791d0a1cc2bf47c13a6b2a92000504b&amount=1&recvWindow=5000&name=test&timestamp=1510903211000'
signature = hashing(another_string)

print("hashing the string: ")
print(another_string)
print("and return:")
print(signature)
print("\n")

another_string = 'symbol=LTCBTC&side=BUY&type=LIMIT&timeInForce=GTC&quantity=1&price=0.1&recvWindow=5000&timestamp=1499827319559'
signature = hashing(another_string)

print("hashing the string: ")
print(another_string)
print("and return:")
print(signature)
