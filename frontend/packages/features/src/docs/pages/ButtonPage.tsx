import { Stack, Card, Button, Text, colors } from '@base/ui'
import { SectionHeader } from '../components/SectionHeader.tsx'

export function ButtonPage() {
  return (
    <Stack gap={6}>
      <Stack gap={1}>
        <Text variant="h2">Button</Text>
        <Text variant="body1" color={colors.gray[500]}>사용자 액션을 트리거하는 버튼 컴포넌트입니다.</Text>
      </Stack>

      <Card as="section">
        <SectionHeader>Variants</SectionHeader>
        <Stack direction="row" gap={3} wrap align="center">
          <Button variant="primary">Primary</Button>
          <Button variant="secondary">Secondary</Button>
          <Button variant="ghost">Ghost</Button>
          <Button variant="danger">Danger</Button>
        </Stack>
      </Card>

      <Card as="section">
        <SectionHeader>Sizes</SectionHeader>
        <Stack direction="row" gap={3} align="center" wrap>
          <Button size="sm">Small</Button>
          <Button size="md">Medium</Button>
          <Button size="lg">Large</Button>
        </Stack>
      </Card>

      <Card as="section">
        <SectionHeader>Full Width</SectionHeader>
        <Stack gap={2}>
          <Button fullWidth>Full Width Primary</Button>
          <Button fullWidth variant="secondary">Full Width Secondary</Button>
        </Stack>
      </Card>

      <Card as="section">
        <SectionHeader>States</SectionHeader>
        <Stack direction="row" gap={3} wrap>
          <Button disabled>Disabled</Button>
          <Button loading>Loading</Button>
          <Button variant="secondary" disabled>Secondary Disabled</Button>
          <Button variant="ghost" loading>Ghost Loading</Button>
          <Button variant="danger" loading>Danger Loading</Button>
        </Stack>
      </Card>
    </Stack>
  )
}
