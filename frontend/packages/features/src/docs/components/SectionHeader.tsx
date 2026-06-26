import { Divider, Text } from '@base/ui'

export function SectionHeader({ children }: { children: string }) {
  return (
    <>
      <Text variant="h4">{children}</Text>
      <Divider />
    </>
  )
}
