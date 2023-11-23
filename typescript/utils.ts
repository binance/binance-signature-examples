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

export function importSecret(secret: string) {
  const enc = new TextEncoder();
  return crypto.subtle.importKey(
    'raw', enc.encode(secret), {name:'HMAC', hash:'SHA-256'}, false, ['sign']
  );
}

export function signWith(cryptoKey: CryptoKey, data: string) {
  const enc = new TextEncoder();
  return crypto.subtle.sign('HMAC', cryptoKey, enc.encode(data))
    .then((signed) => hex(signed));
}
