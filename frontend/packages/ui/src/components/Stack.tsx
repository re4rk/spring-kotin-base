import styled from '@emotion/styled'
import type { CSSProperties, ReactNode, ElementType } from 'react'
import { spacing } from '../tokens/index.ts'
import type { SpacingKey } from '../tokens/index.ts'

export interface StackProps {
  direction?: 'row' | 'column'
  gap?: SpacingKey
  align?: CSSProperties['alignItems']
  justify?: CSSProperties['justifyContent']
  wrap?: boolean
  fullWidth?: boolean
  fullHeight?: boolean
  as?: ElementType
  className?: string
  children?: ReactNode
}

const CUSTOM_PROPS = new Set(['direction', 'gap', 'align', 'justify', 'wrap', 'fullWidth', 'fullHeight'])

type StyledProps = Omit<StackProps, 'as' | 'children' | 'className'>

const StyledStack = styled('div', {
  shouldForwardProp: (prop) => !CUSTOM_PROPS.has(prop as string),
})<StyledProps>(({ direction = 'column', gap = 4, align, justify, wrap, fullWidth, fullHeight }) => ({
  display: 'flex',
  flexDirection: direction,
  gap: spacing[gap],
  alignItems: align,
  justifyContent: justify,
  flexWrap: wrap ? 'wrap' : undefined,
  width: fullWidth ? '100%' : undefined,
  height: fullHeight ? '100%' : undefined,
}))

export function Stack({ as, children, ...props }: StackProps) {
  return (
    <StyledStack as={as} {...props}>
      {children}
    </StyledStack>
  )
}
