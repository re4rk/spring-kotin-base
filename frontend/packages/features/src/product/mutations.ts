import { useMutation } from '@tanstack/react-query'
import { productApi } from '@base/api'

export function useCreateProductMutation() {
  return useMutation({ mutationFn: productApi.create })
}
