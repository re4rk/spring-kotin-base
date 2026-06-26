import { Stack, Card, Text, colors } from '@base/ui'
import { SectionHeader } from '../components/SectionHeader.tsx'

export function TypographyPage() {
  return (
    <Stack gap={6}>
      <Stack gap={1}>
        <Text variant="h2">Typography</Text>
        <Text variant="body1" color={colors.gray[500]}>텍스트 스타일과 계층 구조를 정의합니다.</Text>
      </Stack>

      <Card as="section">
        <SectionHeader>Scale</SectionHeader>
        <Stack gap={5}>
          <Stack gap={1}>
            <Text variant="caption" color={colors.gray[400]}>h1</Text>
            <Text variant="h1">앞으로 나아갈 용기</Text>
          </Stack>
          <Stack gap={1}>
            <Text variant="caption" color={colors.gray[400]}>h2</Text>
            <Text variant="h2">앞으로 나아갈 용기</Text>
          </Stack>
          <Stack gap={1}>
            <Text variant="caption" color={colors.gray[400]}>h3</Text>
            <Text variant="h3">앞으로 나아갈 용기</Text>
          </Stack>
          <Stack gap={1}>
            <Text variant="caption" color={colors.gray[400]}>h4</Text>
            <Text variant="h4">앞으로 나아갈 용기</Text>
          </Stack>
          <Stack gap={1}>
            <Text variant="caption" color={colors.gray[400]}>body1 · 16px</Text>
            <Text variant="body1">복잡한 금융 서비스를 누구나 쉽게 이용할 수 있도록, 간결하고 직관적인 경험을 제공합니다.</Text>
          </Stack>
          <Stack gap={1}>
            <Text variant="caption" color={colors.gray[400]}>body2 · 14px</Text>
            <Text variant="body2">복잡한 금융 서비스를 누구나 쉽게 이용할 수 있도록, 간결하고 직관적인 경험을 제공합니다.</Text>
          </Stack>
          <Stack gap={1}>
            <Text variant="caption" color={colors.gray[400]}>caption · 12px</Text>
            <Text variant="caption">최근 거래 내역 · 2024년 12월 기준</Text>
          </Stack>
          <Stack gap={1}>
            <Text variant="caption" color={colors.gray[400]}>label · 14px semibold</Text>
            <Text variant="label">이메일 주소</Text>
          </Stack>
        </Stack>
      </Card>

      <Card as="section">
        <SectionHeader>Color</SectionHeader>
        <Stack gap={3}>
          <Text variant="body1" color={colors.gray[900]}>gray-900 · 기본 텍스트</Text>
          <Text variant="body1" color={colors.gray[700]}>gray-700 · 보조 텍스트</Text>
          <Text variant="body1" color={colors.gray[500]}>gray-500 · 설명 텍스트</Text>
          <Text variant="body1" color={colors.gray[400]}>gray-400 · 비활성 텍스트</Text>
          <Text variant="body1" color={colors.primary[500]}>primary-500 · 강조 텍스트</Text>
          <Text variant="body1" color={colors.red[500]}>red-500 · 에러 텍스트</Text>
          <Text variant="body1" color={colors.green[600]}>green-600 · 성공 텍스트</Text>
        </Stack>
      </Card>
    </Stack>
  )
}
