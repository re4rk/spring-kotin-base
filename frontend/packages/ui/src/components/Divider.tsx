import styled from '@emotion/styled'
import type { HTMLAttributes } from 'react'
import { colors, spacing } from '../tokens/index.ts'
import type { SpacingKey } from '../tokens/index.ts'

export interface DividerProps extends HTMLAttributes<HTMLHRElement> {
  gap?: SpacingKey
}

export const Divider = styled('hr', {
  shouldForwardProp: (prop) => prop !== 'gap',
})<DividerProps>(({ gap = 5 }) => ({
  border: 'none',
  borderTop: `1px solid ${colors.gray[200]}`,
  margin: `${spacing[gap]} 0`,
}))
