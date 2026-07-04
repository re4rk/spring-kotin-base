import styled from '@emotion/styled'
import { useNavigate } from 'react-router-dom'
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

const Content = styled.main({
  maxWidth: '960px',
  margin: '0 auto',
  padding: spacing[8],
})

export function DashboardPage() {
  const navigate = useNavigate()
  const logout = useAuthStore((s) => s.logout)
  const user = useAuthStore((s) => s.user)
  const isAdmin = user?.roles.includes('ADMIN')

  const handleLogout = () => {
    logout()
    navigate('/auth/login')
  }

  return (
    <PageWrapper>
      <Header>
        <Stack direction="row" gap={3} align="center">
          <Logo>B</Logo>
          <Text variant="body1">Base</Text>
        </Stack>
        <Button variant="ghost" size="sm" onClick={handleLogout}>
          로그아웃
        </Button>
      </Header>

      <Content>
        <Stack gap={6}>
          <Stack gap={1}>
            <Text variant="h2">대시보드</Text>
            <Text variant="body2" color={colors.gray[500]}>
              {user ? `${user.name}님, 환영합니다.` : 'Base에 오신 것을 환영합니다.'}
            </Text>
          </Stack>

          {isAdmin && (
            <Card padding={6}>
              <Stack gap={4}>
                <Stack gap={1}>
                  <Text variant="h4">Admin</Text>
                  <Text variant="body2" color={colors.gray[500]}>
                    유저 관리와 디자인시스템 문서를 확인할 수 있습니다.
                  </Text>
                </Stack>
                <Button onClick={() => navigate('/admin')}>Admin 페이지로 이동</Button>
              </Stack>
            </Card>
          )}

          <Card padding={6}>
            <Stack gap={2}>
              <Text variant="body2" color={colors.gray[600]}>
                액세스 토큰이 localStorage에 저장됩니다. 만료 시 자동으로 갱신됩니다.
              </Text>
            </Stack>
          </Card>
        </Stack>
      </Content>
    </PageWrapper>
  )
}
