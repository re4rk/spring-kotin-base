import { useState } from 'react'
import styled from '@emotion/styled'
import {
  Button, Input, Textarea, Text, Stack, Card, Divider, Container,
  Badge, Spinner, Alert, Select, Checkbox, Avatar,
  Switch, Radio, RadioGroup, Modal, useToast,
  Tabs, TabList, Tab, TabPanel, Skeleton, Progress,
  colors, radii,
} from '@base/ui'
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

function ModalAndToastSection() {
  const [modalOpen, setModalOpen] = useState(false)
  const toast = useToast()

  return (
    <Card as="section">
      <SectionHeader>Modal / Toast</SectionHeader>
      <Stack gap={4}>
        <Stack gap={2}>
          <Text variant="label" color={colors.gray[500]}>Modal</Text>
          <Stack direction="row" gap={3}>
            <Button variant="secondary" onClick={() => setModalOpen(true)}>모달 열기</Button>
          </Stack>
          <Modal
            open={modalOpen}
            onClose={() => setModalOpen(false)}
            title="주문 확인"
            footer={
              <>
                <Button variant="ghost" onClick={() => setModalOpen(false)}>취소</Button>
                <Button onClick={() => setModalOpen(false)}>확인</Button>
              </>
            }
          >
            <Stack gap={3}>
              <Text variant="body1">선택한 상품을 주문하시겠습니까?</Text>
              <Alert variant="info">결제 후 취소는 24시간 이내에만 가능합니다.</Alert>
            </Stack>
          </Modal>
        </Stack>
        <Stack gap={2}>
          <Text variant="label" color={colors.gray[500]}>Toast</Text>
          <Stack direction="row" gap={3} wrap>
            <Button variant="secondary" size="sm" onClick={() => toast('기본 알림 메시지입니다')}>Default</Button>
            <Button variant="secondary" size="sm" onClick={() => toast.success('저장되었습니다!')}>Success</Button>
            <Button variant="secondary" size="sm" onClick={() => toast.warning('저장하지 않은 내용이 있습니다')}>Warning</Button>
            <Button variant="secondary" size="sm" onClick={() => toast.danger('오류가 발생했습니다')}>Danger</Button>
          </Stack>
        </Stack>
      </Stack>
    </Card>
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

          {/* Badge */}
          <Card as="section">
            <SectionHeader>Badge</SectionHeader>
            <Stack gap={4}>
              <Stack gap={2}>
                <Text variant="label" color={colors.gray[500]}>Variants</Text>
                <Stack direction="row" gap={2} wrap align="center">
                  <Badge variant="primary">Primary</Badge>
                  <Badge variant="secondary">Secondary</Badge>
                  <Badge variant="success">Success</Badge>
                  <Badge variant="warning">Warning</Badge>
                  <Badge variant="danger">Danger</Badge>
                  <Badge variant="ghost">Ghost</Badge>
                </Stack>
              </Stack>
              <Stack gap={2}>
                <Text variant="label" color={colors.gray[500]}>Sizes</Text>
                <Stack direction="row" gap={2} align="center">
                  <Badge size="sm">Small</Badge>
                  <Badge size="md">Medium</Badge>
                </Stack>
              </Stack>
            </Stack>
          </Card>

          {/* Spinner */}
          <Card as="section">
            <SectionHeader>Spinner</SectionHeader>
            <Stack direction="row" gap={6} align="center">
              <Stack gap={2} align="center">
                <Spinner size="sm" />
                <Text variant="caption" color={colors.gray[500]}>sm</Text>
              </Stack>
              <Stack gap={2} align="center">
                <Spinner size="md" />
                <Text variant="caption" color={colors.gray[500]}>md</Text>
              </Stack>
              <Stack gap={2} align="center">
                <Spinner size="lg" />
                <Text variant="caption" color={colors.gray[500]}>lg</Text>
              </Stack>
              <Stack gap={2} align="center">
                <Spinner size="md" color={colors.green[600]} />
                <Text variant="caption" color={colors.gray[500]}>custom color</Text>
              </Stack>
            </Stack>
          </Card>

          {/* Alert */}
          <Card as="section">
            <SectionHeader>Alert</SectionHeader>
            <Stack gap={3}>
              <Alert variant="info" title="안내">배포가 진행 중입니다. 잠시 후 다시 시도해 주세요.</Alert>
              <Alert variant="success" title="완료">변경 사항이 저장되었습니다.</Alert>
              <Alert variant="warning" title="주의">저장하지 않은 내용이 있습니다. 페이지를 떠나면 사라집니다.</Alert>
              <Alert variant="danger" title="오류">요청을 처리할 수 없습니다. 잠시 후 다시 시도해 주세요.</Alert>
            </Stack>
          </Card>

          {/* Avatar */}
          <Card as="section">
            <SectionHeader>Avatar</SectionHeader>
            <Stack gap={4}>
              <Stack gap={2}>
                <Text variant="label" color={colors.gray[500]}>Sizes</Text>
                <Stack direction="row" gap={3} align="center">
                  <Avatar size="xs" name="김민준" />
                  <Avatar size="sm" name="이서연" />
                  <Avatar size="md" name="박지호" />
                  <Avatar size="lg" name="최수아" />
                  <Avatar size="xl" name="정태양" />
                </Stack>
              </Stack>
              <Stack gap={2}>
                <Text variant="label" color={colors.gray[500]}>Fallback (no src)</Text>
                <Stack direction="row" gap={3} align="center">
                  <Avatar size="md" name="Logan Jo" />
                  <Avatar size="md" name="Admin" />
                  <Avatar size="md" />
                </Stack>
              </Stack>
            </Stack>
          </Card>

          {/* Select & Checkbox */}
          <Card as="section">
            <SectionHeader>Select / Checkbox</SectionHeader>
            <Stack direction="row" gap={8} wrap>
              <Stack gap={3} style={{ minWidth: '240px' }}>
                <Select label="역할" defaultValue="">
                  <option value="" disabled>선택하세요</option>
                  <option value="admin">관리자</option>
                  <option value="manager">매니저</option>
                  <option value="viewer">뷰어</option>
                </Select>
                <Select label="상태" defaultValue="active">
                  <option value="active">활성</option>
                  <option value="inactive">비활성</option>
                </Select>
                <Select label="에러" error="항목을 선택해주세요" defaultValue="">
                  <option value="" disabled>선택하세요</option>
                </Select>
              </Stack>
              <Stack gap={3}>
                <Checkbox label="이메일 알림 수신" defaultChecked />
                <Checkbox label="SMS 알림 수신" />
                <Checkbox label="비활성화" disabled />
                <Checkbox label="비활성화 + 체크" disabled defaultChecked />
                <Checkbox label="약관에 동의합니다" error="필수 항목입니다" />
              </Stack>
            </Stack>
          </Card>

          {/* Textarea */}
          <Card as="section">
            <SectionHeader>Textarea</SectionHeader>
            <Stack gap={4} style={{ maxWidth: '480px' }}>
              <Textarea label="내용" placeholder="내용을 입력하세요" rows={3} />
              <Textarea label="에러" error="필수 항목입니다" rows={3} />
              <Textarea label="resize 없음" resize="none" placeholder="크기 조절 불가" rows={3} />
              <Textarea label="비활성화" disabled placeholder="수정할 수 없습니다" rows={3} />
            </Stack>
          </Card>

          {/* Switch & Radio */}
          <Card as="section">
            <SectionHeader>Switch / Radio</SectionHeader>
            <Stack direction="row" gap={10} wrap>
              <Stack gap={3}>
                <Text variant="label" color={colors.gray[500]}>Switch</Text>
                <Switch label="이메일 알림" defaultChecked />
                <Switch label="SMS 알림" />
                <Switch label="비활성화" disabled />
                <Switch label="비활성화 + 켜짐" disabled defaultChecked />
                <Switch label="Small" size="sm" defaultChecked />
              </Stack>
              <Stack gap={3}>
                <Text variant="label" color={colors.gray[500]}>Radio</Text>
                <RadioGroup label="배송 방법" defaultValue="standard">
                  <Radio name="shipping" value="standard" label="일반 배송 (3-5일)" defaultChecked />
                  <Radio name="shipping" value="express" label="빠른 배송 (1-2일)" />
                  <Radio name="shipping" value="same" label="당일 배송" />
                  <Radio name="shipping" value="pickup" label="직접 수령" disabled />
                </RadioGroup>
              </Stack>
            </Stack>
          </Card>

          {/* Skeleton */}
          <Card as="section">
            <SectionHeader>Skeleton</SectionHeader>
            <Stack gap={6}>
              <Stack gap={2}>
                <Text variant="label" color={colors.gray[500]}>Text</Text>
                <Skeleton count={3} />
                <Skeleton width="60%" />
              </Stack>
              <Stack gap={2}>
                <Text variant="label" color={colors.gray[500]}>Card</Text>
                <Stack direction="row" gap={3}>
                  <Skeleton variant="circular" width={40} height={40} />
                  <Stack gap={2} style={{ flex: 1 }}>
                    <Skeleton width="40%" />
                    <Skeleton />
                    <Skeleton width="70%" />
                  </Stack>
                </Stack>
              </Stack>
              <Stack gap={2}>
                <Text variant="label" color={colors.gray[500]}>Rectangular</Text>
                <Skeleton variant="rectangular" height={120} />
              </Stack>
            </Stack>
          </Card>

          {/* Progress */}
          <Card as="section">
            <SectionHeader>Progress</SectionHeader>
            <Stack gap={5}>
              <Stack gap={2}>
                <Text variant="label" color={colors.gray[500]}>Determinate</Text>
                <Progress value={25} />
                <Progress value={60} />
                <Progress value={90} />
                <Progress value={100} color={colors.green[600]} />
              </Stack>
              <Stack gap={2}>
                <Text variant="label" color={colors.gray[500]}>Sizes</Text>
                <Progress value={50} size="sm" />
                <Progress value={50} size="md" />
                <Progress value={50} size="lg" />
              </Stack>
              <Stack gap={2}>
                <Text variant="label" color={colors.gray[500]}>Indeterminate</Text>
                <Progress variant="indeterminate" />
              </Stack>
            </Stack>
          </Card>

          {/* Tabs */}
          <Card as="section">
            <SectionHeader>Tabs</SectionHeader>
            <Tabs defaultTab="overview">
              <TabList>
                <Tab id="overview">개요</Tab>
                <Tab id="details">상세 정보</Tab>
                <Tab id="history">변경 이력</Tab>
                <Tab id="settings" disabled>설정</Tab>
              </TabList>
              <TabPanel id="overview">
                <Stack gap={2}>
                  <Text variant="body1">개요 패널입니다.</Text>
                  <Text variant="body2" color={colors.gray[500]}>탭을 클릭하여 콘텐츠를 전환하세요.</Text>
                </Stack>
              </TabPanel>
              <TabPanel id="details">
                <Text variant="body1">상세 정보 패널입니다.</Text>
              </TabPanel>
              <TabPanel id="history">
                <Text variant="body1">변경 이력 패널입니다.</Text>
              </TabPanel>
            </Tabs>
          </Card>

          {/* Modal & Toast */}
          <ModalAndToastSection />

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
