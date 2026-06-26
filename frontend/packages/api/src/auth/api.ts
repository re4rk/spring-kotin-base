import { request } from '../client'
import type {
  LoginRequest,
  RegisterRequest,
  TokenResponse,
  RefreshTokenRequest,
  PasswordResetRequest,
  PasswordResetConfirmRequest,
  UserResponse,
} from './types'

export const authApi = {
  login: (data: LoginRequest) =>
    request<TokenResponse>('/auth/login', { method: 'POST', body: JSON.stringify(data) }),

  register: (data: RegisterRequest) =>
    request<UserResponse>('/auth/register', { method: 'POST', body: JSON.stringify(data) }),

  refresh: (data: RefreshTokenRequest) =>
    request<TokenResponse>('/auth/refresh', { method: 'POST', body: JSON.stringify(data) }),

  requestPasswordReset: (data: PasswordResetRequest) =>
    request<void>('/auth/passwords/reset/init', { method: 'POST', body: JSON.stringify(data) }),

  resetPassword: (data: PasswordResetConfirmRequest) =>
    request<void>('/auth/passwords/reset/confirm', { method: 'POST', body: JSON.stringify(data) }),
}
