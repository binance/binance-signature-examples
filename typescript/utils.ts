const crypto = globalThis.crypto;

// compute a map from bytes to their hexadecimal string representation
function getByteToHexStringMap() {
  const byteToHex: string[] = new Array(0xff + 1);
  for (let n = 0; n <= 0xff; ++n) {
    byteToHex[n] = n.toString(16).padStart(2, "0");
  }
  return byteToHex;
}

// a pre-computed byte-to-hex map is feasible for perfomance considerations
const byteToHex = getByteToHexStringMap();

function hex(arrayBuffer: ArrayBuffer) {
  const buf = new Uint8Array(arrayBuffer);
  const hexOctets = new Array(buf.length);
  for (let i = 0; i < buf.length; ++i) {
    hexOctets[i] = byteToHex[buf[i]];
  }
  return hexOctets.join("");
}

export async function importSecret(secret: string) {
  const enc = new TextEncoder();
  return await crypto.subtle.importKey(
    'raw', enc.encode(secret), {name:'HMAC', hash:'SHA-256'}, false, ['sign']
  );
}

export async function signWith(cryptoKey: CryptoKey, data: string) {
  const enc = new TextEncoder();
  const signed = await crypto.subtle.sign('HMAC', cryptoKey, enc.encode(data));
  return hex(signed);
}

export async function authAndSign(method: 'GET'|'POST'|'DELETE'|'PUT', url: URL, headers: Headers, body: string, apiKey: string, cryptoKey: CryptoKey) {
  const search = new URLSearchParams(url.search);
  const ts = new Date();
  search.append('timestamp', ts.getTime().toString());

  const data = `${ search.toString() }${ body ? body : "" }`;

  search.append('signature', await signWith(cryptoKey, data));
  url.search = search.toString();

  return new Request(url, {method, body, headers});
};
