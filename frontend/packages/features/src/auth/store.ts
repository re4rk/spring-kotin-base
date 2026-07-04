import { create } from 'zustand'
import { persist } from 'zustand/middleware'
import type { TokenResponse, UserResponse } from '@base/api'

interface AuthState {
  accessToken: string | null
  refreshToken: string | null
  user: UserResponse | null
  isAuthenticated: boolean
  sessionExpiredMessage: string | null
  login: (tokens: TokenResponse) => void
  logout: () => void
  clearSessionExpiredMessage: () => void
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      accessToken: null,
      refreshToken: null,
      user: null,
      isAuthenticated: false,
      sessionExpiredMessage: null,
      login: ({ accessToken, refreshToken, user }) =>
        set({
          accessToken,
          refreshToken,
          user,
          isAuthenticated: true,
          sessionExpiredMessage: null,
        }),
      logout: () =>
        set({
          accessToken: null,
          refreshToken: null,
          user: null,
          isAuthenticated: false,
          sessionExpiredMessage: null,
        }),
      clearSessionExpiredMessage: () => set({ sessionExpiredMessage: null }),
    }),
    { name: 'auth' },
  ),
)
