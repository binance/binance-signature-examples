import { importSecret, signWith } from "./utils.ts"

const apiSecret = 'NhqPtmdSJYdKjVHjA7PZj4Mge3R5YNiP1e3UZjInClVN65XAbvqqM6A7H5fATj0j';

const secretKey = await importSecret(apiSecret);

const query_string = 'timestamp=1578963600000';
console.log("hashing the string: ");
console.log(query_string);
console.log("and return:");
console.log(await signWith(secretKey, query_string));

console.log("\n");

const another_query = 'symbol=LTCBTC&side=BUY&type=LIMIT&timeInForce=GTC&quantity=1&price=0.1&recvWindow=5000&timestamp=1499827319559';
console.log("hashing the string: ");
console.log(another_query);
console.log("and return:");
console.log(await signWith(secretKey, another_query));
