import type { RouteObject } from 'react-router-dom'
import { ProtectedRoute } from '../auth/ProtectedRoute'
import { AdminRoute } from '../auth/AdminRoute'
import { UsersPage } from './pages/UsersPage'

export const adminRoutes: RouteObject[] = [
  {
    element: <ProtectedRoute />,
    children: [
      {
        element: <AdminRoute />,
        children: [{ path: '/admin/users', element: <UsersPage /> }],
      },
    ],
  },
]
