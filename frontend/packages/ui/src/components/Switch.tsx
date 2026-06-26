import styled from '@emotion/styled'
import { useState } from 'react'
import type { InputHTMLAttributes, ReactNode, ChangeEvent } from 'react'
import { colors, spacing, typography } from '../tokens/index.ts'

type SwitchSize = 'sm' | 'md'

export interface SwitchProps extends Omit<InputHTMLAttributes<HTMLInputElement>, 'type' | 'size'> {
  label?: ReactNode
  size?: SwitchSize
}

const sizeMap = {
  sm: { width: '32px', height: '18px', thumb: '14px', offset: '2px', translate: '14px' },
  md: { width: '44px', height: '24px', thumb: '18px', offset: '3px', translate: '20px' },
}

const CUSTOM_PROPS = new Set(['checked', 'switchSize', 'disabled'])

const Wrapper = styled('label', {
  shouldForwardProp: (prop) => prop !== 'disabled',
})<{ disabled?: boolean }>(({ disabled }) => ({
  display: 'inline-flex',
  alignItems: 'center',
  gap: spacing[2],
  cursor: disabled ? 'not-allowed' : 'pointer',
  opacity: disabled ? 0.5 : 1,
  fontFamily: typography.fontFamily.sans,
  fontSize: typography.fontSize.sm,
  color: colors.gray[700],
  userSelect: 'none',
}))

const HiddenInput = styled.input({
  position: 'absolute',
  opacity: 0,
  width: 0,
  height: 0,
  pointerEvents: 'none',
})

const Track = styled('span', {
  shouldForwardProp: (prop) => !CUSTOM_PROPS.has(prop as string),
})<{ checked: boolean; switchSize: SwitchSize }>(({ checked, switchSize }) => {
  const s = sizeMap[switchSize]
  return {
    position: 'relative',
    display: 'inline-block',
    width: s.width,
    height: s.height,
    borderRadius: '9999px',
    background: checked ? colors.primary[600] : colors.gray[300],
    transition: 'background 200ms',
    flexShrink: 0,
    [`${Wrapper}:focus-within &`]: {
      outline: `2px solid ${colors.primary[500]}`,
      outlineOffset: '2px',
    },
  }
})

const Thumb = styled('span', {
  shouldForwardProp: (prop) => !CUSTOM_PROPS.has(prop as string),
})<{ checked: boolean; switchSize: SwitchSize }>(({ checked, switchSize }) => {
  const s = sizeMap[switchSize]
  return {
    position: 'absolute',
    top: s.offset,
    left: s.offset,
    width: s.thumb,
    height: s.thumb,
    borderRadius: '50%',
    background: '#fff',
    boxShadow: '0 1px 3px rgba(0,0,0,0.2)',
    transition: 'transform 200ms',
    transform: checked ? `translateX(${s.translate})` : 'translateX(0)',
  }
})

export function Switch({
  label,
  size = 'md',
  checked: controlledChecked,
  defaultChecked,
  onChange,
  disabled,
  ...props
}: SwitchProps) {
  const [internalChecked, setInternalChecked] = useState(defaultChecked ?? false)
  const isChecked = controlledChecked !== undefined ? controlledChecked : internalChecked

  const handleChange = (e: ChangeEvent<HTMLInputElement>) => {
    if (controlledChecked === undefined) setInternalChecked(e.target.checked)
    onChange?.(e)
  }

  return (
    <Wrapper disabled={disabled}>
      <HiddenInput
        type="checkbox"
        checked={isChecked}
        onChange={handleChange}
        disabled={disabled}
        {...props}
      />
      <Track checked={isChecked} switchSize={size}>
        <Thumb checked={isChecked} switchSize={size} />
      </Track>
      {label}
    </Wrapper>
  )
}
