import { useNavigate } from 'react-router-dom'
import { Stack, Text, Button, Card, colors } from '@base/ui'
import { useAuthStore } from '../../auth/store'

export function DashboardPage() {
  const navigate = useNavigate()
  const user = useAuthStore((s) => s.user)
  const isAdmin = user?.roles.includes('ADMIN')

  return (
    <Stack gap={6}>
      <Stack gap={1}>
        <Text variant="h2">대시보드</Text>
        <Text variant="body2" color={colors.gray[500]}>
          {user ? `${user.name}님, 환영합니다.` : 'Base에 오신 것을 환영합니다.'}
        </Text>
      </Stack>

      <Card padding={6}>
        <Stack gap={4}>
          <Stack gap={1}>
            <Text variant="h4">상품</Text>
            <Text variant="body2" color={colors.gray[500]}>
              상품 목록을 조회하고 새 상품을 등록할 수 있습니다.
            </Text>
          </Stack>
          <Stack direction="row" gap={3} style={{ flexWrap: 'wrap' }}>
            <Button onClick={() => navigate('/products')}>상품 목록</Button>
            <Button variant="secondary" onClick={() => navigate('/products/new')}>
              상품 등록
            </Button>
          </Stack>
        </Stack>
      </Card>

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
  )
}
