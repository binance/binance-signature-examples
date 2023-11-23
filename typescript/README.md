# TypeScript (Web API)

This TypeScript demo uses the [Web Crypto API][web-crypto-api], which has a rather wide compatibility as a [Web API][web-api]. For example, not only that they can be called conveniently in any brower console, but at the time of writing, JavaScript runtimes like [Node.js](https://nodejs.org/) and [Deno](https://deno.com/) also implemented required crypto APIs for request signing.

See also these documentation:

- MDN: [Web Crypto API - Web APIs \| MDN][mdn-web-crypto-web-api]
- Node.js documentation ([20.x LTS][nodejs-v20-webcrypto] or [latest][nodejs-webcrypto])
- Deno: [SubtleCrypto \| Runtime APIs \| Deno][deno-subtlecrypto]

[web-crypto-api]: https://www.w3.org/TR/WebCryptoAPI/
[web-api]: https://developer.mozilla.org/en-US/docs/Web/API
[mdn-web-crypto-web-api]: https://developer.mozilla.org/en-US/docs/Web/API/Web_Crypto_API
[nodejs-webcrypto]: https://nodejs.org/api/webcrypto.html
[nodejs-v20-webcrypto]: https://nodejs.org/docs/latest-v20.x/api/webcrypto.html
[deno-subtlecrypto]: https://deno.land/api@v1.38.2?s=SubtleCrypto

This demo seperates key importing and signing funtions, which can be found in [*utils.ts*](./utils.ts).
