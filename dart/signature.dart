
import 'dart:convert';
import 'package:convert/convert.dart';
import 'package:http/http.dart' as http;
import 'package:crypto/crypto.dart';
import 'dart:convert';

class BinanceComunication {

  Future<http.Response> getHttp(url, Map<String, String> queryParameter) {
    queryParameter['timestamp'] = DateTime.now().millisecondsSinceEpoch.toString();
    var uri = Uri.https(baseUrl, url, queryParameter);
    queryParameter['signature'] = createSignature(uri.query);
    uri = Uri.https(baseUrl, url, queryParameter);
    return http.get(uri, headers: {
      "Accept": "application/json",
      'X-MBX-APIKEY': apiKey
    });
  }

  Future<http.Response> postHttp(url, Map<String, String> queryParameter) {
    queryParameter['timestamp'] = DateTime.now().millisecondsSinceEpoch.toString();
    var uri = Uri.https(baseUrl, url, queryParameter);
    queryParameter['signature'] = createSignature(uri.query);
    uri = Uri.https(baseUrl, url, queryParameter);
    return http.post(uri, headers: {
      "Accept": "application/json",
      'X-MBX-APIKEY': apiKey
    });
  }

  String createSignature(String queryString){
    List<int> messageBytes = utf8.encode(queryString);
    List<int> key = utf8.encode(privateKey);
    Hmac hmac = new Hmac(sha256, key);
    Digest digest = hmac.convert(messageBytes);
    String signature = hex.encode(digest.bytes);
    return signature;
  }
}