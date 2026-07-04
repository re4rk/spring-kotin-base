import styled from '@emotion/styled'
import { useNavigate, useLocation, Outlet } from 'react-router-dom'
import { Stack, Text, Button, Card, colors, spacing, typography } from '@base/ui'
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

export function DashboardPage() {
  const navigate = useNavigate()
  const location = useLocation()
  const logout = useAuthStore((s) => s.logout)
  const user = useAuthStore((s) => s.user)

  const isDesignSystem = location.pathname.startsWith('/design-system')

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
            <Tab active={!isDesignSystem} onClick={() => navigate('/dashboard')}>
              홈
            </Tab>
            <Tab active={isDesignSystem} onClick={() => navigate('/design-system')}>
              디자인시스템
            </Tab>
            {user?.roles.includes('ADMIN') && (
              <Tab onClick={() => navigate('/admin/users')}>유저 관리</Tab>
            )}
          </TabBar>
        </HeaderLeft>
        <Button variant="ghost" size="sm" onClick={handleLogout}>
          로그아웃
        </Button>
      </Header>

      <Content>
        <Stack gap={6}>
          <Stack gap={1}>
            <Text variant="h2">대시보드</Text>
            <Text variant="body2" color={colors.gray[500]}>
              Base에 오신 것을 환영합니다.
            </Text>
          </Stack>

          <Card padding={6}>
            <Stack gap={2}>
              <Text variant="body2" color={colors.gray[600]}>
                액세스 토큰이 localStorage에 저장됩니다. 만료(30초) 시 자동으로 갱신됩니다.
              </Text>
            </Stack>
          </Card>
        </Stack>
      </Content>
      <Outlet />
    </PageWrapper>
  )
}
