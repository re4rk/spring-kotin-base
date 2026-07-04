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
  padding: `${spacing[4]} ${spacing[6]}`,
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'space-between',
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
})

const BoldText = styled.span({
  fontWeight: typography.fontWeight.semibold,
  fontFamily: typography.fontFamily.sans,
  fontSize: typography.fontSize.base,
  color: colors.gray[900],
})

const Content = styled.main({
  maxWidth: '960px',
  margin: '0 auto',
  padding: spacing[8],
})

export function DashboardPage() {
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
          <Logo>B</Logo>
          <BoldText>Base</BoldText>
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
              Base에 오신 것을 환영합니다.
            </Text>
          </Stack>

          <Card padding={6}>
            <Stack gap={2}>
              <BoldText>🎉 로그인 성공</BoldText>
              <Text variant="body2" color={colors.gray[600]}>
                액세스 토큰이 localStorage에 저장됐습니다. 토큰 만료 시 자동으로 갱신됩니다.
              </Text>
            </Stack>
          </Card>
        </Stack>
      </Content>
    </PageWrapper>
  )
}
