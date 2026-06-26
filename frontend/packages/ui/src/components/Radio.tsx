import styled from '@emotion/styled'
import type { InputHTMLAttributes, ReactNode } from 'react'
import { colors, spacing, typography } from '../tokens/index.ts'

export interface RadioProps extends Omit<InputHTMLAttributes<HTMLInputElement>, 'type'> {
  label?: ReactNode
}

export interface RadioGroupProps {
  label?: string
  error?: string
  helperText?: string
  direction?: 'row' | 'column'
  children?: ReactNode
}

const Row = styled.label({
  display: 'inline-flex',
  alignItems: 'center',
  gap: spacing[2],
  cursor: 'pointer',
  fontFamily: typography.fontFamily.sans,
  fontSize: typography.fontSize.sm,
  color: colors.gray[700],
  userSelect: 'none',
  '&:has(input:disabled)': { cursor: 'not-allowed', color: colors.gray[400] },
})

const StyledRadio = styled.input({
  appearance: 'none',
  width: '16px',
  height: '16px',
  borderRadius: '50%',
  border: `1.5px solid ${colors.gray[300]}`,
  background: '#fff',
  cursor: 'pointer',
  flexShrink: 0,
  transition: 'border-color 150ms',
  '&:checked': {
    borderColor: colors.primary[600],
    borderWidth: '5px',
  },
  '&:focus-visible': {
    outline: `2px solid ${colors.primary[500]}`,
    outlineOffset: '2px',
  },
  '&:disabled': {
    background: colors.gray[100],
    borderColor: colors.gray[200],
    cursor: 'not-allowed',
  },
})

export function Radio({ label, ...props }: RadioProps) {
  return (
    <Row>
      <StyledRadio type="radio" {...props} />
      {label}
    </Row>
  )
}

const GroupWrapper = styled.div({
  display: 'flex',
  flexDirection: 'column',
  gap: spacing[1],
})

const GroupLabel = styled.p({
  margin: 0,
  fontSize: typography.fontSize.sm,
  fontWeight: typography.fontWeight.medium,
  color: colors.gray[700],
  fontFamily: typography.fontFamily.sans,
})

const RadioList = styled('div', {
  shouldForwardProp: (prop) => prop !== 'direction',
})<{ direction: 'row' | 'column' }>(({ direction }) => ({
  display: 'flex',
  flexDirection: direction,
  gap: direction === 'row' ? spacing[4] : spacing[2],
  flexWrap: 'wrap' as const,
}))

const HelperText = styled('p', {
  shouldForwardProp: (prop) => prop !== 'isError',
})<{ isError: boolean }>(({ isError }) => ({
  margin: 0,
  fontSize: typography.fontSize.xs,
  color: isError ? colors.red[600] : colors.gray[500],
  fontFamily: typography.fontFamily.sans,
}))

export function RadioGroup({
  label,
  error,
  helperText,
  direction = 'column',
  children,
}: RadioGroupProps) {
  const hint = error ?? helperText
  return (
    <GroupWrapper role="radiogroup" aria-label={label}>
      {label && <GroupLabel>{label}</GroupLabel>}
      <RadioList direction={direction}>{children}</RadioList>
      {hint && <HelperText isError={!!error}>{hint}</HelperText>}
    </GroupWrapper>
  )
}
