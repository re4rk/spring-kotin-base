import styled from '@emotion/styled'
import { useState } from 'react'
import { colors, typography, radii } from '../tokens/index.ts'

type AvatarSize = 'xs' | 'sm' | 'md' | 'lg' | 'xl'

export interface AvatarProps {
  src?: string
  alt?: string
  name?: string
  size?: AvatarSize
  className?: string
}

const sizeMap = {
  xs: { width: '24px', height: '24px', fontSize: typography.fontSize.xs },
  sm: { width: '32px', height: '32px', fontSize: typography.fontSize.xs },
  md: { width: '40px', height: '40px', fontSize: typography.fontSize.sm },
  lg: { width: '48px', height: '48px', fontSize: typography.fontSize.base },
  xl: { width: '64px', height: '64px', fontSize: typography.fontSize.lg },
}

const CUSTOM_PROPS = new Set(['size'])

const Wrapper = styled('span', {
  shouldForwardProp: (prop) => !CUSTOM_PROPS.has(prop as string),
})<{ size: AvatarSize }>(({ size }) => ({
  display: 'inline-flex',
  alignItems: 'center',
  justifyContent: 'center',
  borderRadius: radii.full,
  overflow: 'hidden',
  flexShrink: 0,
  background: colors.primary[100],
  color: colors.primary[700],
  fontFamily: typography.fontFamily.sans,
  fontWeight: typography.fontWeight.semibold,
  userSelect: 'none',
  ...sizeMap[size],
}))

const Img = styled.img({
  width: '100%',
  height: '100%',
  objectFit: 'cover',
})

function getInitials(name: string) {
  const parts = name.trim().split(/\s+/)
  if (parts.length === 1) return parts[0].slice(0, 2).toUpperCase()
  return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase()
}

export function Avatar({ src, alt, name, size = 'md', className }: AvatarProps) {
  const [imgError, setImgError] = useState(false)

  return (
    <Wrapper size={size} className={className} role="img" aria-label={alt ?? name}>
      {src && !imgError ? (
        <Img src={src} alt={alt ?? name ?? ''} onError={() => setImgError(true)} />
      ) : name ? (
        getInitials(name)
      ) : (
        <svg viewBox="0 0 24 24" fill="currentColor" width="55%" height="55%">
          <path d="M12 12c2.7 0 4.8-2.1 4.8-4.8S14.7 2.4 12 2.4 7.2 4.5 7.2 7.2 9.3 12 12 12zm0 2.4c-3.2 0-9.6 1.6-9.6 4.8v2.4h19.2v-2.4c0-3.2-6.4-4.8-9.6-4.8z" />
        </svg>
      )}
    </Wrapper>
  )
}
