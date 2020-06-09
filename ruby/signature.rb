require 'openssl'

query_string = 'timestamp=1578963600000'
secret = 'NhqPtmdSJYdKjVHjA7PZj4Mge3R5YNiP1e3UZjInClVN65XAbvqqM6A7H5fATj0j'

def signature(query_string, secret)
    OpenSSL::HMAC.hexdigest(OpenSSL::Digest.new('sha256'), secret, query_string)
end

print("hashing the string: \n")
print(query_string)
print("\nand return:\n")
print(signature(query_string, secret))
print("\n\n")

query_string = 'symbol=LTCBTC&side=BUY&type=LIMIT&timeInForce=GTC&quantity=1&price=0.1&recvWindow=5000&timestamp=1499827319559'

print("hashing the string: \n")
print(query_string)
print("\nand return:\n")
print(signature(query_string, secret))
print("\n")
