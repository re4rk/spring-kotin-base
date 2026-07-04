import { Navigate, Outlet } from 'react-router-dom'
import { useAuthStore } from './store'

export function AdminRoute() {
  const user = useAuthStore((s) => s.user)
  if (!user || !user.roles.includes('ADMIN')) return <Navigate to="/dashboard" replace />
  return <Outlet />
}
