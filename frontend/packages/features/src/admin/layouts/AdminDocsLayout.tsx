import styled from '@emotion/styled'
import { NavLink, Outlet } from 'react-router-dom'
import { colors, spacing, typography, radii } from '@base/ui'

const MOBILE_BREAKPOINT = '768px'

const NAV = [
  {
    group: 'Foundation',
    items: [
      { path: '/admin/design-system/typography', label: 'Typography' },
      { path: '/admin/design-system/colors', label: 'Colors' },
    ],
  },
  {
    group: 'Components',
    items: [
      { path: '/admin/design-system/buttons', label: 'Button' },
      { path: '/admin/design-system/form', label: 'Form Controls' },
      { path: '/admin/design-system/feedback', label: 'Feedback' },
      { path: '/admin/design-system/overlay', label: 'Modal & Toast' },
      { path: '/admin/design-system/navigation', label: 'Navigation' },
    ],
  },
]

const FLAT_NAV = NAV.flatMap(({ items }) => items)

const Wrapper = styled.div({
  display: 'flex',
  flexDirection: 'column',
  minHeight: 'calc(100svh - 57px)',
  background: colors.gray[100],
  [`@media (min-width: ${MOBILE_BREAKPOINT})`]: {
    flexDirection: 'row',
  },
})

const MobileNavBar = styled.div({
  display: 'block',
  background: '#fff',
  borderBottom: `1px solid ${colors.gray[200]}`,
  [`@media (min-width: ${MOBILE_BREAKPOINT})`]: {
    display: 'none',
  },
})

const MobileNavHeader = styled.div({
  padding: `${spacing[3]} ${spacing[4]} ${spacing[2]}`,
})

const MobileAppName = styled.p({
  margin: 0,
  fontSize: typography.fontSize.sm,
  fontWeight: typography.fontWeight.bold,
  fontFamily: typography.fontFamily.sans,
  color: colors.gray[900],
})

const MobileAppSubtitle = styled.p({
  margin: '2px 0 0',
  fontSize: typography.fontSize.xs,
  fontFamily: typography.fontFamily.sans,
  color: colors.gray[400],
})

const MobileNav = styled.nav({
  overflowX: 'auto',
  WebkitOverflowScrolling: 'touch',
  scrollbarWidth: 'none',
  paddingBottom: spacing[2],
  '&::-webkit-scrollbar': {
    display: 'none',
  },
})

const MobileNavInner = styled.div({
  display: 'flex',
  gap: spacing[2],
  padding: `0 ${spacing[4]}`,
  width: 'max-content',
  minWidth: '100%',
})

const MobileNavLink = styled(NavLink)({
  display: 'inline-flex',
  alignItems: 'center',
  padding: `${spacing[2]} ${spacing[3]}`,
  borderRadius: radii.full,
  fontSize: typography.fontSize.sm,
  fontWeight: typography.fontWeight.medium,
  fontFamily: typography.fontFamily.sans,
  color: colors.gray[600],
  textDecoration: 'none',
  background: colors.gray[100],
  whiteSpace: 'nowrap',
  flexShrink: 0,
  transition: 'background 120ms, color 120ms',
  '&[aria-current="page"]': {
    background: colors.primary[50],
    color: colors.primary[600],
    fontWeight: typography.fontWeight.semibold,
  },
})

const Sidebar = styled.nav({
  display: 'none',
  [`@media (min-width: ${MOBILE_BREAKPOINT})`]: {
    display: 'flex',
    flexDirection: 'column',
    width: '220px',
    flexShrink: 0,
    background: '#fff',
    borderRight: `1px solid ${colors.gray[200]}`,
    overflowY: 'auto',
    padding: `${spacing[6]} 0`,
  },
})

const SidebarTitle = styled.div({
  padding: `0 ${spacing[5]} ${spacing[6]}`,
  borderBottom: `1px solid ${colors.gray[100]}`,
  marginBottom: spacing[2],
})

const SidebarAppName = styled.p({
  margin: 0,
  fontSize: typography.fontSize.base,
  fontWeight: typography.fontWeight.bold,
  fontFamily: typography.fontFamily.sans,
  color: colors.gray[900],
})

const SidebarSubtitle = styled.p({
  margin: '2px 0 0',
  fontSize: typography.fontSize.xs,
  fontFamily: typography.fontFamily.sans,
  color: colors.gray[400],
})

const NavGroup = styled.div({
  padding: `${spacing[3]} 0`,
})

const NavGroupLabel = styled.p({
  margin: 0,
  padding: `0 ${spacing[5]} ${spacing[2]}`,
  fontSize: typography.fontSize.xs,
  fontWeight: typography.fontWeight.semibold,
  color: colors.gray[400],
  fontFamily: typography.fontFamily.sans,
  letterSpacing: '0.06em',
  textTransform: 'uppercase',
})

const StyledNavLink = styled(NavLink)({
  display: 'block',
  padding: `${spacing[2]} ${spacing[5]}`,
  fontSize: typography.fontSize.sm,
  fontWeight: typography.fontWeight.medium,
  fontFamily: typography.fontFamily.sans,
  color: colors.gray[600],
  textDecoration: 'none',
  borderRadius: radii.md,
  margin: `0 ${spacing[2]}`,
  transition: 'background 120ms, color 120ms',
  '&:hover': {
    background: colors.gray[100],
    color: colors.gray[900],
  },
  '&[aria-current="page"]': {
    background: colors.primary[50],
    color: colors.primary[600],
    fontWeight: typography.fontWeight.semibold,
  },
})

const Content = styled.main({
  flex: 1,
  minWidth: 0,
  padding: spacing[4],
  [`@media (min-width: ${MOBILE_BREAKPOINT})`]: {
    padding: `${spacing[10]} ${spacing[8]}`,
    maxWidth: '1080px',
  },
})

export function AdminDocsLayout() {
  return (
    <Wrapper>
      <MobileNavBar>
        <MobileNavHeader>
          <MobileAppName>@base/ui</MobileAppName>
          <MobileAppSubtitle>Design System</MobileAppSubtitle>
        </MobileNavHeader>
        <MobileNav aria-label="디자인 시스템 메뉴">
          <MobileNavInner>
            {FLAT_NAV.map(({ path, label }) => (
              <MobileNavLink key={path} to={path} end>
                {label}
              </MobileNavLink>
            ))}
          </MobileNavInner>
        </MobileNav>
      </MobileNavBar>

      <Sidebar aria-label="디자인 시스템 사이드바">
        <SidebarTitle>
          <SidebarAppName>@base/ui</SidebarAppName>
          <SidebarSubtitle>Design System</SidebarSubtitle>
        </SidebarTitle>

        {NAV.map(({ group, items }) => (
          <NavGroup key={group}>
            <NavGroupLabel>{group}</NavGroupLabel>
            {items.map(({ path, label }) => (
              <StyledNavLink key={path} to={path} end>
                {label}
              </StyledNavLink>
            ))}
          </NavGroup>
        ))}
      </Sidebar>

      <Content>
        <Outlet />
      </Content>
    </Wrapper>
  )
}
