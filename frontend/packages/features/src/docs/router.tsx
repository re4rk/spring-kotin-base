import type { RouteObject } from 'react-router-dom'
import { Navigate } from 'react-router-dom'
import { DocsLayout } from './layouts/DocsLayout.tsx'
import { TypographyPage } from './pages/TypographyPage.tsx'
import { ColorsPage } from './pages/ColorsPage.tsx'
import { ButtonPage } from './pages/ButtonPage.tsx'
import { FormPage } from './pages/FormPage.tsx'
import { FeedbackPage } from './pages/FeedbackPage.tsx'
import { OverlayPage } from './pages/OverlayPage.tsx'
import { NavigationPage } from './pages/NavigationPage.tsx'

export const docsRoutes: RouteObject[] = [
  {
    path: '/design-system',
    element: <DocsLayout />,
    children: [
      { index: true, element: <Navigate to="/design-system/typography" replace /> },
      { path: 'typography', element: <TypographyPage /> },
      { path: 'colors', element: <ColorsPage /> },
      { path: 'buttons', element: <ButtonPage /> },
      { path: 'form', element: <FormPage /> },
      { path: 'feedback', element: <FeedbackPage /> },
      { path: 'overlay', element: <OverlayPage /> },
      { path: 'navigation', element: <NavigationPage /> },
    ],
  },
]
