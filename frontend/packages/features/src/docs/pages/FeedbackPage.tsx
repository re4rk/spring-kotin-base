import { Stack, Card, Text, Badge, Alert, Spinner, Progress, Skeleton, Avatar, colors } from '@base/ui'
import { SectionHeader } from '../components/SectionHeader.tsx'
import { DemoRow } from '../components/DemoLayout.tsx'

export function FeedbackPage() {
  return (
    <Stack gap={6}>
      <Stack gap={1}>
        <Text variant="h2">Feedback</Text>
        <Text variant="body1" color={colors.gray[500]}>상태와 피드백을 전달하는 컴포넌트입니다.</Text>
      </Stack>

      <Card as="section">
        <SectionHeader>Badge</SectionHeader>
        <Stack gap={4}>
          <Stack gap={2}>
            <Text variant="label" color={colors.gray[500]}>Variants</Text>
            <Stack direction="row" gap={2} wrap align="center">
              <Badge variant="primary">Primary</Badge>
              <Badge variant="secondary">Secondary</Badge>
              <Badge variant="success">Success</Badge>
              <Badge variant="warning">Warning</Badge>
              <Badge variant="danger">Danger</Badge>
              <Badge variant="ghost">Ghost</Badge>
            </Stack>
          </Stack>
          <Stack gap={2}>
            <Text variant="label" color={colors.gray[500]}>Sizes</Text>
            <Stack direction="row" gap={2} align="center">
              <Badge size="sm">Small</Badge>
              <Badge size="md">Medium</Badge>
            </Stack>
          </Stack>
        </Stack>
      </Card>

      <Card as="section">
        <SectionHeader>Alert</SectionHeader>
        <Stack gap={3}>
          <Alert variant="info" title="안내">배포가 진행 중입니다. 잠시 후 다시 시도해 주세요.</Alert>
          <Alert variant="success" title="완료">변경 사항이 저장되었습니다.</Alert>
          <Alert variant="warning" title="주의">저장하지 않은 내용이 있습니다. 페이지를 떠나면 사라집니다.</Alert>
          <Alert variant="danger" title="오류">요청을 처리할 수 없습니다. 잠시 후 다시 시도해 주세요.</Alert>
        </Stack>
      </Card>

      <Card as="section">
        <SectionHeader>Spinner</SectionHeader>
        <DemoRow gap={4}>
          <Stack gap={2} align="center">
            <Spinner size="sm" />
            <Text variant="caption" color={colors.gray[500]}>sm</Text>
          </Stack>
          <Stack gap={2} align="center">
            <Spinner size="md" />
            <Text variant="caption" color={colors.gray[500]}>md</Text>
          </Stack>
          <Stack gap={2} align="center">
            <Spinner size="lg" />
            <Text variant="caption" color={colors.gray[500]}>lg</Text>
          </Stack>
          <Stack gap={2} align="center">
            <Spinner size="md" color={colors.green[600]} />
            <Text variant="caption" color={colors.gray[500]}>custom</Text>
          </Stack>
        </DemoRow>
      </Card>

      <Card as="section">
        <SectionHeader>Progress</SectionHeader>
        <Stack gap={5}>
          <Stack gap={3}>
            <Text variant="label" color={colors.gray[500]}>Determinate</Text>
            <Stack gap={2}>
              <Progress value={25} />
              <Progress value={60} />
              <Progress value={90} />
              <Progress value={100} color={colors.green[600]} />
            </Stack>
          </Stack>
          <Stack gap={2}>
            <Text variant="label" color={colors.gray[500]}>Sizes</Text>
            <Stack gap={2}>
              <Progress value={50} size="sm" />
              <Progress value={50} size="md" />
              <Progress value={50} size="lg" />
            </Stack>
          </Stack>
          <Stack gap={2}>
            <Text variant="label" color={colors.gray[500]}>Indeterminate</Text>
            <Progress variant="indeterminate" />
          </Stack>
        </Stack>
      </Card>

      <Card as="section">
        <SectionHeader>Skeleton</SectionHeader>
        <Stack gap={6}>
          <Stack gap={2}>
            <Text variant="label" color={colors.gray[500]}>Text</Text>
            <Skeleton count={3} />
            <Skeleton width="60%" />
          </Stack>
          <Stack gap={2}>
            <Text variant="label" color={colors.gray[500]}>Card</Text>
            <Stack direction="row" gap={3}>
              <Skeleton variant="circular" width={40} height={40} />
              <Stack gap={2} style={{ flex: 1 }}>
                <Skeleton width="40%" />
                <Skeleton />
                <Skeleton width="70%" />
              </Stack>
            </Stack>
          </Stack>
          <Stack gap={2}>
            <Text variant="label" color={colors.gray[500]}>Rectangular</Text>
            <Skeleton variant="rectangular" height={120} />
          </Stack>
        </Stack>
      </Card>

      <Card as="section">
        <SectionHeader>Avatar</SectionHeader>
        <Stack gap={4}>
          <Stack gap={2}>
            <Text variant="label" color={colors.gray[500]}>Sizes</Text>
            <Stack direction="row" gap={3} align="center">
              <Avatar size="xs" name="김민준" />
              <Avatar size="sm" name="이서연" />
              <Avatar size="md" name="박지호" />
              <Avatar size="lg" name="최수아" />
              <Avatar size="xl" name="정태양" />
            </Stack>
          </Stack>
          <Stack gap={2}>
            <Text variant="label" color={colors.gray[500]}>Fallback</Text>
            <Stack direction="row" gap={3} align="center">
              <Avatar size="md" name="Logan Jo" />
              <Avatar size="md" name="Admin" />
              <Avatar size="md" />
            </Stack>
          </Stack>
        </Stack>
      </Card>
    </Stack>
  )
}
