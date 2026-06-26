import styled from '@emotion/styled'
import { Stack, Card, Text, colors, radii } from '@base/ui'
import { SectionHeader } from '../components/SectionHeader.tsx'

const Swatch = styled('div', {
  shouldForwardProp: (prop) => prop !== 'bg',
})<{ bg: string }>(({ bg }) => ({
  width: '44px',
  height: '44px',
  borderRadius: radii.md,
  background: bg,
  border: `1px solid rgba(0,0,0,0.06)`,
  flexShrink: 0,
}))

const PALETTES = [
  { name: 'Primary', palette: colors.primary },
  { name: 'Gray', palette: colors.gray },
  { name: 'Red', palette: colors.red },
  { name: 'Green', palette: colors.green },
  { name: 'Yellow', palette: colors.yellow },
] as const

export function ColorsPage() {
  return (
    <Stack gap={6}>
      <Stack gap={1}>
        <Text variant="h2">Colors</Text>
        <Text variant="body1" color={colors.gray[500]}>디자인 시스템의 색상 팔레트입니다.</Text>
      </Stack>

      {PALETTES.map(({ name, palette }) => (
        <Card key={name} as="section">
          <SectionHeader>{name}</SectionHeader>
          <Stack direction="row" gap={3} wrap>
            {Object.entries(palette).map(([key, value]) => (
              <Stack key={key} gap={2} align="center">
                <Swatch bg={value} />
                <Stack gap={0} align="center">
                  <Text variant="caption" color={colors.gray[700]}>{key}</Text>
                  <Text variant="caption" color={colors.gray[400]}>{value}</Text>
                </Stack>
              </Stack>
            ))}
          </Stack>
        </Card>
      ))}
    </Stack>
  )
}
