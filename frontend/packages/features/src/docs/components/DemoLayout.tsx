import styled from '@emotion/styled'
import { Stack, spacing } from '@base/ui'

const MOBILE_BREAKPOINT = '768px'

export const DemoRow = styled(Stack)({
  flexDirection: 'row',
  flexWrap: 'wrap',
  gap: spacing[3],
  alignItems: 'center',
})

export const DemoGrid = styled.div({
  display: 'grid',
  gap: spacing[4],
  gridTemplateColumns: '1fr',
  [`@media (min-width: ${MOBILE_BREAKPOINT})`]: {
    gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))',
  },
})

export const DemoColumn = styled(Stack)({
  minWidth: 0,
  width: '100%',
  [`@media (min-width: ${MOBILE_BREAKPOINT})`]: {
    minWidth: '240px',
    width: 'auto',
  },
})

export const ConstrainedWidth = styled.div({
  width: '100%',
  maxWidth: '100%',
  [`@media (min-width: ${MOBILE_BREAKPOINT})`]: {
    maxWidth: '480px',
  },
})

export const SelectWidth = styled.div({
  width: '100%',
  [`@media (min-width: ${MOBILE_BREAKPOINT})`]: {
    maxWidth: '320px',
  },
})
