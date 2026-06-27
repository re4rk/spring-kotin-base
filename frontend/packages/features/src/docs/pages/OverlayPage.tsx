import { useState } from 'react'
import { Stack, Card, Button, Text, Modal, Alert, useToast, colors } from '@base/ui'
import { SectionHeader } from '../components/SectionHeader.tsx'

function ToastSection() {
  const toast = useToast()
  return (
    <Card as="section">
      <SectionHeader>Toast</SectionHeader>
      <Stack gap={3}>
        <Text variant="body2" color={colors.gray[500]}>
          우측 하단에 알림이 표시됩니다. 3초 후 자동으로 사라집니다.
        </Text>
        <Stack direction="row" gap={3} wrap>
          <Button variant="secondary" onClick={() => toast('기본 알림 메시지입니다')}>Default</Button>
          <Button variant="secondary" onClick={() => toast.success('저장되었습니다!')}>Success</Button>
          <Button variant="secondary" onClick={() => toast.warning('저장하지 않은 내용이 있습니다')}>Warning</Button>
          <Button variant="secondary" onClick={() => toast.danger('오류가 발생했습니다')}>Danger</Button>
        </Stack>
      </Stack>
    </Card>
  )
}

export function OverlayPage() {
  const [basicOpen, setBasicOpen] = useState(false)
  const [confirmOpen, setConfirmOpen] = useState(false)
  const [smOpen, setSmOpen] = useState(false)
  const [lgOpen, setLgOpen] = useState(false)

  return (
    <Stack gap={6}>
      <Stack gap={1}>
        <Text variant="h2">Modal & Toast</Text>
        <Text variant="body1" color={colors.gray[500]}>오버레이로 표시되는 UI 컴포넌트입니다.</Text>
      </Stack>

      <Card as="section">
        <SectionHeader>Modal</SectionHeader>
        <Stack gap={4}>
          <Stack gap={2}>
            <Text variant="label" color={colors.gray[500]}>Sizes</Text>
            <Stack direction="row" gap={3} wrap>
              <Button variant="secondary" size="sm" onClick={() => setSmOpen(true)}>Small (sm)</Button>
              <Button variant="secondary" size="sm" onClick={() => setBasicOpen(true)}>Medium (md)</Button>
              <Button variant="secondary" size="sm" onClick={() => setLgOpen(true)}>Large (lg)</Button>
            </Stack>
          </Stack>
          <Stack gap={2}>
            <Text variant="label" color={colors.gray[500]}>With footer</Text>
            <Stack direction="row" gap={3}>
              <Button variant="secondary" onClick={() => setConfirmOpen(true)}>확인 모달</Button>
            </Stack>
          </Stack>
        </Stack>
      </Card>

      <ToastSection />

      {/* Modals */}
      <Modal open={smOpen} onClose={() => setSmOpen(false)}>
        <Modal.Backdrop />
        <Modal.Dialog size="sm">
          <Modal.Header><Modal.Title>알림</Modal.Title></Modal.Header>
          <Modal.Body>
            <Text variant="body1">작은 사이즈 모달입니다.</Text>
          </Modal.Body>
        </Modal.Dialog>
      </Modal>

      <Modal open={basicOpen} onClose={() => setBasicOpen(false)}>
        <Modal.Backdrop />
        <Modal.Dialog>
          <Modal.Header><Modal.Title>공지사항</Modal.Title></Modal.Header>
          <Modal.Body>
            <Stack gap={3}>
              <Text variant="body1">기본 사이즈 모달입니다.</Text>
              <Alert variant="info">새로운 기능이 추가되었습니다.</Alert>
            </Stack>
          </Modal.Body>
        </Modal.Dialog>
      </Modal>

      <Modal open={lgOpen} onClose={() => setLgOpen(false)}>
        <Modal.Backdrop />
        <Modal.Dialog size="lg">
          <Modal.Header><Modal.Title>상세 내용</Modal.Title></Modal.Header>
          <Modal.Body>
            <Stack gap={3}>
              <Text variant="body1">큰 사이즈 모달입니다.</Text>
              <Text variant="body2" color={colors.gray[500]}>더 많은 내용을 표시할 때 사용합니다.</Text>
            </Stack>
          </Modal.Body>
        </Modal.Dialog>
      </Modal>

      <Modal open={confirmOpen} onClose={() => setConfirmOpen(false)}>
        <Modal.Backdrop />
        <Modal.Dialog size="sm">
          <Modal.Header><Modal.Title>주문 확인</Modal.Title></Modal.Header>
          <Modal.Body>
            <Stack gap={3}>
              <Text variant="body1">선택한 상품을 주문하시겠습니까?</Text>
              <Alert variant="warning">결제 후 취소는 24시간 이내에만 가능합니다.</Alert>
            </Stack>
          </Modal.Body>
          <Modal.Footer>
            <Button variant="ghost" onClick={() => setConfirmOpen(false)}>취소</Button>
            <Button onClick={() => setConfirmOpen(false)}>확인</Button>
          </Modal.Footer>
        </Modal.Dialog>
      </Modal>
    </Stack>
  )
}
