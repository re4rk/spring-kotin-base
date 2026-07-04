import { useState } from 'react'
import styled from '@emotion/styled'
import { useQuery } from '@tanstack/react-query'
import { adminApi } from '@base/api'
import type { UserResponse } from '@base/api'
import { Stack, Text, Button, Badge, Spinner, colors, spacing, typography, radii } from '@base/ui'

const MOBILE_BREAKPOINT = '768px'

const TableWrapper = styled.div({
  display: 'none',
  [`@media (min-width: ${MOBILE_BREAKPOINT})`]: {
    display: 'block',
    overflowX: 'auto',
  },
})

const Table = styled.table({
  width: '100%',
  borderCollapse: 'collapse',
  background: '#fff',
  borderRadius: radii.md,
  overflow: 'hidden',
  border: `1px solid ${colors.gray[200]}`,
})

const Th = styled.th({
  padding: `${spacing[3]} ${spacing[4]}`,
  textAlign: 'left',
  fontSize: typography.fontSize.xs,
  fontWeight: typography.fontWeight.semibold,
  fontFamily: typography.fontFamily.sans,
  color: colors.gray[500],
  background: colors.gray[50],
  borderBottom: `1px solid ${colors.gray[200]}`,
  whiteSpace: 'nowrap',
})

const Td = styled.td({
  padding: `${spacing[3]} ${spacing[4]}`,
  fontSize: typography.fontSize.sm,
  fontFamily: typography.fontFamily.sans,
  color: colors.gray[700],
  borderBottom: `1px solid ${colors.gray[100]}`,
})

const CardList = styled.div({
  display: 'flex',
  flexDirection: 'column',
  gap: spacing[3],
  [`@media (min-width: ${MOBILE_BREAKPOINT})`]: {
    display: 'none',
  },
})

const UserCard = styled.article({
  background: '#fff',
  border: `1px solid ${colors.gray[200]}`,
  borderRadius: radii.md,
  padding: spacing[4],
})

const CardHeader = styled.div({
  display: 'flex',
  alignItems: 'flex-start',
  justifyContent: 'space-between',
  gap: spacing[3],
  marginBottom: spacing[3],
})

const CardMeta = styled.div({
  display: 'grid',
  gap: spacing[2],
})

const CardRow = styled.div({
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'space-between',
  gap: spacing[2],
})

const MetaLabel = styled.span({
  flexShrink: 0,
  fontSize: typography.fontSize.xs,
  fontWeight: typography.fontWeight.medium,
  fontFamily: typography.fontFamily.sans,
  color: colors.gray[500],
})

const MetaValue = styled.span({
  fontSize: typography.fontSize.sm,
  fontFamily: typography.fontFamily.sans,
  color: colors.gray[800],
  textAlign: 'right',
  wordBreak: 'break-all',
})

const RoleGroup = styled.div({
  display: 'flex',
  flexWrap: 'wrap',
  gap: spacing[1],
  justifyContent: 'flex-end',
})

const Pagination = styled(Stack)({
  flexWrap: 'wrap',
})

function UserRoles({ roles }: { roles: UserResponse['roles'] }) {
  return (
    <>
      {roles.map((role) => (
        <Badge key={role} variant={role === 'ADMIN' ? 'primary' : 'secondary'}>
          {role}
        </Badge>
      ))}
    </>
  )
}

function UserCardItem({ user }: { user: UserResponse }) {
  return (
    <UserCard>
      <CardHeader>
        <Stack gap={1}>
          <Text variant="body1">{user.name}</Text>
          <Text variant="caption" color={colors.gray[500]}>
            ID {user.id}
          </Text>
        </Stack>
        <RoleGroup>
          <UserRoles roles={user.roles} />
        </RoleGroup>
      </CardHeader>
      <CardMeta>
        <CardRow>
          <MetaLabel>이메일</MetaLabel>
          <MetaValue>{user.email}</MetaValue>
        </CardRow>
      </CardMeta>
    </UserCard>
  )
}

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
        <>
          <CardList>
            {data?.content.map((user) => (
              <UserCardItem key={user.id} user={user} />
            ))}
          </CardList>

          <TableWrapper>
            <Table>
              <thead>
                <tr>
                  <Th>ID</Th>
                  <Th>이름</Th>
                  <Th>이메일</Th>
                  <Th>역할</Th>
                </tr>
              </thead>
              <tbody>
                {data?.content.map((user) => (
                  <tr key={user.id}>
                    <Td>{user.id}</Td>
                    <Td>{user.name}</Td>
                    <Td>{user.email}</Td>
                    <Td>
                      <RoleGroup style={{ justifyContent: 'flex-start' }}>
                        <UserRoles roles={user.roles} />
                      </RoleGroup>
                    </Td>
                  </tr>
                ))}
              </tbody>
            </Table>
          </TableWrapper>
        </>
      )}

      {data && data.totalPages > 1 && (
        <Pagination direction="row" gap={2} align="center" justify="center">
          <Button variant="secondary" size="sm" disabled={page === 0} onClick={() => setPage((p) => p - 1)}>
            이전
          </Button>
          <Text variant="body2" color={colors.gray[500]}>
            {page + 1} / {data.totalPages}
          </Text>
          <Button
            variant="secondary"
            size="sm"
            disabled={page >= data.totalPages - 1}
            onClick={() => setPage((p) => p + 1)}
          >
            다음
          </Button>
        </Pagination>
      )}
    </Stack>
  )
}
