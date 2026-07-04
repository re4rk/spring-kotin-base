import { useMutation } from '@tanstack/react-query'
import { authApi, isSessionExpiredError } from '@base/api'
import { useAuthStore } from './store'

export function useLoginMutation() {
  const setTokens = useAuthStore((s) => s.setTokens)
  const setUser = useAuthStore((s) => s.setUser)

  return useMutation({
    mutationFn: async (data: Parameters<typeof authApi.login>[0]) => {
      const tokens = await authApi.login(data)
      const user = await authApi.me(tokens.accessToken)
      return { tokens, user }
    },
    onSuccess: ({ tokens, user }) => {
      setTokens(tokens)
      setUser(user)
    },
  })
}

export function useRegisterMutation() {
  return useMutation({ mutationFn: authApi.register })
}

export function useRefreshMutation() {
  const setTokens = useAuthStore((s) => s.setTokens)

  return useMutation({
    mutationFn: authApi.refresh,
    onSuccess: (tokens) => setTokens(tokens),
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
