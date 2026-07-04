import { request } from '../client'
import type { ProductCreateRequest, ProductResponse } from './types'

export const productApi = {
  create: (data: ProductCreateRequest) =>
    request<ProductResponse>('/products', {
      method: 'POST',
      body: JSON.stringify(data),
    }),
}
