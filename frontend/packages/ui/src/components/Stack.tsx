import styled from '@emotion/styled'
import type { CSSProperties, ElementType, HTMLAttributes } from 'react'
import { spacing } from '../tokens/index.ts'
import type { SpacingKey } from '../tokens/index.ts'

export interface StackProps extends HTMLAttributes<HTMLDivElement> {
  direction?: 'row' | 'column'
  gap?: SpacingKey
  align?: CSSProperties['alignItems']
  justify?: CSSProperties['justifyContent']
  wrap?: boolean
  fullWidth?: boolean
  fullHeight?: boolean
  as?: ElementType
}

type StyledStackProps = {
  direction?: 'row' | 'column'
  gap?: SpacingKey
  align?: CSSProperties['alignItems']
  justify?: CSSProperties['justifyContent']
  wrap?: boolean
  fullWidth?: boolean
  fullHeight?: boolean
}

const CUSTOM_PROPS = new Set(['direction', 'gap', 'align', 'justify', 'wrap', 'fullWidth', 'fullHeight'])

const StyledStack = styled('div', {
  shouldForwardProp: (prop) => !CUSTOM_PROPS.has(prop as string),
})<StyledStackProps>(({ direction = 'column', gap = 4, align, justify, wrap, fullWidth, fullHeight }) => ({
  display: 'flex',
  flexDirection: direction,
  gap: spacing[gap],
  alignItems: align,
  justifyContent: justify,
  flexWrap: wrap ? 'wrap' : undefined,
  width: fullWidth ? '100%' : undefined,
  height: fullHeight ? '100%' : undefined,
}))

export function Stack({
  as,
  children,
  direction,
  gap,
  align,
  justify,
  wrap,
  fullWidth,
  fullHeight,
  ...htmlProps
}: StackProps) {
  return (
    <StyledStack
      as={as}
      direction={direction}
      gap={gap}
      align={align}
      justify={justify}
      wrap={wrap}
      fullWidth={fullWidth}
      fullHeight={fullHeight}
      {...htmlProps}
    >
      {children}
    </StyledStack>
  )
}
