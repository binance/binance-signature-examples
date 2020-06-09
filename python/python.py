import hmac
import hashlib

query_string = 'timestamp=1578963600000'
secret = 'NhqPtmdSJYdKjVHjA7PZj4Mge3R5YNiP1e3UZjInClVN65XAbvqqM6A7H5fATj0j'

signature = hmac.new(secret.encode('utf-8'), query_string.encode('utf-8'), hashlib.sha256).hexdigest()

print("hashing the string: ")
print(query_string)
print("and return:")
print(signature)

print("\n")
another_string = 'asset=ETH&address=0x6915f16f8791d0a1cc2bf47c13a6b2a92000504b&amount=1&recvWindow=5000&name=test&timestamp=1510903211000'
signature = hmac.new(secret.encode('utf-8'), another_string.encode('utf-8'), hashlib.sha256).hexdigest()

print("hashing the string: ")
print(another_string)
print("and return:")
print(signature)
