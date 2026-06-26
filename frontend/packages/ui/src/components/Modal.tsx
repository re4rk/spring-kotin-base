import { useEffect } from 'react'
import { createPortal } from 'react-dom'
import styled from '@emotion/styled'
import { keyframes } from '@emotion/react'
import type { ReactNode, MouseEvent } from 'react'
import { colors, radii, shadows, spacing, typography } from '../tokens/index.ts'

type ModalSize = 'sm' | 'md' | 'lg' | 'xl'

export interface ModalProps {
  open: boolean
  onClose: () => void
  title?: string
  children?: ReactNode
  footer?: ReactNode
  size?: ModalSize
  closeOnBackdrop?: boolean
}

const fadeIn = keyframes`
  from { opacity: 0; }
  to { opacity: 1; }
`

const slideUp = keyframes`
  from { opacity: 0; transform: translateY(8px) scale(0.98); }
  to { opacity: 1; transform: translateY(0) scale(1); }
`

const Backdrop = styled.div({
  position: 'fixed',
  inset: 0,
  background: 'rgba(0, 0, 0, 0.45)',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  padding: spacing[4],
  zIndex: 1000,
  animation: `${fadeIn} 150ms ease`,
})

const sizeMap: Record<ModalSize, string> = {
  sm: '400px',
  md: '560px',
  lg: '720px',
  xl: '900px',
}

const Dialog = styled('div', {
  shouldForwardProp: (prop) => prop !== 'size',
})<{ size: ModalSize }>(({ size }) => ({
  background: '#fff',
  borderRadius: radii['2xl'],
  boxShadow: shadows.xl,
  width: '100%',
  maxWidth: sizeMap[size],
  maxHeight: 'calc(100svh - 48px)',
  display: 'flex',
  flexDirection: 'column' as const,
  animation: `${slideUp} 180ms ease`,
}))

const Header = styled.div({
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'space-between',
  padding: `${spacing[5]} ${spacing[6]}`,
  borderBottom: `1px solid ${colors.gray[100]}`,
  flexShrink: 0,
})

const Title = styled.p({
  margin: 0,
  fontSize: typography.fontSize.lg,
  fontWeight: typography.fontWeight.semibold,
  color: colors.gray[900],
  fontFamily: typography.fontFamily.sans,
})

const CloseButton = styled.button({
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  width: '32px',
  height: '32px',
  borderRadius: radii.md,
  border: 'none',
  background: 'transparent',
  color: colors.gray[400],
  cursor: 'pointer',
  fontSize: '18px',
  lineHeight: 1,
  flexShrink: 0,
  transition: 'background 150ms, color 150ms',
  '&:hover': { background: colors.gray[100], color: colors.gray[700] },
  '&:focus-visible': {
    outline: `2px solid ${colors.primary[500]}`,
    outlineOffset: '2px',
  },
})

const Body = styled.div({
  padding: `${spacing[5]} ${spacing[6]}`,
  overflowY: 'auto',
  flex: 1,
})

const Footer = styled.div({
  display: 'flex',
  justifyContent: 'flex-end',
  gap: spacing[3],
  padding: `${spacing[4]} ${spacing[6]}`,
  borderTop: `1px solid ${colors.gray[100]}`,
  flexShrink: 0,
})

export function Modal({
  open,
  onClose,
  title,
  children,
  footer,
  size = 'md',
  closeOnBackdrop = true,
}: ModalProps) {
  useEffect(() => {
    if (!open) return
    const handler = (e: KeyboardEvent) => { if (e.key === 'Escape') onClose() }
    document.addEventListener('keydown', handler)
    return () => document.removeEventListener('keydown', handler)
  }, [open, onClose])

  if (!open) return null

  const handleBackdropClick = closeOnBackdrop ? onClose : undefined
  const stopPropagation = (e: MouseEvent) => e.stopPropagation()

  return createPortal(
    <Backdrop onClick={handleBackdropClick}>
      <Dialog size={size} role="dialog" aria-modal aria-labelledby={title ? 'modal-title' : undefined} onClick={stopPropagation}>
        {title !== undefined && (
          <Header>
            <Title id="modal-title">{title}</Title>
            <CloseButton onClick={onClose} aria-label="닫기">✕</CloseButton>
          </Header>
        )}
        <Body>{children}</Body>
        {footer && <Footer>{footer}</Footer>}
      </Dialog>
    </Backdrop>,
    document.body,
  )
}
