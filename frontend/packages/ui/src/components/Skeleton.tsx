import styled from '@emotion/styled'
import { keyframes } from '@emotion/react'
import { colors, radii } from '../tokens/index.ts'

type SkeletonVariant = 'text' | 'circular' | 'rectangular'

export interface SkeletonProps {
  variant?: SkeletonVariant
  width?: string | number
  height?: string | number
  count?: number
  className?: string
}

const shimmer = keyframes`
  0% { background-position: -200% 0; }
  100% { background-position: 200% 0; }
`

const CUSTOM_PROPS = new Set(['variant', 'w', 'h'])

const SkeletonEl = styled('span', {
  shouldForwardProp: (prop) => !CUSTOM_PROPS.has(prop as string),
})<{ variant: SkeletonVariant; w?: string | number; h?: string | number }>(({ variant, w, h }) => ({
  display: 'block',
  background: `linear-gradient(90deg, ${colors.gray[100]} 25%, ${colors.gray[200]} 50%, ${colors.gray[100]} 75%)`,
  backgroundSize: '200% 100%',
  animation: `${shimmer} 1.5s ease-in-out infinite`,
  borderRadius: variant === 'circular' ? '50%' : variant === 'text' ? radii.sm : radii.md,
  width: w !== undefined ? (typeof w === 'number' ? `${w}px` : w) : variant === 'text' ? '100%' : undefined,
  height: h !== undefined ? (typeof h === 'number' ? `${h}px` : h) : variant === 'text' ? '1em' : undefined,
  '& + &': { marginTop: '6px' },
}))

export function Skeleton({ variant = 'text', width, height, count = 1, className }: SkeletonProps) {
  if (count === 1) {
    return <SkeletonEl variant={variant} w={width} h={height} className={className} />
  }
  return (
    <>
      {Array.from({ length: count }).map((_, i) => (
        <SkeletonEl key={i} variant={variant} w={width} h={height} />
      ))}
    </>
  )
}
