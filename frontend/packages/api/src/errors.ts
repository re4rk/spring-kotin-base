export const ErrorCode = {
  UNAUTHORIZED: 'UNAUTHORIZED',
  ACCESS_TOKEN_EXPIRED: 'ACCESS_TOKEN_EXPIRED',
  REFRESH_TOKEN_EXPIRED: 'REFRESH_TOKEN_EXPIRED',
  USER_REFRESH_TOKEN_INVALID: 'USER_REFRESH_TOKEN_INVALID',
  USER_REFRESH_TOKEN_REUSED: 'USER_REFRESH_TOKEN_REUSED',
} as const

export type ErrorCode = (typeof ErrorCode)[keyof typeof ErrorCode]

interface ApiErrorBody {
  code?: string
  message?: string
}

export class ApiError extends Error {
  readonly code?: string
  readonly status?: number

  constructor(message: string, code?: string, status?: number) {
    super(message)
    this.name = 'ApiError'
    this.code = code
    this.status = status
  }
}

export function parseApiError(body: unknown, status?: number): ApiError {
  const error = ((body as { data?: ApiErrorBody })?.data ?? body) as ApiErrorBody
  const message = error.message ?? (status ? `${status}` : '요청 처리 중 오류가 발생했습니다.')

  return new ApiError(message, error.code, status)
}

export function isSessionExpiredError(error: unknown): error is ApiError {
  if (!(error instanceof ApiError) || !error.code) return false

  return (
    error.code === ErrorCode.ACCESS_TOKEN_EXPIRED ||
    error.code === ErrorCode.REFRESH_TOKEN_EXPIRED ||
    error.code === ErrorCode.USER_REFRESH_TOKEN_REUSED
  )
}
