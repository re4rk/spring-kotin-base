export interface LoginRequest {
  email: string
  password: string
}

export interface RegisterRequest {
  email: string
  name: string
  password: string
}

export interface UserResponse {
  id: number
  email: string
  name: string
  roles: ('ADMIN' | 'USER')[]
}

export interface TokenResponse {
  accessToken: string
  refreshToken: string
  user: UserResponse
}

export interface RefreshTokenRequest {
  refreshToken: string
}

export interface PasswordResetRequest {
  email: string
}

export interface PasswordResetConfirmRequest {
  token: string
  newPassword: string
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
}
