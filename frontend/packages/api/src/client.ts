const BASE_URL = import.meta.env.VITE_API_URL ?? '/api'

interface AuthProvider {
  getAccessToken(): string | null
  getRefreshToken(): string | null
  onTokensRefreshed(tokens: { accessToken: string; refreshToken: string; [key: string]: unknown }): void
  onLogout(): void
}

let authProvider: AuthProvider | null = null

export function configureClient(provider: AuthProvider) {
  authProvider = provider
}

let isRefreshing = false
let refreshQueue: Array<(token: string | null) => void> = []

async function tryRefreshToken(): Promise<string | null> {
  const refreshToken = authProvider?.getRefreshToken()
  if (!refreshToken) return null

  const res = await fetch(`${BASE_URL}/auth/refresh`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ refreshToken }),
  })

  if (!res.ok) {
    authProvider?.onLogout()
    return null
  }

  const tokens = await res.json()
  authProvider?.onTokensRefreshed(tokens.data ?? tokens)
  return (tokens.data ?? tokens).accessToken
}

export async function request<T>(
  path: string,
  init: RequestInit,
  token?: string | null,
): Promise<T> {
  const accessToken = token !== undefined ? token : (authProvider?.getAccessToken() ?? null)
  const headers: Record<string, string> = { 'Content-Type': 'application/json' }
  if (accessToken) headers['Authorization'] = `Bearer ${accessToken}`

  const res = await fetch(`${BASE_URL}${path}`, {
    ...init,
    headers: { ...headers, ...init.headers },
  })

  // 만료된 액세스 토큰 → refresh 시도
  if (res.status === 401 && !path.startsWith('/auth/')) {
    const body = await res.json().catch(() => ({}))
    const error = (body as { data?: { code?: string; message?: string }; code?: string; message?: string }).data ?? body
    if (error.code !== 'ACCESS_TOKEN_EXPIRED') {
      throw new Error(error.message ?? `${res.status} ${res.statusText}`)
    }

    if (!isRefreshing) {
      isRefreshing = true
      const newToken = await tryRefreshToken().catch(() => null)
      isRefreshing = false
      refreshQueue.forEach((cb) => cb(newToken))
      refreshQueue = []

      if (!newToken) throw new Error('세션이 만료됐습니다. 다시 로그인해주세요.')
      return request<T>(path, init, newToken)
    }

    // 이미 refresh 중이면 대기열에 추가
    return new Promise((resolve, reject) => {
      refreshQueue.push(async (newToken) => {
        if (!newToken) {
          reject(new Error('세션이 만료됐습니다. 다시 로그인해주세요.'))
          return
        }
        try {
          resolve(await request<T>(path, init, newToken))
        } catch (err) {
          reject(err)
        }
      })
    })
  }

  if (!res.ok) {
    const body = await res.json().catch(() => ({}))
    throw new Error((body.data ?? body).message ?? `${res.status} ${res.statusText}`)
  }

  if (res.status === 204) return undefined as T

  const json = await res.json()
  return json.data ?? json
}
