export type ProductStatus =
  | 'DRAFT'
  | 'PENDING'
  | 'ON_SALE'
  | 'SOLD_OUT'
  | 'DISCONTINUED'

export interface ProductCreateRequest {
  name: string
  price: number
  description?: string
  category?: string
  thumbnailUrl?: string
}

export interface ProductResponse {
  id: number
  name: string
  price: number
  stock: number
  status: ProductStatus
  description: string | null
  category: string | null
  thumbnailUrl: string | null
}
