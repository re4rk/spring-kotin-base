import { useEffect, createContext, useContext } from 'react'
import { createPortal } from 'react-dom'
import styled from '@emotion/styled'
import { keyframes } from '@emotion/react'
import type { ReactNode } from 'react'
import { colors, radii, shadows, spacing, typography } from '../tokens/index.ts'

type ModalSize = 'sm' | 'md' | 'lg' | 'xl'

interface ModalContextValue {
  onClose: () => void
}

const ModalContext = createContext<ModalContextValue | null>(null)

function useModalContext() {
  const ctx = useContext(ModalContext)
  if (!ctx) throw new Error('Modal compound components must be used within <Modal>')
  return ctx
}

const fadeIn = keyframes`from { opacity: 0 } to { opacity: 1 }`
const slideUp = keyframes`
  from { opacity: 0; transform: translate(-50%, calc(-50% + 8px)) scale(0.98); }
  to   { opacity: 1; transform: translate(-50%, -50%) scale(1); }
`

const BackdropEl = styled.div({
  position: 'fixed',
  inset: 0,
  background: 'rgba(0, 0, 0, 0.45)',
  zIndex: 1000,
  animation: `${fadeIn} 150ms ease`,
})

const sizeMap: Record<ModalSize, string> = {
  sm: '400px',
  md: '560px',
  lg: '720px',
  xl: '900px',
}

const DialogEl = styled('div', {
  shouldForwardProp: (prop) => prop !== 'size',
})<{ size: ModalSize }>(({ size }) => ({
  position: 'fixed',
  top: '50%',
  left: '50%',
  transform: 'translate(-50%, -50%)',
  background: '#fff',
  borderRadius: radii['2xl'],
  boxShadow: shadows.xl,
  width: `calc(100% - ${spacing[8]})`,
  maxWidth: sizeMap[size],
  maxHeight: 'calc(100svh - 48px)',
  display: 'flex',
  flexDirection: 'column' as const,
  zIndex: 1001,
  animation: `${slideUp} 180ms ease`,
}))

const HeaderEl = styled.div({
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'space-between',
  padding: `${spacing[5]} ${spacing[6]}`,
  borderBottom: `1px solid ${colors.gray[100]}`,
  flexShrink: 0,
})

const TitleEl = styled.p({
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
  '&:focus-visible': { outline: `2px solid ${colors.primary[500]}`, outlineOffset: '2px' },
})

const BodyEl = styled('div', {
  shouldForwardProp: (prop) => prop !== 'noPadding',
})<{ noPadding?: boolean }>(({ noPadding }) => ({
  padding: noPadding ? 0 : `${spacing[5]} ${spacing[6]}`,
  overflowY: 'auto',
  flex: 1,
}))

const FooterEl = styled.div({
  display: 'flex',
  justifyContent: 'flex-end',
  gap: spacing[3],
  padding: `${spacing[4]} ${spacing[6]}`,
  borderTop: `1px solid ${colors.gray[100]}`,
  flexShrink: 0,
})

export interface ModalProps {
  open: boolean
  onClose: () => void
  children?: ReactNode
}

function ModalRoot({ open, onClose, children }: ModalProps) {
  useEffect(() => {
    if (!open) return
    const handler = (e: KeyboardEvent) => { if (e.key === 'Escape') onClose() }
    document.addEventListener('keydown', handler)
    return () => document.removeEventListener('keydown', handler)
  }, [open, onClose])

  if (!open) return null

  return createPortal(
    <ModalContext.Provider value={{ onClose }}>
      {children}
    </ModalContext.Provider>,
    document.body,
  )
}

function ModalBackdrop() {
  const { onClose } = useModalContext()
  return <BackdropEl onClick={onClose} />
}

function ModalDialog({ size = 'md', children }: { size?: ModalSize; children?: ReactNode }) {
  return (
    <DialogEl size={size} role="dialog" aria-modal>
      {children}
    </DialogEl>
  )
}

function ModalHeader({ children }: { children?: ReactNode }) {
  const { onClose } = useModalContext()
  return (
    <HeaderEl>
      {children}
      <CloseButton onClick={onClose} aria-label="닫기">✕</CloseButton>
    </HeaderEl>
  )
}

function ModalTitle({ children }: { children?: ReactNode }) {
  return <TitleEl>{children}</TitleEl>
}

function ModalBody({ noPadding, children }: { noPadding?: boolean; children?: ReactNode }) {
  return <BodyEl noPadding={noPadding}>{children}</BodyEl>
}

function ModalFooter({ children }: { children?: ReactNode }) {
  return <FooterEl>{children}</FooterEl>
}

export const Modal = Object.assign(ModalRoot, {
  Backdrop: ModalBackdrop,
  Dialog: ModalDialog,
  Header: ModalHeader,
  Title: ModalTitle,
  Body: ModalBody,
  Footer: ModalFooter,
})
