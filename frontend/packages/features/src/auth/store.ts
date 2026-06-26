import { create } from 'zustand'
import type { TokenResponse } from '@base/api'

interface AuthState {
  accessToken: string | null
  refreshToken: string | null
  isAuthenticated: boolean
  login: (tokens: TokenResponse) => void
  logout: () => void
}

export const useAuthStore = create<AuthState>((set) => ({
  accessToken: null,
  refreshToken: null,
  isAuthenticated: false,
  login: ({ accessToken, refreshToken }) =>
    set({ accessToken, refreshToken, isAuthenticated: true }),
  logout: () => set({ accessToken: null, refreshToken: null, isAuthenticated: false }),
}))
