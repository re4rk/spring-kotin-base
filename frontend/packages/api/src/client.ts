const BASE_URL = import.meta.env.VITE_API_URL ?? '/api'

export async function request<T>(path: string, init: RequestInit, token?: string | null): Promise<T> {
  const headers: Record<string, string> = { 'Content-Type': 'application/json' }
  if (token) headers['Authorization'] = `Bearer ${token}`

  const res = await fetch(`${BASE_URL}${path}`, { ...init, headers: { ...headers, ...init.headers } })

  if (!res.ok) {
    const body = await res.json().catch(() => ({}))
    throw new Error(body.message ?? `${res.status} ${res.statusText}`)
  }

  if (res.status === 204) return undefined as T

  return res.json()
}
