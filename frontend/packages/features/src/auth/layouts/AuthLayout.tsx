import styled from '@emotion/styled'
import { Outlet } from 'react-router-dom'
import { colors, spacing } from '@base/ui'

const Wrapper = styled.div({
  minHeight: '100svh',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  background: colors.gray[100],
  padding: spacing[4],
})

export function AuthLayout() {
  return (
    <Wrapper>
      <Outlet />
    </Wrapper>
  )
}
