<?php

$query_string = 'timestamp=1578963600000';
$secret = 'NhqPtmdSJYdKjVHjA7PZj4Mge3R5YNiP1e3UZjInClVN65XAbvqqM6A7H5fATj0j';

function signature($query_string, $secret) {
    return hash_hmac('sha256', $query_string, $secret);
}
echo "hashing the string:".PHP_EOL;
echo $query_string.PHP_EOL;
echo "and return:".PHP_EOL;
echo signature($query_string, $secret).PHP_EOL.PHP_EOL;

$another_string = 'symbol=LTCBTC&side=BUY&type=LIMIT&timeInForce=GTC&quantity=1&price=0.1&recvWindow=5000&timestamp=1499827319559';
echo "hashing the string:".PHP_EOL;
echo $another_string.PHP_EOL;
echo "and return:".PHP_EOL;
echo signature($another_string, $secret);
