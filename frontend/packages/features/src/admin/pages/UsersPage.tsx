import { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { adminApi } from '@base/api'
import { Stack, Text, Button, Badge, Spinner, colors, spacing } from '@base/ui'

export function UsersPage() {
  const [page, setPage] = useState(0)

  const { data, isLoading } = useQuery({
    queryKey: ['admin', 'users', page],
    queryFn: () => adminApi.listUsers(page),
  })

  return (
    <Stack gap={6}>
      <Stack gap={1}>
        <Text variant="h2">유저 관리</Text>
        <Text variant="body2" color={colors.gray[500]}>
          {data ? `전체 ${data.totalElements}명` : ''}
        </Text>
      </Stack>

      {isLoading ? (
        <Stack align="center" justify="center" style={{ padding: spacing[12] }}>
          <Spinner size="lg" />
        </Stack>
      ) : (
        <table style={{ width: '100%', borderCollapse: 'collapse', background: '#fff', borderRadius: '8px', overflow: 'hidden', border: `1px solid ${colors.gray[200]}` }}>
          <thead>
            <tr>
              <th style={{ padding: `${spacing[3]} ${spacing[4]}`, textAlign: 'left', fontSize: '12px', color: colors.gray[500], background: colors.gray[50], borderBottom: `1px solid ${colors.gray[200]}` }}>ID</th>
              <th style={{ padding: `${spacing[3]} ${spacing[4]}`, textAlign: 'left', fontSize: '12px', color: colors.gray[500], background: colors.gray[50], borderBottom: `1px solid ${colors.gray[200]}` }}>이름</th>
              <th style={{ padding: `${spacing[3]} ${spacing[4]}`, textAlign: 'left', fontSize: '12px', color: colors.gray[500], background: colors.gray[50], borderBottom: `1px solid ${colors.gray[200]}` }}>이메일</th>
              <th style={{ padding: `${spacing[3]} ${spacing[4]}`, textAlign: 'left', fontSize: '12px', color: colors.gray[500], background: colors.gray[50], borderBottom: `1px solid ${colors.gray[200]}` }}>역할</th>
            </tr>
          </thead>
          <tbody>
            {data?.content.map((user) => (
              <tr key={user.id}>
                <td style={{ padding: `${spacing[3]} ${spacing[4]}`, borderBottom: `1px solid ${colors.gray[100]}` }}>{user.id}</td>
                <td style={{ padding: `${spacing[3]} ${spacing[4]}`, borderBottom: `1px solid ${colors.gray[100]}` }}>{user.name}</td>
                <td style={{ padding: `${spacing[3]} ${spacing[4]}`, borderBottom: `1px solid ${colors.gray[100]}` }}>{user.email}</td>
                <td style={{ padding: `${spacing[3]} ${spacing[4]}`, borderBottom: `1px solid ${colors.gray[100]}` }}>
                  {user.roles.map((role) => (
                    <Badge key={role} variant={role === 'ADMIN' ? 'primary' : 'secondary'}>
                      {role}
                    </Badge>
                  ))}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      {data && data.totalPages > 1 && (
        <Stack direction="row" gap={2} align="center" justify="center">
          <Button variant="secondary" size="sm" disabled={page === 0} onClick={() => setPage((p) => p - 1)}>
            이전
          </Button>
          <Text variant="body2" color={colors.gray[500]}>
            {page + 1} / {data.totalPages}
          </Text>
          <Button variant="secondary" size="sm" disabled={page >= data.totalPages - 1} onClick={() => setPage((p) => p + 1)}>
            다음
          </Button>
        </Stack>
      )}
    </Stack>
  )
}
