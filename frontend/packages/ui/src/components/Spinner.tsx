import styled from '@emotion/styled'
import { keyframes } from '@emotion/react'
import type { HTMLAttributes } from 'react'
import { colors } from '../tokens/index.ts'

type SpinnerSize = 'sm' | 'md' | 'lg'

export interface SpinnerProps extends HTMLAttributes<HTMLSpanElement> {
  size?: SpinnerSize
  color?: string
}

const spin = keyframes`
  to { transform: rotate(360deg); }
`

const sizeMap = { sm: '16px', md: '24px', lg: '32px' }

const CUSTOM_PROPS = new Set(['size', 'spinnerColor'])

const StyledSpinner = styled('span', {
  shouldForwardProp: (prop) => !CUSTOM_PROPS.has(prop as string),
})<{ size: SpinnerSize; spinnerColor: string }>(({ size, spinnerColor }) => ({
  display: 'inline-block',
  width: sizeMap[size],
  height: sizeMap[size],
  borderRadius: '50%',
  border: `2px solid ${colors.gray[200]}`,
  borderTopColor: spinnerColor,
  animation: `${spin} 0.65s linear infinite`,
  flexShrink: 0,
}))

export function Spinner({ size = 'md', color, ...htmlProps }: SpinnerProps) {
  return (
    <StyledSpinner
      size={size}
      spinnerColor={color ?? colors.primary[600]}
      role="status"
      aria-label="로딩 중"
      {...htmlProps}
    />
  )
}
