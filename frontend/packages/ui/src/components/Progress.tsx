import styled from '@emotion/styled'
import { keyframes } from '@emotion/react'
import type { HTMLAttributes } from 'react'
import { colors, radii } from '../tokens/index.ts'

type ProgressSize = 'sm' | 'md' | 'lg'

export interface ProgressProps extends HTMLAttributes<HTMLDivElement> {
  value?: number
  max?: number
  size?: ProgressSize
  color?: string
  variant?: 'determinate' | 'indeterminate'
}

const indeterminate = keyframes`
  0%   { left: -40%; width: 40%; }
  60%  { left: 100%; width: 40%; }
  100% { left: 100%; width: 40%; }
`

const sizeMap: Record<ProgressSize, string> = { sm: '4px', md: '8px', lg: '12px' }

const CUSTOM_PROPS = new Set(['size'])

const Track = styled('div', {
  shouldForwardProp: (prop) => !CUSTOM_PROPS.has(prop as string),
})<{ size: ProgressSize }>(({ size }) => ({
  position: 'relative',
  width: '100%',
  height: sizeMap[size],
  background: colors.gray[200],
  borderRadius: radii.full,
  overflow: 'hidden',
}))

const FILL_PROPS = new Set(['fillColor', 'percent', 'isIndeterminate'])

const Fill = styled('div', {
  shouldForwardProp: (prop) => !FILL_PROPS.has(prop as string),
})<{ fillColor: string; percent: number; isIndeterminate: boolean }>(
  ({ fillColor, percent, isIndeterminate }) => ({
    height: '100%',
    background: fillColor,
    borderRadius: radii.full,
    ...(isIndeterminate
      ? {
          position: 'absolute',
          width: '40%',
          animation: `${indeterminate} 1.4s ease-in-out infinite`,
        }
      : {
          width: `${percent}%`,
          transition: 'width 300ms ease',
        }),
  })
)

export function Progress({
  value = 0,
  max = 100,
  size = 'md',
  color,
  variant = 'determinate',
  ...htmlProps
}: ProgressProps) {
  const percent = Math.min(100, Math.max(0, (value / max) * 100))
  const isIndeterminate = variant === 'indeterminate'

  return (
    <Track
      size={size}
      role="progressbar"
      aria-valuenow={isIndeterminate ? undefined : value}
      aria-valuemin={0}
      aria-valuemax={max}
      {...htmlProps}
    >
      <Fill
        fillColor={color ?? colors.primary[600]}
        percent={percent}
        isIndeterminate={isIndeterminate}
      />
    </Track>
  )
}
