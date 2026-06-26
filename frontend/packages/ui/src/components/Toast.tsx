import { useState, useCallback, useMemo, useEffect } from 'react'
import { createPortal } from 'react-dom'
import styled from '@emotion/styled'
import { keyframes } from '@emotion/react'
import type { ReactNode } from 'react'
import { colors, radii, shadows, spacing, typography } from '../tokens/index.ts'
import { ToastContext } from './ToastContext.ts'
import type { ToastOptions, ToastFn } from './ToastContext.ts'

type ToastVariant = 'default' | 'success' | 'warning' | 'danger'

interface ToastItem {
  id: string
  message: string
  variant: ToastVariant
  duration: number
}

let counter = 0

const slideIn = keyframes`
  from { opacity: 0; transform: translateX(100%); }
  to { opacity: 1; transform: translateX(0); }
`

const variantMap: Record<ToastVariant, { background: string; color: string; border: string }> = {
  default: { background: colors.gray[900], color: '#fff', border: colors.gray[700] },
  success: { background: colors.green[50], color: colors.green[700], border: colors.green[100] },
  warning: { background: colors.yellow[50], color: '#92400E', border: colors.yellow[100] },
  danger: { background: colors.red[50], color: colors.red[700], border: colors.red[100] },
}

const Container = styled.div({
  position: 'fixed',
  bottom: spacing[6],
  right: spacing[6],
  display: 'flex',
  flexDirection: 'column',
  gap: spacing[2],
  zIndex: 1100,
  pointerEvents: 'none',
})

const CUSTOM_PROPS = new Set(['variant'])

const ToastEl = styled('div', {
  shouldForwardProp: (prop) => !CUSTOM_PROPS.has(prop as string),
})<{ variant: ToastVariant }>(({ variant }) => {
  const v = variantMap[variant]
  return {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'space-between',
    gap: spacing[3],
    padding: `${spacing[3]} ${spacing[4]}`,
    borderRadius: radii.lg,
    border: `1px solid ${v.border}`,
    background: v.background,
    color: v.color,
    boxShadow: shadows.lg,
    fontFamily: typography.fontFamily.sans,
    fontSize: typography.fontSize.sm,
    fontWeight: typography.fontWeight.medium,
    minWidth: '260px',
    maxWidth: '380px',
    pointerEvents: 'auto',
    animation: `${slideIn} 200ms ease`,
  }
})

const DismissButton = styled.button<{ variant: ToastVariant }>(({ variant }) => ({
  background: 'transparent',
  border: 'none',
  cursor: 'pointer',
  color: 'inherit',
  opacity: 0.6,
  padding: '2px',
  lineHeight: 1,
  flexShrink: 0,
  borderRadius: radii.sm,
  '&:hover': { opacity: 1 },
  ...(variant ? {} : {}),
}))

function SingleToast({ item, onDismiss }: { item: ToastItem; onDismiss: (id: string) => void }) {
  useEffect(() => {
    const t = setTimeout(() => onDismiss(item.id), item.duration)
    return () => clearTimeout(t)
  }, [item.id, item.duration, onDismiss])

  return (
    <ToastEl variant={item.variant} role="status" aria-live="polite">
      {item.message}
      <DismissButton variant={item.variant} onClick={() => onDismiss(item.id)} aria-label="닫기">✕</DismissButton>
    </ToastEl>
  )
}

export interface ToastProviderProps {
  children?: ReactNode
}

export function ToastProvider({ children }: ToastProviderProps) {
  const [toasts, setToasts] = useState<ToastItem[]>([])

  const dismiss = useCallback((id: string) => {
    setToasts(prev => prev.filter(t => t.id !== id))
  }, [])

  const add = useCallback((message: string, options?: ToastOptions) => {
    const id = String(++counter)
    setToasts(prev => [...prev, {
      id,
      message,
      variant: options?.variant ?? 'default',
      duration: options?.duration ?? 3000,
    }])
  }, [])

  const toast = useMemo((): ToastFn => Object.assign(
    (message: string, options?: ToastOptions) => add(message, options),
    {
      success: (message: string, options?: Omit<ToastOptions, 'variant'>) => add(message, { ...options, variant: 'success' }),
      warning: (message: string, options?: Omit<ToastOptions, 'variant'>) => add(message, { ...options, variant: 'warning' }),
      danger: (message: string, options?: Omit<ToastOptions, 'variant'>) => add(message, { ...options, variant: 'danger' }),
    }
  ) as ToastFn, [add])

  return (
    <ToastContext.Provider value={{ toast }}>
      {children}
      {createPortal(
        <Container>
          {toasts.map(item => (
            <SingleToast key={item.id} item={item} onDismiss={dismiss} />
          ))}
        </Container>,
        document.body,
      )}
    </ToastContext.Provider>
  )
}
