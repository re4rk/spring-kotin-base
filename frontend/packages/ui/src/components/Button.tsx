import styled from '@emotion/styled'
import type { ButtonHTMLAttributes, ReactNode } from 'react'
import { colors, spacing, typography, radii } from '../tokens/index.ts'

type ButtonVariant = 'primary' | 'secondary' | 'ghost' | 'danger'
type ButtonSize = 'sm' | 'md' | 'lg'

export interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: ButtonVariant
  size?: ButtonSize
  fullWidth?: boolean
  loading?: boolean
  children?: ReactNode
}

const CUSTOM_PROPS = new Set(['variant', 'size', 'fullWidth'])

const StyledButton = styled('button', {
  shouldForwardProp: (prop) => !CUSTOM_PROPS.has(prop as string),
})<{ variant: ButtonVariant; size: ButtonSize; fullWidth: boolean }>(
  ({ variant, size, fullWidth }) => {
    const sizeMap = {
      sm: { padding: `0 ${spacing[4]}`, fontSize: typography.fontSize.sm, height: '36px' },
      md: { padding: `0 ${spacing[5]}`, fontSize: typography.fontSize.sm, height: '44px' },
      lg: { padding: `0 ${spacing[6]}`, fontSize: typography.fontSize.base, height: '52px' },
    }

    const variantMap = {
      primary: {
        background: colors.primary[500],
        color: '#fff',
        border: 'none',
        '&:hover:not(:disabled)': { background: colors.primary[600] },
        '&:active:not(:disabled)': { background: colors.primary[700] },
      },
      secondary: {
        background: colors.gray[100],
        color: colors.gray[800],
        border: 'none',
        '&:hover:not(:disabled)': { background: colors.gray[200] },
        '&:active:not(:disabled)': { background: colors.gray[200] },
      },
      ghost: {
        background: 'transparent',
        color: colors.gray[600],
        border: 'none',
        '&:hover:not(:disabled)': { background: colors.gray[100] },
        '&:active:not(:disabled)': { background: colors.gray[200] },
      },
      danger: {
        background: colors.red[500],
        color: '#fff',
        border: 'none',
        '&:hover:not(:disabled)': { background: colors.red[600] },
        '&:active:not(:disabled)': { background: colors.red[700] },
      },
    }

    return {
      display: 'inline-flex',
      alignItems: 'center',
      justifyContent: 'center',
      gap: spacing[2],
      fontFamily: typography.fontFamily.sans,
      fontWeight: typography.fontWeight.semibold,
      letterSpacing: '-0.01em',
      borderRadius: radii.lg,
      cursor: 'pointer',
      transition: 'background 120ms',
      outline: 'none',
      width: fullWidth ? '100%' : undefined,
      whiteSpace: 'nowrap' as const,
      boxSizing: 'border-box' as const,
      '&:disabled': { opacity: 0.4, cursor: 'not-allowed' },
      '&:focus-visible': {
        outline: `2px solid ${colors.primary[500]}`,
        outlineOffset: '2px',
      },
      ...sizeMap[size],
      ...variantMap[variant],
    }
  }
)

export function Button({
  variant = 'primary',
  size = 'md',
  fullWidth = false,
  loading = false,
  disabled,
  children,
  ...props
}: ButtonProps) {
  return (
    <StyledButton
      variant={variant}
      size={size}
      fullWidth={fullWidth}
      disabled={disabled || loading}
      {...props}
    >
      {loading ? '···' : children}
    </StyledButton>
  )
}
