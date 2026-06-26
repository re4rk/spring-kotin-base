import { Stack, Card, Text, Tabs, TabList, Tab, TabPanel, Badge, colors } from '@base/ui'
import { SectionHeader } from '../components/SectionHeader.tsx'

export function NavigationPage() {
  return (
    <Stack gap={6}>
      <Stack gap={1}>
        <Text variant="h2">Navigation</Text>
        <Text variant="body1" color={colors.gray[500]}>콘텐츠 탐색을 위한 네비게이션 컴포넌트입니다.</Text>
      </Stack>

      <Card as="section">
        <SectionHeader>Tabs — 기본</SectionHeader>
        <Tabs defaultTab="overview">
          <TabList>
            <Tab id="overview">개요</Tab>
            <Tab id="details">상세 정보</Tab>
            <Tab id="history">변경 이력</Tab>
            <Tab id="settings" disabled>설정</Tab>
          </TabList>
          <TabPanel id="overview">
            <Stack gap={2}>
              <Text variant="body1">개요 패널 내용입니다.</Text>
              <Text variant="body2" color={colors.gray[500]}>탭을 클릭해 콘텐츠를 전환하세요.</Text>
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

      <Card as="section">
        <SectionHeader>Tabs — Badge와 함께</SectionHeader>
        <Tabs defaultTab="all">
          <TabList>
            <Tab id="all">전체</Tab>
            <Tab id="pending">
              <Stack direction="row" gap={2} align="center">
                처리 대기
                <Badge variant="danger" size="sm">3</Badge>
              </Stack>
            </Tab>
            <Tab id="done">완료</Tab>
          </TabList>
          <TabPanel id="all">
            <Text variant="body1">전체 항목 목록</Text>
          </TabPanel>
          <TabPanel id="pending">
            <Stack gap={2}>
              <Text variant="body1">처리 대기 중인 항목 3건</Text>
              <Text variant="body2" color={colors.gray[500]}>확인이 필요한 항목이 있습니다.</Text>
            </Stack>
          </TabPanel>
          <TabPanel id="done">
            <Text variant="body1">완료된 항목 목록</Text>
          </TabPanel>
        </Tabs>
      </Card>
    </Stack>
  )
}
