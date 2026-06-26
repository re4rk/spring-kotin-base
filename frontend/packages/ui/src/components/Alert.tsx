import styled from '@emotion/styled'
import type { HTMLAttributes, ReactNode } from 'react'
import { colors, typography, radii, spacing } from '../tokens/index.ts'

type AlertVariant = 'info' | 'success' | 'warning' | 'danger'

export interface AlertProps extends HTMLAttributes<HTMLDivElement> {
  variant?: AlertVariant
  title?: string
  children?: ReactNode
}

const variantMap = {
  info: {
    background: colors.primary[50],
    border: colors.primary[200],
    titleColor: colors.primary[800],
    bodyColor: colors.primary[700],
  },
  success: {
    background: colors.green[50],
    border: colors.green[100],
    titleColor: colors.green[700],
    bodyColor: colors.green[600],
  },
  warning: {
    background: colors.yellow[50],
    border: colors.yellow[100],
    titleColor: '#92400E',
    bodyColor: '#A16207',
  },
  danger: {
    background: colors.red[50],
    border: colors.red[100],
    titleColor: colors.red[700],
    bodyColor: colors.red[600],
  },
}

const CUSTOM_PROPS = new Set(['variant'])

const StyledAlert = styled('div', {
  shouldForwardProp: (prop) => !CUSTOM_PROPS.has(prop as string),
})<{ variant: AlertVariant }>(({ variant }) => {
  const v = variantMap[variant]
  return {
    display: 'flex',
    flexDirection: 'column' as const,
    gap: spacing[1],
    padding: `${spacing[3]} ${spacing[4]}`,
    borderRadius: radii.lg,
    border: `1px solid ${v.border}`,
    background: v.background,
    fontFamily: typography.fontFamily.sans,
  }
})

const AlertTitle = styled('p', {
  shouldForwardProp: (prop) => prop !== 'variant',
})<{ variant: AlertVariant }>(({ variant }) => ({
  margin: 0,
  fontSize: typography.fontSize.sm,
  fontWeight: typography.fontWeight.semibold,
  color: variantMap[variant].titleColor,
}))

const AlertBody = styled('p', {
  shouldForwardProp: (prop) => prop !== 'variant',
})<{ variant: AlertVariant }>(({ variant }) => ({
  margin: 0,
  fontSize: typography.fontSize.sm,
  color: variantMap[variant].bodyColor,
}))

export function Alert({ variant = 'info', title, children, ...htmlProps }: AlertProps) {
  return (
    <StyledAlert variant={variant} role="alert" {...htmlProps}>
      {title && <AlertTitle variant={variant}>{title}</AlertTitle>}
      {children && <AlertBody variant={variant}>{children}</AlertBody>}
    </StyledAlert>
  )
}
