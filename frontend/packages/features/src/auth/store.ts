import { create } from 'zustand'
import { persist } from 'zustand/middleware'
import type { TokenResponse, UserResponse } from '@base/api'

interface AuthState {
  accessToken: string | null
  refreshToken: string | null
  user: UserResponse | null
  isAuthenticated: boolean
  sessionExpiredMessage: string | null
  setTokens: (tokens: TokenResponse) => void
  setUser: (user: UserResponse) => void
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
      setTokens: ({ accessToken, refreshToken }) =>
        set({
          accessToken,
          refreshToken,
          isAuthenticated: true,
          sessionExpiredMessage: null,
        }),
      setUser: (user) => set({ user }),
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
