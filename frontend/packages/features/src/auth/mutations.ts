import { useMutation } from '@tanstack/react-query'
import { authApi, isSessionExpiredError } from '@base/api'
import { useAuthStore } from './store'

export function useLoginMutation() {
  const login = useAuthStore((s) => s.login)

  return useMutation({
    mutationFn: authApi.login,
    onSuccess: (tokens) => login(tokens),
  })
}

export function useRegisterMutation() {
  return useMutation({ mutationFn: authApi.register })
}

export function useRefreshMutation() {
  const login = useAuthStore((s) => s.login)

  return useMutation({
    mutationFn: authApi.refresh,
    onSuccess: (tokens) => login(tokens),
    onError: (error) => {
      if (!isSessionExpiredError(error)) return

      useAuthStore.getState().logout()
      useAuthStore.setState({ sessionExpiredMessage: error.message })
    },
  })
}

export function useRequestPasswordResetMutation() {
  return useMutation({ mutationFn: authApi.requestPasswordReset })
}

export function useResetPasswordMutation() {
  return useMutation({ mutationFn: authApi.resetPassword })
}
