using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Threading.Tasks;
using System.Web;

namespace BinanceDotNet
{
    class Program
    {
        static async Task Main(string[] args)
        {
            if(args.Count() == 0)
                throw new ArgumentException($"Command missing. Accept commands: signature, spot, futures, deliveryfutures");

            string apiKey = "vmPUZE6mv9SD5VNHk4HlWFsOr6aKE2zvsw0MuIgwCIPy6utIco14y7Ju91duEh8A";
            string apiSecret = "NhqPtmdSJYdKjVHjA7PZj4Mge3R5YNiP1e3UZjInClVN65XAbvqqM6A7H5fATj0j";

            string spotBaseUrl = "https://testnet.binance.vision"; //Production Base Url: https://api.binance.com
            string futuresBaseUrl = "https://testnet.binancefuture.com"; //Production Base Url: https://fapi.binance.com
            string deliveryFutures = "https://testnet.binancefuture.com"; //Production Base Url: https://dapi.binance.com

            HttpClient httpClient = new HttpClient();
            switch(args[0]) {
                case "signature": Signature(apiSecret); break;
                case "spot": await Spot(new BinanceService(apiKey, apiSecret, spotBaseUrl, httpClient)); break;
                case "futures": await Futures(new BinanceService(apiKey, apiSecret, futuresBaseUrl, httpClient)); break;
                case "deliveryfutures": await DeliveryFutures(new BinanceService(apiKey, apiSecret, deliveryFutures, httpClient)); break;
                default: throw new NotSupportedException($"Command {args[0]} is not support. Accept commands: signature, spot, futures, deliveryfutures");
            }
        }

        private static async Task Spot(BinanceService binanceService) {
            /// Klines
            using(var response = binanceService.SendPublicAsync("/api/v3/klines", HttpMethod.Get, new Dictionary<string, object> {
                { "symbol", "BTCUSDT" }, 
                { "interval", "1d"}
            })) {
                Console.WriteLine(await response);
            };

            /// Account Information
            using(var response = binanceService.SendSignedAsync("/api/v3/account", HttpMethod.Get)) {
                Console.WriteLine(await response);
            };
            
            /// Place order
            using(var response = binanceService.SendSignedAsync("/api/v3/order", HttpMethod.Post, new Dictionary<string, object> {
                {"symbol", "BNBUSDT" },
                {"side", "BUY" },
                {"type", "LIMIT" },
                {"timeInForce", "GTC" },
                {"quantity", 1 },
                {"price", 200 }
            })) {
                Console.WriteLine(await response);
            };

            /// User Universal Transfer
            using(var response = binanceService.SendSignedAsync("/sapi/v1/asset/transfer", HttpMethod.Post, new Dictionary<string, object> {
                { "type", "MAIN_MARGIN" },
                { "asset", "USDT" },
                { "amount", 0.1 }
            })) {
                Console.WriteLine(await response);
            };

            /// New Future Account Transfer (FUTURES)
            using(var response = binanceService.SendSignedAsync("/sapi/v1/futures/transfer", HttpMethod.Post, new Dictionary<string, object> {
                { "asset", "USDT" },
                { "amount", 0.01 },
                { "type", 2 }
            })) {
                Console.WriteLine(await response);
            };
        }
        
        private static async Task Futures(BinanceService binanceService) {
            /// Klines
            using(var response = binanceService.SendPublicAsync("/fapi/v1/klines", HttpMethod.Get, new Dictionary<string, object> {
                { "symbol", "BTCUSDT" }, 
                { "interval", "1d"}
            })) {
                Console.WriteLine(await response);
            };
            
            /// Get Account Information
            using(var response = binanceService.SendSignedAsync("/fapi/v2/account", HttpMethod.Get)) {
                Console.WriteLine(await response);
            };
            
            /// Placed Order
            using(var response = binanceService.SendSignedAsync("/fapi/v1/order", HttpMethod.Post, new Dictionary<string, object> {
                { "symbol", "BNBUSDT" },
                { "side", "BUY" },
                { "type", "LIMIT" },
                { "timeInForce", "GTC" },
                { "quantity", 1 },
                { "price", 15 }
            })) {
                Console.WriteLine(await response);
            };
            
            /// Placed Batched Orders
            using(var response = binanceService.SendSignedAsync("/fapi/v1/batchOrders", HttpMethod.Post, new Dictionary<string, object> {
                { "batchOrders", new object[] {
                    new {
                        symbol = "BNBUSDT",
                        side = "BUY",
                        type = "STOP",
                        quantity = 1,
                        price = 9000,
                        timeInForce = "GTC",
                        stopPrice = 9100
                    },
                    new {
                        symbol = "BNBUSDT",
                        side = "BUY",
                        type = "LIMIT",
                        quantity = 1,
                        price = 15,
                        timeInForce = "GTC"
                    }}
                }
            })) {
                Console.WriteLine(await response);
            };
        }
    
        private static async Task DeliveryFutures(BinanceService binanceService) {
            /// Server Time
            using(var response = binanceService.SendPublicAsync("/dapi/v1/time", HttpMethod.Get)) {
                Console.WriteLine(await response);
            };
            
            /// Account Information
            using(var response = binanceService.SendSignedAsync("/dapi/v1/account", HttpMethod.Get)) {
                Console.WriteLine(await response);
            };
            
            /// Place Order
            using(var response = binanceService.SendSignedAsync("/dapi/v1/order", HttpMethod.Post, new Dictionary<string, object> {
                { "symbol", "BTCUSD_200925" },
                { "side", "BUY" },
                { "type", "LIMIT" },
                { "timeInForce", "GTC" },
                { "quantity", 1 },
                { "price", 9000 }
            })) {
                Console.WriteLine(await response);
            };
            
            /// Placed Batched Orders
            using(var response = binanceService.SendSignedAsync("/dapi/v1/batchOrders", HttpMethod.Post, new Dictionary<string, object> {
                { "batchOrders", new object[] {
                    new {
                        symbol = "BTCUSD_200925",
                        side = "BUY",
                        type = "LIMIT",
                        timeInForce = "GTC",
                        quantity = 1,
                        price = 9000
                    },
                    new {
                        symbol = "BTCUSD_200925",
                        side = "BUY",
                        type = "LIMIT",
                        timeInForce = "GTC",
                        quantity = 1,
                        price = 9000
                    }}
                }
            })) {
                Console.WriteLine(await response);
            };
        }

        private static void Signature(string apiSecret) {
            // Basic 
            Dictionary<string, object> basicParameters = new Dictionary<string, object> {
                { "timestamp", 1578963600000 }
            };

            string basicQueryString = BuildQueryString(basicParameters);
            Console.WriteLine(basicQueryString);
            string basicSignature = SignatureHelper.Sign(basicQueryString, apiSecret);
            Console.WriteLine(basicSignature);

            // Complex
            Dictionary<string, object> complexParameters = new Dictionary<string, object> {
                { "symbol", "LTCBTC" },
                { "side", "BUY" },
                { "type", "LIMIT" },
                { "timeInForce", "GTC" },
                { "quantity", 1 },
                { "price", 0.1 },
                { "recvWindow", 5000 },
                { "timestamp", 1499827319559 },
            };
            
            string complexQueryString = BuildQueryString(complexParameters);
            Console.WriteLine(complexQueryString);
            string complexSignature = SignatureHelper.Sign(complexQueryString, apiSecret);
            Console.WriteLine(complexSignature);
        }

        /// <summary>Builds a URL encoded query string for the given parameters.</summary>
        private static string BuildQueryString(Dictionary<string, object> queryParameters) {
            return string.Join("&", queryParameters.Select(kvp => 
                string.Format("{0}={1}", kvp.Key, HttpUtility.UrlEncode(kvp.Value.ToString()))));
        }
    }
}
