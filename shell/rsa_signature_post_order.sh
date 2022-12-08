#!/usr/bin/env bash

# Set up authentication with your API key:
apiKey=""

# Replace with your (RSA PKCS#8 and PEM format) private key path:
privKeyPath="/path/to/privateKey"

# Set up the request:
baseUrl="https://testnet.binance.vision"
apiMethod="POST"
apiCall="api/v3/order"
apiParams="symbol=BTCUSDT&side=SELL&type=LIMIT&timeInForce=GTC&quantity=1&price=0.2&recvWindow=5000"


function rawurlencode {
    local value="$1"
    local len=${#value}
    local encoded=""
    local pos c o
    for (( pos=0 ; pos<len ; pos++ ))
    do
        c=${value:$pos:1}
        case "$c" in
            [-_.~a-zA-Z0-9] ) o="${c}" ;;
            * )   printf -v o '%%%02x' "'$c"
        esac
        encoded+="$o"
    done
    echo "$encoded"
}

ts=$(date +%s000)
paramsWithTs="$apiParams&timestamp=$ts"

rawSignature=$(echo -n "$paramsWithTs" \
               | openssl dgst -keyform PEM -sha256 -sign $privKeyPath \
               | openssl enc -base64 \
               | tr -d '\n')
signature=$(rawurlencode "$rawSignature")

curl -H "X-MBX-APIKEY: $apiKey" -X $apiMethod \
    "$baseUrl/$apiCall?$paramsWithTs&signature=$signature"
