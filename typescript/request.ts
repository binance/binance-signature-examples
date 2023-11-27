import { importSecret, authAndSign } from "./utils.ts";

const endpoint = "https://fapi.binance.com";

const apiKey = '2b5eb11e18796d12d88f13dc27dbbd02c2cc51ff7059765ed9821957d82bb4d9';
const apiSecret = 'dbefbc809e3e83c283a984c3a1459732ea7db1360ca80c5c2c8867408d28cc83';

const cryptoKey = await importSecret(apiSecret);

// You may want to initiate requests with additional headers (e.g. API key)
const headers = new Headers();
headers.set('X-MBX-APIKEY', apiKey);

// If you'd like to seperate payload across query string and POST body:

// For query string:
const q = new URLSearchParams({
  symbol: 'BTCUSDT',
  side: 'BUY',
  type: 'MARKET',
});

// For request body:
const s = new URLSearchParams({
  quantity: '1',
  price: '9000',
  recvWindow: '5000',
});

// Set `Content-Encoding` in the headers:
headers.set('Content-Encoding', 'application/x-www-form-urlencoded');

const signedRequest = await authAndSign(  // See `authAndSign` definition in "utils.ts"
  'POST',
  new URL(`/fapi/v1/order?${q}`, endpoint),  // composing the URL with query string
  headers,  // the headers
  s.toString(),  // the request body
  apiKey,
  cryptoKey,
);

console.log(`Initial URL: ${url.toString()}`);
console.log(`Body: ${body}`);
console.log(`Final URL: ${signedRequest.url}`);

// const response = fetch(signedRequest);  // use the signed request in time, before it expired

// console.log(response);
