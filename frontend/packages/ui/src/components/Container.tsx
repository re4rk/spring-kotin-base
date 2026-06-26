import styled from '@emotion/styled'
import type { HTMLAttributes } from 'react'

export interface ContainerProps extends HTMLAttributes<HTMLDivElement> {
  maxWidth?: string | number
}

export const Container = styled('div', {
  shouldForwardProp: (prop) => prop !== 'maxWidth',
})<ContainerProps>(({ maxWidth = '900px' }) => ({
  width: '100%',
  maxWidth: typeof maxWidth === 'number' ? `${maxWidth}px` : maxWidth,
  marginInline: 'auto',
}))
