import { useState } from 'react'
import styled from '@emotion/styled'
import { Button, Input, Text, Stack, Card, Divider, Container, colors, radii } from '@base/ui'
import { Page } from './layouts/Page.tsx'

const Swatch = styled('div', {
  shouldForwardProp: (prop) => prop !== 'bg',
})<{ bg: string }>(({ bg }) => ({
  width: '40px',
  height: '40px',
  borderRadius: radii.md,
  background: bg,
  border: `1px solid ${colors.gray[200]}`,
  flexShrink: 0,
}))

const PALETTES = [
  { name: 'Primary', palette: colors.primary },
  { name: 'Gray', palette: colors.gray },
  { name: 'Red', palette: colors.red },
  { name: 'Green', palette: colors.green },
] as const

function SectionHeader({ children }: { children: string }) {
  return (
    <>
      <Text variant="h4">{children}</Text>
      <Divider />
    </>
  )
}

function App() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [loginLoading, setLoginLoading] = useState(false)

  const handleLogin = () => {
    if (!email || !password) return
    setLoginLoading(true)
    setTimeout(() => setLoginLoading(false), 1500)
  }

  return (
    <Page>
      <Container>
        <Stack gap={8}>

          {/* Header */}
          <Stack gap={2}>
            <Text variant="h1">Design System</Text>
            <Text variant="body1" color={colors.gray[500]}>
              @base/ui — Emotion 기반 컴포넌트 라이브러리
            </Text>
          </Stack>

          {/* Typography */}
          <Card as="section">
            <SectionHeader>Typography</SectionHeader>
            <Stack gap={4}>
              <Text variant="h1">Heading 1 — 36px Bold</Text>
              <Text variant="h2">Heading 2 — 30px Bold</Text>
              <Text variant="h3">Heading 3 — 24px Semibold</Text>
              <Text variant="h4">Heading 4 — 20px Semibold</Text>
              <Text variant="body1">Body 1 — 16px 본문 텍스트입니다. 기본 단락에 사용합니다.</Text>
              <Text variant="body2">Body 2 — 14px 보조 텍스트입니다. 설명이나 부가 정보에 사용합니다.</Text>
              <Text variant="caption" color={colors.gray[500]}>Caption — 12px 캡션 텍스트</Text>
              <Text variant="label">Label — 14px Medium 레이블</Text>
            </Stack>
          </Card>

          {/* Colors */}
          <Card as="section">
            <SectionHeader>Colors</SectionHeader>
            <Stack gap={6}>
              {PALETTES.map(({ name, palette }) => (
                <Stack key={name} gap={2}>
                  <Text variant="label" color={colors.gray[500]}>{name}</Text>
                  <Stack direction="row" gap={2} wrap>
                    {Object.entries(palette).map(([key, value]) => (
                      <Stack key={key} gap={1} align="center">
                        <Swatch bg={value} />
                        <Text variant="caption" color={colors.gray[400]}>{key}</Text>
                      </Stack>
                    ))}
                  </Stack>
                </Stack>
              ))}
            </Stack>
          </Card>

          {/* Buttons */}
          <Card as="section">
            <SectionHeader>Button</SectionHeader>
            <Stack gap={6}>
              <Stack gap={2}>
                <Text variant="label" color={colors.gray[500]}>Variants</Text>
                <Stack direction="row" gap={3} wrap>
                  <Button variant="primary">Primary</Button>
                  <Button variant="secondary">Secondary</Button>
                  <Button variant="ghost">Ghost</Button>
                  <Button variant="danger">Danger</Button>
                </Stack>
              </Stack>
              <Stack gap={2}>
                <Text variant="label" color={colors.gray[500]}>Sizes</Text>
                <Stack direction="row" gap={3} align="center" wrap>
                  <Button size="sm">Small</Button>
                  <Button size="md">Medium</Button>
                  <Button size="lg">Large</Button>
                </Stack>
              </Stack>
              <Stack gap={2}>
                <Text variant="label" color={colors.gray[500]}>States</Text>
                <Stack direction="row" gap={3} wrap>
                  <Button disabled>Disabled</Button>
                  <Button loading>Loading</Button>
                  <Button variant="secondary" disabled>Secondary Disabled</Button>
                  <Button variant="ghost" loading>Ghost Loading</Button>
                </Stack>
              </Stack>
            </Stack>
          </Card>

          {/* Inputs */}
          <Card as="section">
            <SectionHeader>Input</SectionHeader>
            <Stack gap={6}>
              <Stack gap={2}>
                <Text variant="label" color={colors.gray[500]}>Sizes</Text>
                <Stack direction="row" gap={4} align="flex-end" wrap>
                  <Input inputSize="sm" placeholder="Small" />
                  <Input inputSize="md" placeholder="Medium (default)" />
                  <Input inputSize="lg" placeholder="Large" />
                </Stack>
              </Stack>
              <Stack gap={2}>
                <Text variant="label" color={colors.gray[500]}>States</Text>
                <Stack direction="row" gap={6} wrap>
                  <Stack gap={3} style={{ minWidth: '240px' }}>
                    <Input label="기본" placeholder="텍스트를 입력하세요" />
                    <Input label="에러" error="올바른 형식이 아닙니다" defaultValue="wrong@" />
                  </Stack>
                  <Stack gap={3} style={{ minWidth: '240px' }}>
                    <Input label="도움말" helperText="8자 이상 입력해주세요" type="password" placeholder="비밀번호" />
                    <Input label="비활성화" disabled placeholder="수정할 수 없습니다" />
                  </Stack>
                </Stack>
              </Stack>
            </Stack>
          </Card>

          {/* Form Example */}
          <Card as="section">
            <SectionHeader>Form 예시</SectionHeader>
            <Stack gap={5} style={{ maxWidth: '360px' }}>
              <Stack gap={1}>
                <Text variant="h3">다시 만나요</Text>
                <Text variant="body2" color={colors.gray[500]}>계정에 로그인하세요</Text>
              </Stack>
              <Stack gap={3}>
                <Input
                  label="이메일"
                  type="email"
                  placeholder="name@company.com"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  fullWidth
                />
                <Input
                  label="비밀번호"
                  type="password"
                  placeholder="••••••••"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  fullWidth
                />
              </Stack>
              <Button fullWidth loading={loginLoading} onClick={handleLogin}>
                로그인
              </Button>
              <Stack direction="row" gap={1} justify="center">
                <Text variant="body2" color={colors.gray[500]}>계정이 없으신가요?</Text>
                <Text variant="body2" color={colors.primary[600]}>회원가입</Text>
              </Stack>
            </Stack>
          </Card>

        </Stack>
      </Container>
    </Page>
  )
}

export default App
