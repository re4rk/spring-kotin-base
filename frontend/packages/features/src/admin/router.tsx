import type { RouteObject } from 'react-router-dom'
import { Navigate } from 'react-router-dom'
import { ProtectedRoute } from '../auth/ProtectedRoute'
import { AdminRoute } from '../auth/AdminRoute'
import { AdminLayout } from './layouts/AdminLayout'
import { AdminDocsLayout } from './layouts/AdminDocsLayout'
import { UsersPage } from './pages/UsersPage'
import { TypographyPage } from '../docs/pages/TypographyPage'
import { ColorsPage } from '../docs/pages/ColorsPage'
import { ButtonPage } from '../docs/pages/ButtonPage'
import { FormPage } from '../docs/pages/FormPage'
import { FeedbackPage } from '../docs/pages/FeedbackPage'
import { OverlayPage } from '../docs/pages/OverlayPage'
import { NavigationPage } from '../docs/pages/NavigationPage'

export const adminRoutes: RouteObject[] = [
  {
    element: <ProtectedRoute />,
    children: [
      {
        element: <AdminRoute />,
        children: [
          {
            path: '/admin',
            element: <AdminLayout />,
            children: [
              { index: true, element: <Navigate to="/admin/users" replace /> },
              { path: 'users', element: <UsersPage /> },
              {
                path: 'design-system',
                element: <AdminDocsLayout />,
                children: [
                  { index: true, element: <Navigate to="/admin/design-system/typography" replace /> },
                  { path: 'typography', element: <TypographyPage /> },
                  { path: 'colors', element: <ColorsPage /> },
                  { path: 'buttons', element: <ButtonPage /> },
                  { path: 'form', element: <FormPage /> },
                  { path: 'feedback', element: <FeedbackPage /> },
                  { path: 'overlay', element: <OverlayPage /> },
                  { path: 'navigation', element: <NavigationPage /> },
                ],
              },
            ],
          },
        ],
      },
    ],
  },
]
