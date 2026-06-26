import type { RouteObject } from 'react-router-dom'
import { Navigate } from 'react-router-dom'
import { AuthLayout } from './layouts/AuthLayout.tsx'
import { LoginPage } from './pages/LoginPage.tsx'

export const authRoutes: RouteObject[] = [
  {
    path: '/auth',
    element: <AuthLayout />,
    children: [
      { index: true, element: <Navigate to="/auth/login" replace /> },
      { path: 'login', element: <LoginPage /> },
    ],
  },
]
