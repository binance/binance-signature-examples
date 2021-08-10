using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Cryptography;
using System.Text;
using System.Web;

namespace SignatureExample
{
    class Program
    {
        static void Main(string[] args)
        {
            string key = "NhqPtmdSJYdKjVHjA7PZj4Mge3R5YNiP1e3UZjInClVN65XAbvqqM6A7H5fATj0j";

            // Example 1
            Dictionary<string, object> example1Parameters = new Dictionary<string, object> {
                { "timestamp", 1578963600000 }
            };

            string example1QueryString = BuildQueryString(example1Parameters);
            Console.WriteLine(example1QueryString);
            string example1Signature = Sign(example1QueryString, key);
            Console.WriteLine(example1Signature);

            // Example 2
            Dictionary<string, object> example2Parameters = new Dictionary<string, object> {
                { "symbol", "LTCBTC" },
                { "side", "BUY" },
                { "type", "LIMIT" },
                { "timeInForce", "GTC" },
                { "quantity", 1 },
                { "price", 0.1 },
                { "recvWindow", 5000 },
                { "timestamp", 1499827319559 },
            };
            
            string example2QueryString = BuildQueryString(example2Parameters);
            Console.WriteLine(example2QueryString);
            string example2Signature = Sign(example2QueryString, key);
            Console.WriteLine(example2Signature);
        }

        /// <summary>Builds a URL encoded query string for the given parameters.</summary>
        private static string BuildQueryString(Dictionary<string, object> queryParameters) {
            return string.Join("&", queryParameters.Select(kvp => 
                string.Format("{0}={1}", kvp.Key, HttpUtility.UrlEncode(kvp.Value.ToString()))));
        }
        
        /// <summary>Signs the given source with the given key using HMAC SHA256.</summary>
        private static string Sign(string source, string key) {
            byte[] keyBytes = Encoding.UTF8.GetBytes(key);
            using(HMACSHA256 hmacsha256 = new HMACSHA256(keyBytes)) {
                byte[] sourceBytes = Encoding.UTF8.GetBytes(source);

                byte[] hash = hmacsha256.ComputeHash(sourceBytes);

                return BitConverter.ToString(hash).Replace("-", "").ToLower();
            }
        }
    }
}
