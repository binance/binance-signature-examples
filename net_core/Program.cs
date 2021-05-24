using System;
using System.Net.Http;
using System.Security.Cryptography;
using System.Text;
using System.Threading.Tasks;
using Newtonsoft.Json;

namespace net_core
{
    class Program
    {
        const string api_key = "";
        const string secret_key = "";

        static async Task Main(string[] args)
        {
            var parameters = $"timestamp={DateTimeOffset.Now.ToUnixTimeMilliseconds()}";
            var signature = GenerateSignature(secret_key, parameters);

            var accountInformations = await GetAccountInformations(parameters, signature);

            Console.WriteLine(accountInformations);

            Console.WriteLine("Press any key to exit ...");
            Console.ReadLine();
        }
        
        static async Task<dynamic> GetAccountInformations(string parameters, string signature)
        {
            var httpClient = new HttpClient();

            httpClient.BaseAddress = new Uri("https://api.binance.com");
            httpClient.DefaultRequestHeaders.Add("X-MBX-APIKEY", api_key);
            httpClient.DefaultRequestHeaders.Accept.Add(new System.Net.Http.Headers.MediaTypeWithQualityHeaderValue("application/json"));

            var binanceEnpoint = "/api/v3/account";

            // The ? has to be put there and not in the signature encoding
            var completeEndpoint = $"{binanceEnpoint}?{parameters}&signature={signature}";

            var response = await httpClient.GetAsync(completeEndpoint);

            var resultStr = await response.Content.ReadAsStringAsync();
            if (response.IsSuccessStatusCode)
            {
                var data = JsonConvert.DeserializeObject<dynamic>(resultStr);
                return data;
            }
            else
            {
                throw new Exception(resultStr);
            }
        }

        /// <summary>
        /// Generate a signature for the headers used on the query
        /// </summary>
        /// <param name="headers">HTTP headers as string (ex: symbol=LTCBTC&side=BUY&type=LIMIT&timeInForce=GTC)</param>
        /// <returns>signature encoded with SHA256</returns>
        static string GenerateSignature(string key, string headers)
        {
            byte[] keyBytes = Encoding.UTF8.GetBytes(key);
            byte[] queryStringBytes = Encoding.UTF8.GetBytes(headers);
            HMACSHA256 hmacsha256 = new HMACSHA256(keyBytes);

            byte[] bytes = hmacsha256.ComputeHash(queryStringBytes);

            return BitConverter.ToString(bytes).Replace("-", "").ToLower();
        }
    }
}
