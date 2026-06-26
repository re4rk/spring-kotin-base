import { useContext } from 'react'
import { ToastContext } from './ToastContext.ts'
import type { ToastFn } from './ToastContext.ts'

export function useToast(): ToastFn {
  const ctx = useContext(ToastContext)
  if (!ctx) throw new Error('useToast must be used within <ToastProvider>')
  return ctx.toast
}
