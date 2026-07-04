import type { RouteObject } from 'react-router-dom'
import { ProtectedRoute } from '../auth/ProtectedRoute'
import { AppLayout } from '../layouts/AppLayout'
import { DashboardPage } from './pages/DashboardPage'
import { CreateProductPage } from '../product/pages/CreateProductPage'
import { ProductListPage } from '../product/pages/ProductListPage'

export const dashboardRoutes: RouteObject[] = [
  {
    element: <ProtectedRoute />,
    children: [
      {
        element: <AppLayout />,
        children: [
          { path: '/dashboard', element: <DashboardPage /> },
          { path: '/products', element: <ProductListPage /> },
          { path: '/products/new', element: <CreateProductPage /> },
        ],
      },
    ],
  },
]
