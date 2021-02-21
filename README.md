# Binance API signature examples

[Binance API document](https://binance-docs.github.io/apidocs/spot/en/#signed-trade-user_data-and-margin-endpoint-security) has the details of how to hash the signature.
However we are still seeing users spending hours to find out why server still complains about bad signatures. In this repo, we give the example script on how to do signature.

Code in this repository should NOT be used in production.

## How it works
In each language, the script will try to hash following string and should return same signature

```bash
# hashing string
timestamp=1578963600000
# and return
d84e6641b1e328e7b418fff030caed655c266299c9355e36ce801ed14631eed4

# hashing string
symbol=LTCBTC&side=BUY&type=LIMIT&timeInForce=GTC&quantity=1&price=0.1&recvWindow=5000&timestamp=1499827319559
# and return
c8db56825ae71d6d79447849e617115f4a920fa2acdcab2b053c4b2838bd6b71

```

The secret is the same as `NhqPtmdSJYdKjVHjA7PZj4Mge3R5YNiP1e3UZjInClVN65XAbvqqM6A7H5fATj0j`

## Should I look into this repo
- if you don't know what's signature
- if you don't know how to do sha256 hashing in a language
- if your signature can't pass the server's validation

## Which language included
- Python
- C++ 
- Java
- Shell
- Ruby
- NodeJS
- PHP

## License
MIT
