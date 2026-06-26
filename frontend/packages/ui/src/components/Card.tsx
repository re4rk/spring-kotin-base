import styled from '@emotion/styled'
import type { HTMLAttributes } from 'react'
import { colors, radii, shadows, spacing } from '../tokens/index.ts'

export interface CardProps extends HTMLAttributes<HTMLElement> {
  as?: 'div' | 'section' | 'article' | 'aside'
  padding?: keyof typeof spacing
}

const StyledCard = styled('div', {
  shouldForwardProp: (prop) => !['padding'].includes(prop as string),
})<Pick<CardProps, 'padding'>>(({ padding = 8 }) => ({
  background: '#fff',
  borderRadius: radii['2xl'],
  padding: spacing[padding],
  boxShadow: shadows.sm,
  border: `1px solid ${colors.gray[200]}`,
}))

export function Card({ as = 'div', padding, children, ...htmlProps }: CardProps) {
  return (
    <StyledCard as={as} padding={padding} {...htmlProps}>
      {children}
    </StyledCard>
  )
}
