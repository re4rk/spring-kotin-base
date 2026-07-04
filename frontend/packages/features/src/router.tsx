import { createBrowserRouter, RouterProvider, Navigate } from 'react-router-dom'
import { configureClient, clearPasswordPublicKeyCache } from '@base/api'
import { authRoutes } from './auth/index.ts'
import { AuthBootstrap } from './auth/AuthBootstrap.tsx'
import { dashboardRoutes } from './dashboard/index.ts'
import { adminRoutes } from './admin/index.ts'
import { useAuthStore } from './auth/store.ts'

// API 클라이언트에 auth 제공자 주입 (모듈 로드 시 1회 실행)
configureClient({
  getAccessToken: () => useAuthStore.getState().accessToken,
  getRefreshToken: () => useAuthStore.getState().refreshToken,
  onTokensRefreshed: (tokens) => useAuthStore.getState().setTokens(tokens),
  onLogout: (message) => {
    useAuthStore.getState().logout()
    clearPasswordPublicKeyCache()
    if (message) {
      useAuthStore.setState({ sessionExpiredMessage: message })
    }
  },
})

const router = createBrowserRouter([
  { path: '/', element: <Navigate to="/dashboard" replace /> },
  ...authRoutes,
  ...dashboardRoutes,
  ...adminRoutes,
  { path: '/design-system/*', element: <Navigate to="/admin/design-system" replace /> },
])

export function AppRouter() {
  return (
    <AuthBootstrap>
      <RouterProvider router={router} />
    </AuthBootstrap>
  )
}
