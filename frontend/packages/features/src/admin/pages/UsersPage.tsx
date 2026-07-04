import { useState } from 'react'
import styled from '@emotion/styled'
import { useNavigate } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { adminApi } from '@base/api'
import { Stack, Text, Button, Badge, Spinner, colors, spacing, typography } from '@base/ui'
import { useAuthStore } from '../../auth/store'

const PageWrapper = styled.div({
  minHeight: '100svh',
  background: colors.gray[100],
})

const Header = styled.header({
  background: '#fff',
  borderBottom: `1px solid ${colors.gray[200]}`,
  padding: `0 ${spacing[6]}`,
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'space-between',
  gap: spacing[6],
})

const HeaderLeft = styled.div({
  display: 'flex',
  alignItems: 'center',
  gap: spacing[6],
})

const Logo = styled.div({
  width: '32px',
  height: '32px',
  borderRadius: '8px',
  background: colors.primary[500],
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  color: '#fff',
  fontSize: typography.fontSize.sm,
  fontWeight: typography.fontWeight.bold,
  fontFamily: typography.fontFamily.sans,
  flexShrink: 0,
})

const TabBar = styled.nav({
  display: 'flex',
  alignItems: 'stretch',
  gap: 0,
  height: '56px',
})

const Tab = styled.button<{ active?: boolean }>(({ active }) => ({
  padding: `0 ${spacing[4]}`,
  background: 'none',
  border: 'none',
  borderBottom: `2px solid ${active ? colors.primary[500] : 'transparent'}`,
  fontSize: typography.fontSize.sm,
  fontWeight: active ? typography.fontWeight.semibold : typography.fontWeight.medium,
  fontFamily: typography.fontFamily.sans,
  color: active ? colors.primary[600] : colors.gray[500],
  cursor: 'pointer',
  transition: 'color 120ms, border-color 120ms',
  whiteSpace: 'nowrap',
  '&:hover': {
    color: active ? colors.primary[600] : colors.gray[700],
  },
}))

const Content = styled.main({
  maxWidth: '960px',
  margin: '0 auto',
  padding: spacing[8],
})

const Table = styled.table({
  width: '100%',
  borderCollapse: 'collapse',
  background: '#fff',
  borderRadius: '8px',
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
})

const Td = styled.td({
  padding: `${spacing[3]} ${spacing[4]}`,
  fontSize: typography.fontSize.sm,
  fontFamily: typography.fontFamily.sans,
  color: colors.gray[700],
  borderBottom: `1px solid ${colors.gray[100]}`,
})

export function UsersPage() {
  const navigate = useNavigate()
  const logout = useAuthStore((s) => s.logout)
  const [page, setPage] = useState(0)

  const { data, isLoading } = useQuery({
    queryKey: ['admin', 'users', page],
    queryFn: () => adminApi.listUsers(page),
  })

  const handleLogout = () => {
    logout()
    navigate('/auth/login')
  }

  return (
    <PageWrapper>
      <Header>
        <HeaderLeft>
          <Stack direction="row" gap={3} align="center">
            <Logo>B</Logo>
          </Stack>
          <TabBar>
            <Tab onClick={() => navigate('/dashboard')}>홈</Tab>
            <Tab active>유저 관리</Tab>
          </TabBar>
        </HeaderLeft>
        <Button variant="ghost" size="sm" onClick={handleLogout}>
          로그아웃
        </Button>
      </Header>

      <Content>
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
                      {user.roles.map((role) => (
                        <Badge key={role} variant={role === 'ADMIN' ? 'primary' : 'secondary'}>
                          {role}
                        </Badge>
                      ))}
                    </Td>
                  </tr>
                ))}
              </tbody>
            </Table>
          )}

          {data && data.totalPages > 1 && (
            <Stack direction="row" gap={2} align="center" justify="center">
              <Button
                variant="outline"
                size="sm"
                disabled={page === 0}
                onClick={() => setPage((p) => p - 1)}
              >
                이전
              </Button>
              <Text variant="body2" color={colors.gray[500]}>
                {page + 1} / {data.totalPages}
              </Text>
              <Button
                variant="outline"
                size="sm"
                disabled={page >= data.totalPages - 1}
                onClick={() => setPage((p) => p + 1)}
              >
                다음
              </Button>
            </Stack>
          )}
        </Stack>
      </Content>
    </PageWrapper>
  )
}
