package main

import (
	"crypto/hmac"
	"crypto/sha256"
	"fmt"
)

func main() {
	query_string := "timestamp=1578963600000"
	secret := "NhqPtmdSJYdKjVHjA7PZj4Mge3R5YNiP1e3UZjInClVN65XAbvqqM6A7H5fATj0j"

	fmt.Println("hashing the string:")
	fmt.Println(query_string)
	fmt.Println("and return:")
	fmt.Println(signature(query_string, secret))
	fmt.Println("")
	query_string = "symbol=LTCBTC&side=BUY&type=LIMIT&timeInForce=GTC&quantity=1&price=0.1&recvWindow=5000&timestamp=1499827319559"

	fmt.Println("hashing the string: ")
	fmt.Println(query_string)
	fmt.Println("and return:")
	fmt.Println(signature(query_string, secret))
	fmt.Println("")

}

func signature(message, secret string) string {
	mac := hmac.New(sha256.New, []byte(secret))
	mac.Write([]byte(message))
	signingKey := fmt.Sprintf("%x", mac.Sum(nil))
	return signingKey

}
