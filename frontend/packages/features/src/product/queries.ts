import { useInfiniteQuery } from '@tanstack/react-query'
import { productApi } from '@base/api'
import type { ProductListParams } from '@base/api'

const PAGE_SIZE = 20

export function useProductsInfiniteQuery(params: Omit<ProductListParams, 'page' | 'size'> = {}) {
  return useInfiniteQuery({
    queryKey: ['products', params],
    queryFn: ({ pageParam }) =>
      productApi.list({ ...params, page: pageParam, size: PAGE_SIZE }),
    initialPageParam: 0,
    getNextPageParam: (lastPage) =>
      lastPage.number + 1 < lastPage.totalPages ? lastPage.number + 1 : undefined,
  })
}
