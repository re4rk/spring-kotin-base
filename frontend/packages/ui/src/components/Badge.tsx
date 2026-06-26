import styled from '@emotion/styled'
import type { HTMLAttributes } from 'react'
import { colors, typography, radii, spacing } from '../tokens/index.ts'

type BadgeVariant = 'primary' | 'secondary' | 'success' | 'warning' | 'danger' | 'ghost'
type BadgeSize = 'sm' | 'md'

export interface BadgeProps extends HTMLAttributes<HTMLSpanElement> {
  variant?: BadgeVariant
  size?: BadgeSize
}

const variantMap: Record<BadgeVariant, object> = {
  primary: { background: colors.primary[100], color: colors.primary[700] },
  secondary: { background: colors.gray[100], color: colors.gray[700] },
  success: { background: colors.green[100], color: colors.green[700] },
  warning: { background: colors.yellow[100], color: colors.yellow[600] },
  danger: { background: colors.red[100], color: colors.red[700] },
  ghost: { background: 'transparent', color: colors.gray[600], border: `1px solid ${colors.gray[300]}` },
}

const sizeMap = {
  sm: { fontSize: typography.fontSize.xs, padding: `2px ${spacing[2]}`, height: '20px' },
  md: { fontSize: typography.fontSize.xs, padding: `${spacing[1]} ${spacing[2]}`, height: '24px' },
}

const CUSTOM_PROPS = new Set(['variant', 'size'])

const StyledBadge = styled('span', {
  shouldForwardProp: (prop) => !CUSTOM_PROPS.has(prop as string),
})<{ variant: BadgeVariant; size: BadgeSize }>(({ variant, size }) => ({
  display: 'inline-flex',
  alignItems: 'center',
  fontFamily: typography.fontFamily.sans,
  fontWeight: typography.fontWeight.medium,
  borderRadius: radii.full,
  whiteSpace: 'nowrap' as const,
  ...sizeMap[size],
  ...variantMap[variant],
}))

export function Badge({ variant = 'secondary', size = 'md', children, ...htmlProps }: BadgeProps) {
  return (
    <StyledBadge variant={variant} size={size} {...htmlProps}>
      {children}
    </StyledBadge>
  )
}
