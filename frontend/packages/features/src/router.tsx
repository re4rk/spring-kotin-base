import { createBrowserRouter, RouterProvider, Navigate } from 'react-router-dom'
import { configureClient } from '@base/api'
import { authRoutes } from './auth/index.ts'
import { AuthBootstrap } from './auth/AuthBootstrap.tsx'
import { docsRoutes } from './docs/index.ts'
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
  ...docsRoutes,
])

export function AppRouter() {
  return (
    <AuthBootstrap>
      <RouterProvider router={router} />
    </AuthBootstrap>
  )
}
