import { request } from '../client'

const RSA_PREFIX = 'rsa:'

let cachedPublicKey: CryptoKey | null = null
let cachedPublicKeyValue: string | null = null

async function importPublicKey(spkiBase64: string): Promise<CryptoKey> {
  const binary = Uint8Array.from(atob(spkiBase64), (char) => char.charCodeAt(0))
  return crypto.subtle.importKey(
    'spki',
    binary,
    { name: 'RSA-OAEP', hash: 'SHA-256' },
    false,
    ['encrypt'],
  )
}

async function getPublicKey(): Promise<CryptoKey> {
  const { publicKey } = await request<{ publicKey: string }>('/auth/crypto/public-key', { method: 'GET' })
  if (cachedPublicKey && cachedPublicKeyValue === publicKey) {
    return cachedPublicKey
  }

  cachedPublicKeyValue = publicKey
  cachedPublicKey = await importPublicKey(publicKey)
  return cachedPublicKey
}

export async function encryptPassword(password: string): Promise<string> {
  const publicKey = await getPublicKey()
  const encoded = new TextEncoder().encode(password)
  const cipher = await crypto.subtle.encrypt({ name: 'RSA-OAEP' }, publicKey, encoded)
  const base64 = btoa(String.fromCharCode(...new Uint8Array(cipher)))
  return `${RSA_PREFIX}${base64}`
}

export function clearPasswordPublicKeyCache() {
  cachedPublicKey = null
  cachedPublicKeyValue = null
}
