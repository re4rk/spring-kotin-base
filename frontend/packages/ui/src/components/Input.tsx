import styled from '@emotion/styled'
import type { InputHTMLAttributes } from 'react'
import { colors, spacing, typography, radii } from '../tokens/index.ts'

type InputSize = 'sm' | 'md' | 'lg'

export interface InputProps extends InputHTMLAttributes<HTMLInputElement> {
  label?: string
  error?: string
  helperText?: string
  inputSize?: InputSize
  fullWidth?: boolean
}

const sizeMap = {
  sm: { padding: `0 ${spacing[3]}`, fontSize: typography.fontSize.sm, height: '36px' },
  md: { padding: `0 ${spacing[4]}`, fontSize: typography.fontSize.sm, height: '44px' },
  lg: { padding: `0 ${spacing[4]}`, fontSize: typography.fontSize.base, height: '52px' },
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

const CUSTOM_PROPS = new Set(['inputSize', 'hasError'])

const StyledInput = styled('input', {
  shouldForwardProp: (prop) => !CUSTOM_PROPS.has(prop as string),
})<{ inputSize: InputSize; hasError: boolean }>(({ inputSize, hasError }) => ({
  fontFamily: typography.fontFamily.sans,
  fontWeight: typography.fontWeight.medium,
  borderRadius: radii.lg,
  border: `1.5px solid ${hasError ? colors.red[500] : 'transparent'}`,
  outline: 'none',
  width: '100%',
  color: colors.gray[900],
  background: hasError ? colors.red[50] : colors.gray[100],
  boxSizing: 'border-box' as const,
  transition: 'background 120ms, border-color 120ms, box-shadow 120ms',
  '&::placeholder': { color: colors.gray[400], fontWeight: typography.fontWeight.normal },
  '&:focus': {
    background: '#fff',
    borderColor: hasError ? colors.red[500] : colors.primary[500],
    boxShadow: hasError
      ? `0 0 0 3px ${colors.red[100]}`
      : `0 0 0 3px ${colors.primary[100]}`,
  },
  '&:disabled': {
    opacity: 0.5,
    cursor: 'not-allowed',
  },
  ...sizeMap[inputSize],
}))

const HelperText = styled('p', {
  shouldForwardProp: (prop) => prop !== 'isError',
})<{ isError: boolean }>(({ isError }) => ({
  margin: 0,
  fontSize: typography.fontSize.xs,
  color: isError ? colors.red[600] : colors.gray[500],
  fontFamily: typography.fontFamily.sans,
}))

export function Input({
  label,
  error,
  helperText,
  inputSize = 'md',
  fullWidth = false,
  id,
  ...props
}: InputProps) {
  const inputId = id ?? (label ? label.toLowerCase().replace(/\s+/g, '-') : undefined)
  const hint = error ?? helperText

  return (
    <Wrapper fullWidth={fullWidth}>
      {label && <Label htmlFor={inputId}>{label}</Label>}
      <StyledInput inputSize={inputSize} hasError={!!error} id={inputId} {...props} />
      {hint && <HelperText isError={!!error}>{hint}</HelperText>}
    </Wrapper>
  )
}
