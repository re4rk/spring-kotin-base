import { request } from '../client'
import type { UserResponse, PageResponse } from '../auth/types'

export const adminApi = {
  listUsers: (page = 0, size = 20) =>
    request<PageResponse<UserResponse>>(
      `/admin/api/users?page=${page}&size=${size}&sort=id,desc`,
    ),
}
