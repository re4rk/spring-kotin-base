import { ApiError, ErrorCode, parseApiError } from './errors'

const BASE_URL = import.meta.env.VITE_API_URL ?? '/api'

interface AuthProvider {
  getAccessToken(): string | null
  getRefreshToken(): string | null
  onTokensRefreshed(tokens: { accessToken: string; refreshToken: string; [key: string]: unknown }): void
  onLogout(message?: string): void
}

let authProvider: AuthProvider | null = null

export function configureClient(provider: AuthProvider) {
  authProvider = provider
}

let isRefreshing = false
let refreshQueue: Array<(token: string | null) => void> = []

const SESSION_EXPIRED_MESSAGE = '세션이 만료됐습니다. 다시 로그인해주세요.'

async function tryRefreshToken(): Promise<string | null> {
  const refreshToken = authProvider?.getRefreshToken()
  if (!refreshToken) {
    authProvider?.onLogout(SESSION_EXPIRED_MESSAGE)
    return null
  }

  const res = await fetch(`${BASE_URL}/auth/refresh`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ refreshToken }),
  })

  if (!res.ok) {
    const apiError = parseApiError(await res.json().catch(() => ({})), res.status)
    authProvider?.onLogout(apiError.message)
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

  if (res.status === 401 && !path.startsWith('/auth/')) {
    const apiError = parseApiError(await res.json().catch(() => ({})), res.status)

    if (apiError.code !== ErrorCode.ACCESS_TOKEN_EXPIRED) {
      throw apiError
    }

    if (!isRefreshing) {
      isRefreshing = true
      const newToken = await tryRefreshToken().catch(() => null)
      isRefreshing = false
      refreshQueue.forEach((cb) => cb(newToken))
      refreshQueue = []

      if (!newToken) throw new ApiError(SESSION_EXPIRED_MESSAGE, ErrorCode.REFRESH_TOKEN_EXPIRED, 401)
      return request<T>(path, init, newToken)
    }

    return new Promise((resolve, reject) => {
      refreshQueue.push(async (newToken) => {
        if (!newToken) {
          reject(new ApiError(SESSION_EXPIRED_MESSAGE, ErrorCode.REFRESH_TOKEN_EXPIRED, 401))
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
    throw parseApiError(await res.json().catch(() => ({})), res.status)
  }

  if (res.status === 204) return undefined as T

  const json = await res.json()
  return json.data ?? json
}
