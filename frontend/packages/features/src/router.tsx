import { createBrowserRouter, RouterProvider } from 'react-router-dom'
import { authRoutes } from './auth/index.ts'
import { docsRoutes } from './docs/index.ts'

const router = createBrowserRouter([...authRoutes, ...docsRoutes])

export function AppRouter() {
  return <RouterProvider router={router} />
}
