import { Outlet, useNavigate } from 'react-router-dom'
import styled from '@emotion/styled'
import { Stack, Text, Button, colors, spacing, typography } from '@base/ui'
import { useAuthStore } from '../auth/store'

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
  minHeight: '56px',
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
  cursor: 'pointer',
})

const Content = styled.main({
  maxWidth: '960px',
  margin: '0 auto',
  padding: spacing[8],
})

export function AppLayout() {
  const navigate = useNavigate()
  const logout = useAuthStore((s) => s.logout)

  const handleLogout = () => {
    logout()
    navigate('/auth/login')
  }

  return (
    <PageWrapper>
      <Header>
        <Stack direction="row" gap={3} align="center">
          <Logo onClick={() => navigate('/dashboard')} role="button" tabIndex={0} onKeyDown={(e) => e.key === 'Enter' && navigate('/dashboard')}>
            B
          </Logo>
          <Text variant="body1">Base</Text>
        </Stack>
        <Button variant="ghost" size="sm" onClick={handleLogout}>
          로그아웃
        </Button>
      </Header>
      <Content>
        <Outlet />
      </Content>
    </PageWrapper>
  )
}
