// Written on mac environment
#include <iostream>
#include <curl/curl.h>
#include <string>
#include <chrono>
#include <openssl/hmac.h>
#include <iomanip>
#include <sstream>
#include <unordered_map>

using namespace std;

const string baseUrl = "https://api.binance.com";
/*export api key and secret key to environment*/
const string apiKey = getenv("BINANCE_API_KEY");
const string apiSecret = getenv("BINANCE_API_SECRET");

static string gs_strLastResponse;

string getTimestamp() {
	long long ms_since_epoch = duration_cast<std::chrono::milliseconds>(std::chrono::system_clock::now().time_since_epoch()).count();
	return to_string(ms_since_epoch);
}

string encryptWithHMAC(const char* key, const char* data) {
    unsigned char *result;
    static char res_hexstring[64];
    int result_len = 32;
    string signature;

    result = HMAC(EVP_sha256(), key, strlen((char *)key), const_cast<unsigned char *>(reinterpret_cast<const unsigned char*>(data)), strlen((char *)data), NULL, NULL);
  	for (int i = 0; i < result_len; i++) {
    	sprintf(&(res_hexstring[i * 2]), "%02x", result[i]);
  	}

  	for (int i = 0; i < 64; i++) {
  		signature += res_hexstring[i];
  	}

  	return signature;
}

string getSignature(string query) {
	string signature = encryptWithHMAC(apiSecret.c_str(), query.c_str());
	return "&signature=" + signature;
}

/* concatenate the query parameters to &<key>=<value> */
string joinQueryParameters(const unordered_map<string,string> &parameters) {
	string queryString = "";
	for (auto it = parameters.cbegin(); it != parameters.cend(); ++it) {
			if (it == parameters.cbegin()) {
				queryString += it->first + '=' + it->second;
			}

			else {
				queryString += '&' + it->first + '=' + it->second;
			}
	}

	return queryString;
}

/*Sending the HTTP Request and printing the response*/
void executeHTTPRequest(CURL *curl) {
	CURLcode res;
	gs_strLastResponse = "";
	/* Perform the request, res will get the return code */ 
	res = curl_easy_perform(curl);
	/* Check for errors */ 
	if(res != CURLE_OK)
	  fprintf(stderr, "curl_easy_perform() failed: %s\n",
	          curl_easy_strerror(res));
	cout << gs_strLastResponse << endl;
}

/*Write the server response to string for display*/
size_t WriteCallback(char *contents, size_t size, size_t nmemb, void *userp) {
    gs_strLastResponse += (const char*)contents;
    gs_strLastResponse += '\n';
    return size * nmemb;
}

void sendPublicRequest(CURL *curl, string urlPath, unordered_map<string,string> &parameters) {
	string url = baseUrl + urlPath + '?';
    if (!parameters.empty()) {
    	url += joinQueryParameters(parameters);
    }
    cout << "url:" << url << endl;
    curl_easy_setopt(curl, CURLOPT_URL, url.c_str());
	curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, WriteCallback);
	parameters.clear();
	executeHTTPRequest(curl);
}

void sendSignedRequest(CURL *curl, string httpMethod, string urlPath, unordered_map<string,string> &parameters) {
	string url = baseUrl + urlPath + '?';
	string queryString = "";
	string timeStamp = "timestamp=" + getTimestamp();
	if (!parameters.empty()) {
		string res = joinQueryParameters(parameters) + '&' + timeStamp;
		url += res;
		queryString += res; 
	}

	else {
		url += timeStamp;
		queryString += timeStamp;
	}

	string signature = getSignature(queryString);
	url += signature;
	queryString += signature;
	cout << "url:" << url << endl;

	if (httpMethod == "POST") {
		curl_easy_setopt(curl, CURLOPT_URL, (baseUrl + urlPath).c_str());
		curl_easy_setopt(curl, CURLOPT_POSTFIELDS, queryString.c_str());
		curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, WriteCallback);
		parameters.clear();
		executeHTTPRequest(curl);
	}

	else {
		curl_easy_setopt(curl, CURLOPT_URL, url.c_str());
		curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, WriteCallback);
		executeHTTPRequest(curl);
	}
}

int main() {
	CURL *curl;
	unordered_map<string,string> parameters;
	string queryString;

	curl_global_init(CURL_GLOBAL_ALL);

	/* get a curl handle */ 
	curl = curl_easy_init();
	if(curl) {
		/* Adding API key to header */
		struct curl_slist *chunk = NULL;
		chunk = curl_slist_append(chunk, ("X-MBX-APIKEY:" + apiKey).c_str());
		curl_easy_setopt(curl, CURLOPT_HTTPHEADER, chunk);

		/*
		### public data endpoint, call send_public_request #####
		get klines
		*/
		parameters.insert({
			{"symbol", "BTCUSDT"},
			{"interval", "1d"},
			{"limit", "10"}});
		sendPublicRequest(curl, "/api/v3/klines", parameters);
		

		/*
		get account informtion
		if you can see the account details, then the API key/secret is correct
		*/
		sendSignedRequest(curl, "GET", "/api/v3/account", parameters);


		/*
		place an order
		if you see order response, then the parameters setting is correct
		*/
		parameters.insert({
			{"symbol", "BNBUSDT"},
			{"side", "BUY"},
			{"type", "LIMIT"},
			{"timeInForce", "GTC"},
			{"quantity", "1"},
			{"price", "50"}});
		sendSignedRequest(curl, "POST", "/api/v3/order", parameters);

		/*
		transfer funds
		*/
		parameters.insert({
			{"fromEmail", ""},
			{"toEmail", ""},
			{"asset", "USDT"},
			{ "amount", "0.1"}});
		sendSignedRequest(curl, "POST", "/sapi/v1/asset/transfer", parameters);
		
		/*
		New Future Account Transfer (FUTURES)
		*/
		parameters.insert({
			{"asset", "USDT"},
			{"amount", "0.01"},
			{"type", "2"}});
		sendSignedRequest(curl, "POST", "/sapi/v1/futures/transfer", parameters);

			
		/* always cleanup */ 
		curl_easy_cleanup(curl);
	}
	curl_global_cleanup();
	return 0;
}