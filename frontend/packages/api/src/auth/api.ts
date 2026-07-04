import { request } from '../client'
import { encryptPassword } from '../crypto/password'
import type {
  LoginRequest,
  RegisterRequest,
  TokenResponse,
  RefreshTokenRequest,
  PasswordResetRequest,
  PasswordResetConfirmRequest,
  UserResponse,
  PasswordPublicKeyResponse,
} from './types'

export const authApi = {
  getPasswordPublicKey: () =>
    request<PasswordPublicKeyResponse>('/auth/crypto/public-key', { method: 'GET' }),

  login: async (data: LoginRequest) =>
    request<TokenResponse>('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ ...data, password: await encryptPassword(data.password) }),
    }),

  register: async (data: RegisterRequest) =>
    request<UserResponse>('/auth/register', {
      method: 'POST',
      body: JSON.stringify({ ...data, password: await encryptPassword(data.password) }),
    }),

  refresh: (data: RefreshTokenRequest) =>
    request<TokenResponse>('/auth/refresh', { method: 'POST', body: JSON.stringify(data) }),

  me: (token?: string | null) =>
    request<UserResponse>('/users/me', { method: 'GET' }, token),

  requestPasswordReset: (data: PasswordResetRequest) =>
    request<void>('/auth/passwords/reset/init', { method: 'POST', body: JSON.stringify(data) }),

  resetPassword: async (data: PasswordResetConfirmRequest) =>
    request<void>('/auth/passwords/reset/confirm', {
      method: 'POST',
      body: JSON.stringify({ ...data, newPassword: await encryptPassword(data.newPassword) }),
    }),
}
