import styled from '@emotion/styled'
import type { HTMLAttributes } from 'react'
import { colors, spacing } from '@base/ui'

export const Page = styled('div')<HTMLAttributes<HTMLDivElement>>({
  minHeight: '100vh',
  background: colors.gray[100],
  padding: `${spacing[10]} ${spacing[6]}`,
  textAlign: 'left',
  fontFamily: 'inherit',
})
