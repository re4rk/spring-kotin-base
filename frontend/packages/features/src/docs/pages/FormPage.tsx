import { Stack, Card, Text, Input, Textarea, Select, Checkbox, Switch, Radio, RadioGroup, colors } from '@base/ui'
import { SectionHeader } from '../components/SectionHeader.tsx'
import { DemoColumn, DemoGrid, DemoRow, SelectWidth, ConstrainedWidth } from '../components/DemoLayout.tsx'

export function FormPage() {
  return (
    <Stack gap={6}>
      <Stack gap={1}>
        <Text variant="h2">Form Controls</Text>
        <Text variant="body1" color={colors.gray[500]}>사용자 입력을 받는 폼 컴포넌트입니다.</Text>
      </Stack>

      {/* Input */}
      <Card as="section">
        <SectionHeader>Input</SectionHeader>
        <Stack gap={5}>
          <Stack gap={2}>
            <Text variant="label" color={colors.gray[500]}>Sizes</Text>
            <DemoRow gap={4} align="flex-end">
              <Input inputSize="sm" placeholder="Small" />
              <Input inputSize="md" placeholder="Medium (default)" />
              <Input inputSize="lg" placeholder="Large" />
            </DemoRow>
          </Stack>
          <Stack gap={2}>
            <Text variant="label" color={colors.gray[500]}>States</Text>
            <DemoGrid>
              <DemoColumn gap={3}>
                <Input label="기본" placeholder="텍스트를 입력하세요" />
                <Input label="에러" error="올바른 형식이 아닙니다" defaultValue="wrong@" />
              </DemoColumn>
              <DemoColumn gap={3}>
                <Input label="도움말" helperText="8자 이상 입력해주세요" type="password" placeholder="비밀번호" />
                <Input label="비활성화" disabled placeholder="수정할 수 없습니다" />
              </DemoColumn>
            </DemoGrid>
          </Stack>
        </Stack>
      </Card>

      {/* Textarea */}
      <Card as="section">
        <SectionHeader>Textarea</SectionHeader>
        <ConstrainedWidth>
          <Stack gap={4}>
            <Textarea label="내용" placeholder="내용을 입력하세요" rows={3} />
            <Textarea label="에러" error="필수 항목입니다" rows={3} />
            <Textarea label="resize 없음" resize="none" placeholder="크기 조절 불가" rows={3} />
            <Textarea label="비활성화" disabled placeholder="수정할 수 없습니다" rows={3} />
          </Stack>
        </ConstrainedWidth>
      </Card>

      {/* Select */}
      <Card as="section">
        <SectionHeader>Select</SectionHeader>
        <SelectWidth>
          <Stack gap={4}>
            <Select label="역할" defaultValue="">
              <option value="" disabled>선택하세요</option>
              <option value="admin">관리자</option>
              <option value="manager">매니저</option>
              <option value="viewer">뷰어</option>
            </Select>
            <Select label="에러" error="항목을 선택해주세요" defaultValue="">
              <option value="" disabled>선택하세요</option>
            </Select>
            <Select label="비활성화" disabled defaultValue="admin">
              <option value="admin">관리자</option>
            </Select>
          </Stack>
        </SelectWidth>
      </Card>

      {/* Checkbox & Switch & Radio */}
      <Card as="section">
        <SectionHeader>Checkbox / Switch / Radio</SectionHeader>
        <DemoGrid>
          <Stack gap={2}>
            <Text variant="label" color={colors.gray[500]}>Checkbox</Text>
            <Stack gap={3}>
              <Checkbox label="이메일 알림 수신" defaultChecked />
              <Checkbox label="SMS 알림 수신" />
              <Checkbox label="비활성화" disabled />
              <Checkbox label="비활성화 + 체크" disabled defaultChecked />
              <Checkbox label="약관에 동의합니다" error="필수 항목입니다" />
            </Stack>
          </Stack>
          <Stack gap={2}>
            <Text variant="label" color={colors.gray[500]}>Switch</Text>
            <Stack gap={3}>
              <Switch label="이메일 알림" defaultChecked />
              <Switch label="SMS 알림" />
              <Switch label="비활성화" disabled />
              <Switch label="비활성화 + 켜짐" disabled defaultChecked />
              <Switch label="Small" size="sm" defaultChecked />
            </Stack>
          </Stack>
          <Stack gap={2}>
            <Text variant="label" color={colors.gray[500]}>Radio</Text>
            <RadioGroup label="배송 방법">
              <Radio name="shipping" value="standard" label="일반 배송 (3-5일)" defaultChecked />
              <Radio name="shipping" value="express" label="빠른 배송 (1-2일)" />
              <Radio name="shipping" value="same" label="당일 배송" />
              <Radio name="shipping" value="pickup" label="직접 수령" disabled />
            </RadioGroup>
          </Stack>
        </DemoGrid>
      </Card>
    </Stack>
  )
}
