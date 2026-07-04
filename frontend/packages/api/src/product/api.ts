import { request } from '../client'
import type { ProductCreateRequest, ProductListParams, ProductPageResponse, ProductResponse } from './types'

function buildQuery(params: ProductListParams): string {
  const search = new URLSearchParams()
  if (params.page != null) search.set('page', String(params.page))
  if (params.size != null) search.set('size', String(params.size))
  if (params.status) search.set('status', params.status)
  if (params.name) search.set('name', params.name)
  if (params.minPrice != null) search.set('minPrice', String(params.minPrice))
  if (params.maxPrice != null) search.set('maxPrice', String(params.maxPrice))
  if (params.category) search.set('category', params.category)
  search.set('sort', 'id,desc')
  const qs = search.toString()
  return qs ? `?${qs}` : ''
}

export const productApi = {
  list: (params: ProductListParams = {}) =>
    request<ProductPageResponse>(`/products${buildQuery(params)}`, {}),

  create: (data: ProductCreateRequest) =>
    request<ProductResponse>('/products', {
      method: 'POST',
      body: JSON.stringify(data),
    }),
}
