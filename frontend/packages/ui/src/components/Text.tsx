import styled from '@emotion/styled'
import type { ReactNode, ElementType } from 'react'
import { colors, typography } from '../tokens/index.ts'

type TextVariant = 'h1' | 'h2' | 'h3' | 'h4' | 'body1' | 'body2' | 'caption' | 'label'

export interface TextProps {
  variant?: TextVariant
  as?: ElementType
  color?: string
  className?: string
  children?: ReactNode
}

const variantStyles: Record<TextVariant, object> = {
  h1: { fontSize: typography.fontSize['4xl'], fontWeight: typography.fontWeight.bold, lineHeight: typography.lineHeight.tight },
  h2: { fontSize: typography.fontSize['3xl'], fontWeight: typography.fontWeight.bold, lineHeight: typography.lineHeight.tight },
  h3: { fontSize: typography.fontSize['2xl'], fontWeight: typography.fontWeight.semibold, lineHeight: typography.lineHeight.snug },
  h4: { fontSize: typography.fontSize.xl, fontWeight: typography.fontWeight.semibold, lineHeight: typography.lineHeight.snug },
  body1: { fontSize: typography.fontSize.base, fontWeight: typography.fontWeight.normal, lineHeight: typography.lineHeight.normal },
  body2: { fontSize: typography.fontSize.sm, fontWeight: typography.fontWeight.normal, lineHeight: typography.lineHeight.normal },
  caption: { fontSize: typography.fontSize.xs, fontWeight: typography.fontWeight.normal, lineHeight: typography.lineHeight.normal },
  label: { fontSize: typography.fontSize.sm, fontWeight: typography.fontWeight.medium, lineHeight: typography.lineHeight.snug },
}

const defaultElement: Record<TextVariant, ElementType> = {
  h1: 'h1', h2: 'h2', h3: 'h3', h4: 'h4',
  body1: 'p', body2: 'p',
  caption: 'span', label: 'label',
}

const CUSTOM_PROPS = new Set(['variant', 'textColor'])

const StyledText = styled('p', {
  shouldForwardProp: (prop) => !CUSTOM_PROPS.has(prop as string),
})<{ variant: TextVariant; textColor?: string }>(({ variant, textColor }) => ({
  margin: 0,
  padding: 0,
  fontFamily: typography.fontFamily.sans,
  color: textColor ?? colors.gray[900],
  ...variantStyles[variant],
}))

export function Text({ variant = 'body1', as, color, children, className }: TextProps) {
  return (
    <StyledText
      as={as ?? defaultElement[variant]}
      variant={variant}
      textColor={color}
      className={className}
    >
      {children}
    </StyledText>
  )
}
