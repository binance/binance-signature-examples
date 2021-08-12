using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Security.Cryptography;
using System.Text;
using System.Threading.Tasks;
using System.Web;
using Newtonsoft.Json;

namespace BinanceDotNet
{
    public sealed class BinanceService 
    {
        private string baseUrl;
        private string apiKey;
        private string apiSecret;
        private HttpClient httpClient;

        public BinanceService(string apiKey, string apiSecret, string baseUrl, HttpClient httpClient) {
            this.apiKey = apiKey;
            this.apiSecret = apiSecret;
            this.baseUrl = baseUrl;
            this.httpClient = httpClient;        
        }

        private async Task<string> SendAsync(string requestUri, HttpMethod httpMethod, object content = null) {
            Console.WriteLine(requestUri);
            using(var request = new HttpRequestMessage(httpMethod, this.baseUrl + requestUri)) {
                request.Headers.Add("X-MBX-APIKEY", this.apiKey);

                if(!(content is null))
                    request.Content = new StringContent(JsonConvert.SerializeObject(content), Encoding.UTF8, "application/json");

                HttpResponseMessage response = await this.httpClient.SendAsync(request);
                
                using(HttpContent responseContent = response.Content) {
                    string jsonString = await responseContent.ReadAsStringAsync();
                    
                    return jsonString;
                }
            }
        }

        public async Task<string> SendPublicAsync(string requestUri, HttpMethod httpMethod, Dictionary<string, object> query = null, object content = null) {
            if(!(query is null)) {
                string queryString = string.Join("&", query.Where(kvp => !string.IsNullOrWhiteSpace(kvp.Value?.ToString())).Select(kvp => string.Format("{0}={1}", kvp.Key, HttpUtility.UrlEncode(kvp.Value.ToString()))));

                if(!string.IsNullOrWhiteSpace(queryString)) {
                    requestUri += "?" + queryString;
                }
            }

            return await this.SendAsync(requestUri, httpMethod, content);
        }

        public async Task<string> SendSignedAsync(string requestUri, HttpMethod httpMethod, Dictionary<string, object> query = null, object content = null) {
            StringBuilder queryStringBuilder = new StringBuilder();

            if(!(query is null)) {
                string queryParameterString = string.Join("&", query.Where(kvp => !string.IsNullOrWhiteSpace(kvp.Value?.ToString())).Select(kvp => string.Format("{0}={1}", kvp.Key, HttpUtility.UrlEncode(kvp.Value.ToString()))));
                queryStringBuilder.Append(queryParameterString);
            }

            if(queryStringBuilder.Length > 0) 
                queryStringBuilder.Append("&");

            long now = DateTimeOffset.UtcNow.ToUnixTimeMilliseconds();
            queryStringBuilder.Append("timestamp=").Append(now);

            string signature = SignatureHelper.Sign(queryStringBuilder.ToString(), this.apiSecret);
            queryStringBuilder.Append("&signature=").Append(signature);

            StringBuilder requestUriBuilder = new StringBuilder(requestUri);
            requestUriBuilder.Append("?").Append(queryStringBuilder.ToString());

            return await this.SendAsync(requestUriBuilder.ToString(), httpMethod, content);
        }
    }
}