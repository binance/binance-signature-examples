<?php

$KEY= '';
$SECRET = '';

// $BASE_URL = 'https://api.binance.com/'; // production
$BASE_URL = 'https://testnet.binance.vision/'; // testnet

function signature($query_string, $secret) {
    return hash_hmac('sha256', $query_string, $secret);
}

function sendRequest($method, $path) {
  global $KEY;
  global $BASE_URL;
  
  $url = "${BASE_URL}${path}";

  echo "requested URL: ". PHP_EOL;
  echo $url. PHP_EOL;
  $ch = curl_init();
  curl_setopt($ch, CURLOPT_HTTPHEADER, array('X-MBX-APIKEY:'.$KEY));    
  curl_setopt($ch, CURLOPT_FOLLOWLOCATION, 1);
  curl_setopt($ch, CURLOPT_URL, $url);
  curl_setopt($ch, CURLOPT_POST, $method == "POST" ? true : false);
  curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
  $execResult = curl_exec($ch);
  $response = curl_getinfo($ch);
    
  // if you wish to print the response headers
  // echo print_r($response);

  curl_close ($ch);
  return json_decode($execResult, true);
}

function signedRequest($method, $path, $parameters = []) {
  global $SECRET;

  $parameters['timestamp'] = round(microtime(true) * 1000);
  $query = buildQuery($parameters);
  $signature = signature($query, $SECRET);
  return sendRequest($method, "${path}?${query}&signature=${signature}");
}

function buildQuery(array $params)
{
    $query_array = array();
    foreach ($params as $key => $value) {
        if (is_array($value)) {
            $query_array = array_merge($query_array, array_map(function ($v) use ($key) {
                return urlencode($key) . '=' . urlencode($v);
            }, $value));
        } else {
            $query_array[] = urlencode($key) . '=' . urlencode($value);
        }
    }
    return implode('&', $query_array);
}

// get orderbook
$response = sendRequest('GET', 'api/v3/depth?symbol=BNBUSDT&limit=5');
echo json_encode($response);

// get account information, make sure API key and secret are set
$response = signedRequest('GET', 'api/v3/account');
echo json_encode($response);

// place order, make sure API key and secret are set, recommend to test on testnet.
$response = signedRequest('POST', 'api/v3/order', [
  'symbol' => 'BNBUSDT',
  'side' => 'BUY', 
  'type' => 'LIMIT',
  'timeInForce' => 'GTC',
  'quantity' => 1,
  'price' => 15,
  // 'newClientOrderId' => 'my_order', // optional
  'newOrderRespType' => 'FULL' //optional
]);
echo json_encode($response);
