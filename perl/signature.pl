#!/usr/bin/perl
use v5.010;
use warnings;
use strict;
use Digest::SHA qw(hmac_sha256_hex); 
my $secretKey = "NhqPtmdSJYdKjVHjA7PZj4Mge3R5YNiP1e3UZjInClVN65XAbvqqM6A7H5fATj0j";
my $exampleQuery1 = "timestamp=1578963600000";
say "example query string 1: ", $exampleQuery1;
say hmac_sha256_hex($exampleQuery1, $secretKey);

my $exampleQuery2 = "symbol=LTCBTC&side=BUY&type=LIMIT&timeInForce=GTC&quantity=1&price=0.1&recvWindow=5000&timestamp=1499827319559";
say "example query string 2: ", $exampleQuery2;
say hmac_sha256_hex($exampleQuery2, $secretKey);
