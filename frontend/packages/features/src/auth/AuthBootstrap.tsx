import { useEffect } from 'react'
import type { ReactNode } from 'react'
import { authApi } from '@base/api'
import { Spinner } from '@base/ui'
import { useAuthStore } from './store'

export function AuthBootstrap({ children }: { children: ReactNode }) {
  const accessToken = useAuthStore((s) => s.accessToken)
  const user = useAuthStore((s) => s.user)
  const setUser = useAuthStore((s) => s.setUser)
  const logout = useAuthStore((s) => s.logout)

  useEffect(() => {
    if (!accessToken || user) return

    authApi.me().then(setUser).catch(() => logout())
  }, [accessToken, user, setUser, logout])

  if (accessToken && !user) {
    return <Spinner />
  }

  return children
}
