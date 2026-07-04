import { create } from 'zustand'
import { persist } from 'zustand/middleware'
import type { TokenResponse } from '@base/api'

interface AuthState {
  accessToken: string | null
  refreshToken: string | null
  isAuthenticated: boolean
  login: (tokens: TokenResponse) => void
  logout: () => void
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      accessToken: null,
      refreshToken: null,
      isAuthenticated: false,
      login: ({ accessToken, refreshToken }) =>
        set({ accessToken, refreshToken, isAuthenticated: true }),
      logout: () =>
        set({ accessToken: null, refreshToken: null, isAuthenticated: false }),
    }),
    { name: 'auth' },
  ),
)
