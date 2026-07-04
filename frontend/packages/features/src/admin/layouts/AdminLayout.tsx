import styled from '@emotion/styled'
import { useNavigate, useLocation, Outlet } from 'react-router-dom'
import { Stack, Button, colors, spacing, typography } from '@base/ui'
import { useAuthStore } from '../../auth/store'

const PageWrapper = styled.div({
  minHeight: '100svh',
  background: colors.gray[100],
})

const Header = styled.header({
  background: '#fff',
  borderBottom: `1px solid ${colors.gray[200]}`,
  padding: `0 ${spacing[4]}`,
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'space-between',
  gap: spacing[3],
  [`@media (min-width: 768px)`]: {
    padding: `0 ${spacing[6]}`,
    gap: spacing[6],
  },
})

const HeaderLeft = styled.div({
  display: 'flex',
  alignItems: 'center',
  gap: spacing[3],
  minWidth: 0,
  flex: 1,
  [`@media (min-width: 768px)`]: {
    gap: spacing[6],
  },
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
  overflowX: 'auto',
  WebkitOverflowScrolling: 'touch',
  scrollbarWidth: 'none',
  '&::-webkit-scrollbar': {
    display: 'none',
  },
})

const Tab = styled.button<{ active?: boolean }>(({ active }) => ({
  padding: `0 ${spacing[3]}`,
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
  flexShrink: 0,
  '&:hover': {
    color: active ? colors.primary[600] : colors.gray[700],
  },
  [`@media (min-width: 768px)`]: {
    padding: `0 ${spacing[4]}`,
  },
}))

const Content = styled.main({
  maxWidth: '960px',
  margin: '0 auto',
  padding: spacing[4],
  [`@media (min-width: 768px)`]: {
    padding: spacing[8],
  },
})

export function AdminLayout() {
  const navigate = useNavigate()
  const location = useLocation()
  const logout = useAuthStore((s) => s.logout)

  const isUsers = location.pathname.startsWith('/admin/users')
  const isDesignSystem = location.pathname.startsWith('/admin/design-system')

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
            <Tab onClick={() => navigate('/dashboard')}>대시보드</Tab>
            <Tab active={isUsers} onClick={() => navigate('/admin/users')}>
              유저 관리
            </Tab>
            <Tab active={isDesignSystem} onClick={() => navigate('/admin/design-system')}>
              디자인시스템
            </Tab>
          </TabBar>
        </HeaderLeft>
        <Button variant="ghost" size="sm" onClick={handleLogout}>
          로그아웃
        </Button>
      </Header>

      {isDesignSystem ? <Outlet /> : (
        <Content>
          <Outlet />
        </Content>
      )}
    </PageWrapper>
  )
}
