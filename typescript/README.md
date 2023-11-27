# TypeScript (Web API)

This TypeScript demo uses the [Web Crypto API][web-crypto-api], which has a rather wide compatibility as a [Web API][web-api]. For example, not only that they can be called conveniently in any brower console, but at the time of writing, JavaScript runtimes like [Node.js](https://nodejs.org/) and [Deno](https://deno.com/) also implemented required crypto APIs for request signing.

See also these documentation:

- MDN: [Web Crypto API - Web APIs \| MDN][mdn-web-crypto-web-api]
- Node.js documentation ([19.x][nodejs-v19-webcrypto], [20.x LTS][nodejs-v20-webcrypto] or [latest][nodejs-webcrypto])
- Deno: [SubtleCrypto \| Runtime APIs \| Deno][deno-subtlecrypto]

[web-crypto-api]: https://www.w3.org/TR/WebCryptoAPI/
[web-api]: https://developer.mozilla.org/en-US/docs/Web/API
[mdn-web-crypto-web-api]: https://developer.mozilla.org/en-US/docs/Web/API/Web_Crypto_API
[nodejs-webcrypto]: https://nodejs.org/api/webcrypto.html
[nodejs-v19-webcrypto]: https://nodejs.org/docs/latest-v19.x/api/webcrypto.html
[nodejs-v20-webcrypto]: https://nodejs.org/docs/latest-v20.x/api/webcrypto.html
[deno-subtlecrypto]: https://deno.land/api@v1.38.2?s=SubtleCrypto

This demo seperates key importing and signing funtions, which can be found in [*utils.ts*](./utils.ts).

Alongside it are [*sign.ts*](./sign.ts) that shows basic signing and [*request.ts*](./request.ts) in which a rather real-world request is composed.

## Run with Deno (recommended)

Run with Deno is quite simple. Only that it currently doesn't provide ARM64 build officially.

With Deno installed, just `run` the script:

```
deno run sign.ts
```

## Run with Node.js

To keep it simple and compatible with Deno, here we use [`swc`](https://swc.rs) which supports `import`ing from `.ts` (it is also [used by Deno](https://docs.deno.com/runtime/manual/advanced/typescript/overview)).

```
npm i @swc/cli @swc/core
```

And to run TypeScript on-the-fly with `node`, we can use `ts-node`.

```
npm i ts-node
```

Since we use `await` in the scripts, we want `node` to treat our JavaScript files as ES Modules. To do that, we [can add `"type": "module"`](https://stackoverflow.com/questions/58273824/typescript-cannot-use-import-statement-outside-a-module) to their adjacent *package.json* and run `ts-node` with `esm` support on.

However recent Node.js (20+) [has conflicts with `ts-node`](https://github.com/TypeStrong/ts-node/issues/1997), while former Node.js versions (18 or earlier) have no stable support for `crypto`. If you'd really like to run with Node.js, 19.x can be the choice for the time being.

Run the script using `ts-node` with `--esm` and `--swc` options:

```
npx ts-node --esm --swc sign.ts 
```

---

If you're not willing to use `swc`, then please install `typesrcipt`, and manually change `*.ts` to `*.js` in all `import` statements. You may also ensure that `moduleResolution` in TypeScript configuration is set to `NodeNext`.

```
npm i typescript
sed -i 's/utils.ts/utils.js/g' *.ts
```

Then run `ts-node` but without `--swc` option:

```
npx ts-node --esm sign.ts 
```

Or if you'd like to use a newer Node.js currently, or if you want to run the script in a browser console, you may need to do the modification as above and manually run the TypeScript build (here using `tsc`):

```
npx tsc --build
```

To run the script's JavaScript version with `node`:

```
node sign.js
```

