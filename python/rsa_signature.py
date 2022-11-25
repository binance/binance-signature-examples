from base64 import b64encode
from Crypto.PublicKey import RSA
from Crypto.Hash import SHA256
from Crypto.Signature import pkcs1_15

query_string = "timestamp=1578963600000"
private_key = "./private_key.pem"
encrypted_private_key = "./encrypted_private_key.pem"

def rsa_hashing(private_key, payload, passphrase=None):
    # load the key
    with open (private_key, "r") as private_key_file:
        private_key = RSA.importKey(private_key_file.read(), passphrase=passphrase)
    h = SHA256.new(payload.encode("utf-8"))
    signature = pkcs1_15.new(private_key).sign(h)
    return b64encode(signature).decode("utf-8")

print("RSA sign with the string: ")
print(query_string)
print("and return:")
print(rsa_hashing(private_key, query_string))
print("\n")

another_string = "symbol=LTCBTC&side=BUY&type=LIMIT&timeInForce=GTC&quantity=1&price=0.1&recvWindow=5000&timestamp=1499827319559"

print("RSA sign with the string: ")
print(another_string)
print("and return:")
print(rsa_hashing(private_key, another_string))
print("\n")

# Sign with encrypted RSA key
another_string = "symbol=LTCBTC&side=BUY&type=LIMIT&timeInForce=GTC&quantity=1&price=0.1&recvWindow=5000&timestamp=1499827319559"

print("RSA sign with the string: ")
print(another_string)
print("and return:")
print(rsa_hashing(encrypted_private_key, another_string, 'password'))
