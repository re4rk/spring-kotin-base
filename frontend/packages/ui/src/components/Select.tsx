import styled from '@emotion/styled'
import type { SelectHTMLAttributes, ReactNode } from 'react'
import { colors, spacing, typography, radii } from '../tokens/index.ts'

type SelectSize = 'sm' | 'md' | 'lg'

export interface SelectProps extends SelectHTMLAttributes<HTMLSelectElement> {
  label?: string
  error?: string
  helperText?: string
  selectSize?: SelectSize
  fullWidth?: boolean
  children?: ReactNode
}

// stroke color matches new gray[400] = #B0B8C1
const ARROW_SVG = `url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 12 12'%3E%3Cpath d='M2 4l4 4 4-4' stroke='%23B0B8C1' stroke-width='1.5' fill='none' stroke-linecap='round' stroke-linejoin='round'/%3E%3C/svg%3E")`

const sizeMap = {
  sm: { padding: `0 ${spacing[8]} 0 ${spacing[3]}`, fontSize: typography.fontSize.sm, height: '36px' },
  md: { padding: `0 ${spacing[8]} 0 ${spacing[4]}`, fontSize: typography.fontSize.sm, height: '44px' },
  lg: { padding: `0 ${spacing[10]} 0 ${spacing[4]}`, fontSize: typography.fontSize.base, height: '52px' },
}

const Wrapper = styled('div', {
  shouldForwardProp: (prop) => prop !== 'fullWidth',
})<{ fullWidth: boolean }>(({ fullWidth }) => ({
  display: 'inline-flex',
  flexDirection: 'column' as const,
  gap: spacing[1],
  width: fullWidth ? '100%' : undefined,
}))

const Label = styled.label({
  fontSize: typography.fontSize.sm,
  fontWeight: typography.fontWeight.semibold,
  color: colors.gray[700],
  fontFamily: typography.fontFamily.sans,
})

const CUSTOM_PROPS = new Set(['selectSize', 'hasError'])

const StyledSelect = styled('select', {
  shouldForwardProp: (prop) => !CUSTOM_PROPS.has(prop as string),
})<{ selectSize: SelectSize; hasError: boolean }>(({ selectSize, hasError }) => ({
  fontFamily: typography.fontFamily.sans,
  fontWeight: typography.fontWeight.medium,
  borderRadius: radii.lg,
  border: `1.5px solid ${hasError ? colors.red[500] : 'transparent'}`,
  outline: 'none',
  width: '100%',
  color: colors.gray[900],
  background: `${hasError ? colors.red[50] : colors.gray[100]} ${ARROW_SVG} no-repeat right ${spacing[3]} center`,
  appearance: 'none',
  cursor: 'pointer',
  boxSizing: 'border-box' as const,
  transition: 'background 120ms, border-color 120ms, box-shadow 120ms',
  '&:focus': {
    background: `#fff ${ARROW_SVG} no-repeat right ${spacing[3]} center`,
    borderColor: hasError ? colors.red[500] : colors.primary[500],
    boxShadow: hasError ? `0 0 0 3px ${colors.red[100]}` : `0 0 0 3px ${colors.primary[100]}`,
  },
  '&:disabled': {
    opacity: 0.5,
    cursor: 'not-allowed',
  },
  ...sizeMap[selectSize],
}))

const HelperText = styled('p', {
  shouldForwardProp: (prop) => prop !== 'isError',
})<{ isError: boolean }>(({ isError }) => ({
  margin: 0,
  fontSize: typography.fontSize.xs,
  color: isError ? colors.red[600] : colors.gray[500],
  fontFamily: typography.fontFamily.sans,
}))

export function Select({
  label,
  error,
  helperText,
  selectSize = 'md',
  fullWidth = false,
  id,
  children,
  ...props
}: SelectProps) {
  const selectId = id ?? (label ? label.toLowerCase().replace(/\s+/g, '-') : undefined)
  const hint = error ?? helperText

  return (
    <Wrapper fullWidth={fullWidth}>
      {label && <Label htmlFor={selectId}>{label}</Label>}
      <StyledSelect selectSize={selectSize} hasError={!!error} id={selectId} {...props}>
        {children}
      </StyledSelect>
      {hint && <HelperText isError={!!error}>{hint}</HelperText>}
    </Wrapper>
  )
}
