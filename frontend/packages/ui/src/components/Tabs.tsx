import { createContext, useContext, useState } from 'react'
import styled from '@emotion/styled'
import type { ReactNode, HTMLAttributes } from 'react'
import { colors, typography, spacing, radii } from '../tokens/index.ts'

interface TabsContextValue {
  active: string
  setActive: (id: string) => void
}

const TabsContext = createContext<TabsContextValue | null>(null)

function useTabsContext() {
  const ctx = useContext(TabsContext)
  if (!ctx) throw new Error('Tabs subcomponents must be used within <Tabs>')
  return ctx
}

export interface TabsProps {
  defaultTab?: string
  activeTab?: string
  onChange?: (id: string) => void
  children?: ReactNode
  className?: string
}

export interface TabListProps extends HTMLAttributes<HTMLDivElement> {
  children?: ReactNode
}

export interface TabProps extends HTMLAttributes<HTMLButtonElement> {
  id: string
  disabled?: boolean
  children?: ReactNode
}

export interface TabPanelProps {
  id: string
  children?: ReactNode
  className?: string
}

const StyledTabList = styled.div({
  display: 'flex',
  borderBottom: `1px solid ${colors.gray[200]}`,
  gap: spacing[1],
})

const CUSTOM_PROPS = new Set(['active'])

const StyledTab = styled('button', {
  shouldForwardProp: (prop) => !CUSTOM_PROPS.has(prop as string),
})<{ active: boolean }>(({ active }) => ({
  padding: `${spacing[2]} ${spacing[4]}`,
  border: 'none',
  background: 'transparent',
  fontFamily: typography.fontFamily.sans,
  fontSize: typography.fontSize.sm,
  fontWeight: active ? typography.fontWeight.semibold : typography.fontWeight.normal,
  color: active ? colors.primary[600] : colors.gray[500],
  cursor: 'pointer',
  borderBottom: `2px solid ${active ? colors.primary[600] : 'transparent'}`,
  marginBottom: '-1px',
  transition: 'color 150ms, border-color 150ms',
  borderRadius: `${radii.md} ${radii.md} 0 0`,
  '&:hover:not(:disabled)': {
    color: active ? colors.primary[600] : colors.gray[700],
    background: colors.gray[50],
  },
  '&:disabled': { opacity: 0.4, cursor: 'not-allowed' },
  '&:focus-visible': {
    outline: `2px solid ${colors.primary[500]}`,
    outlineOffset: '2px',
  },
}))

const StyledTabPanel = styled.div({
  paddingTop: spacing[4],
})

export function Tabs({ defaultTab = '', activeTab, onChange, children, className }: TabsProps) {
  const [internalActive, setInternalActive] = useState(defaultTab)
  const active = activeTab ?? internalActive
  const setActive = onChange ?? setInternalActive

  return (
    <TabsContext.Provider value={{ active, setActive }}>
      <div className={className}>{children}</div>
    </TabsContext.Provider>
  )
}

export function TabList({ children, ...htmlProps }: TabListProps) {
  return <StyledTabList role="tablist" {...htmlProps}>{children}</StyledTabList>
}

export function Tab({ id, disabled, children, ...htmlProps }: TabProps) {
  const { active, setActive } = useTabsContext()
  return (
    <StyledTab
      role="tab"
      aria-selected={active === id}
      aria-controls={`panel-${id}`}
      active={active === id}
      disabled={disabled}
      onClick={() => !disabled && setActive(id)}
      {...htmlProps}
    >
      {children}
    </StyledTab>
  )
}

export function TabPanel({ id, children, className }: TabPanelProps) {
  const { active } = useTabsContext()
  if (active !== id) return null
  return (
    <StyledTabPanel
      role="tabpanel"
      id={`panel-${id}`}
      aria-labelledby={id}
      className={className}
    >
      {children}
    </StyledTabPanel>
  )
}
