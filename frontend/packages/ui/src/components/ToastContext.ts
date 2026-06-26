import { createContext } from 'react'

export interface ToastOptions {
  variant?: 'default' | 'success' | 'warning' | 'danger'
  duration?: number
}

export interface ToastFn {
  (message: string, options?: ToastOptions): void
  success: (message: string, options?: Omit<ToastOptions, 'variant'>) => void
  warning: (message: string, options?: Omit<ToastOptions, 'variant'>) => void
  danger: (message: string, options?: Omit<ToastOptions, 'variant'>) => void
}

export interface ToastContextValue {
  toast: ToastFn
}

export const ToastContext = createContext<ToastContextValue | null>(null)
