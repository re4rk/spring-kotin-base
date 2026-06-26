import styled from '@emotion/styled'
import type { InputHTMLAttributes, ReactNode } from 'react'
import { colors, typography, radii, spacing } from '../tokens/index.ts'

export interface CheckboxProps extends Omit<InputHTMLAttributes<HTMLInputElement>, 'type' | 'size'> {
  label?: ReactNode
  error?: string
  helperText?: string
}

const CHECK_SVG = `url("data:image/svg+xml,%3Csvg viewBox='0 0 12 12' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath d='M2 6l3 3 5-5' stroke='white' stroke-width='1.5' fill='none' stroke-linecap='round' stroke-linejoin='round'/%3E%3C/svg%3E")`

const Wrapper = styled.div({
  display: 'inline-flex',
  flexDirection: 'column',
  gap: spacing[1],
})

const Row = styled.label({
  display: 'inline-flex',
  alignItems: 'center',
  gap: spacing[2],
  cursor: 'pointer',
  fontFamily: typography.fontFamily.sans,
  fontSize: typography.fontSize.sm,
  color: colors.gray[700],
  '&:has(input:disabled)': {
    cursor: 'not-allowed',
    color: colors.gray[400],
  },
})

const StyledCheckbox = styled.input({
  appearance: 'none',
  width: '16px',
  height: '16px',
  borderRadius: radii.sm,
  border: `1.5px solid ${colors.gray[300]}`,
  background: '#fff',
  cursor: 'pointer',
  flexShrink: 0,
  transition: 'background 150ms, border-color 150ms',
  '&:checked': {
    background: colors.primary[600],
    borderColor: colors.primary[600],
    backgroundImage: CHECK_SVG,
    backgroundRepeat: 'no-repeat',
    backgroundPosition: 'center',
  },
  '&:indeterminate': {
    background: colors.primary[600],
    borderColor: colors.primary[600],
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

const HelperText = styled('p', {
  shouldForwardProp: (prop) => prop !== 'isError',
})<{ isError: boolean }>(({ isError }) => ({
  margin: 0,
  fontSize: typography.fontSize.xs,
  color: isError ? colors.red[600] : colors.gray[500],
  fontFamily: typography.fontFamily.sans,
  paddingLeft: '24px',
}))

export function Checkbox({ label, error, helperText, id, ...props }: CheckboxProps) {
  const checkId = id ?? (typeof label === 'string' ? label.toLowerCase().replace(/\s+/g, '-') : undefined)
  const hint = error ?? helperText

  return (
    <Wrapper>
      <Row htmlFor={checkId}>
        <StyledCheckbox type="checkbox" id={checkId} {...props} />
        {label}
      </Row>
      {hint && <HelperText isError={!!error}>{hint}</HelperText>}
    </Wrapper>
  )
}
