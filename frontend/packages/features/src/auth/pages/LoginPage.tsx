import { useState } from 'react'
import type { FormEvent, ChangeEvent } from 'react'
import styled from '@emotion/styled'
import { useNavigate, useLocation } from 'react-router-dom'
import { Stack, Card, Text, Input, Button, Alert, colors, typography, radii, spacing } from '@base/ui'
import { useLoginMutation } from '../mutations'

const LoginCard = styled(Card)({
  width: '100%',
  maxWidth: '400px',
})

const Logo = styled.div({
  width: '48px',
  height: '48px',
  borderRadius: radii.xl,
  background: colors.primary[500],
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  color: '#fff',
  fontSize: typography.fontSize.xl,
  fontWeight: typography.fontWeight.bold,
  fontFamily: typography.fontFamily.sans,
  flexShrink: 0,
})

const Form = styled.form({
  display: 'flex',
  flexDirection: 'column',
  gap: spacing[3],
})

const ForgotLink = styled.span({
  fontSize: typography.fontSize.xs,
  color: colors.primary[500],
  cursor: 'pointer',
  fontFamily: typography.fontFamily.sans,
  '&:hover': { color: colors.primary[600] },
})

const RegisterLink = styled.span({
  fontSize: typography.fontSize.sm,
  color: colors.primary[500],
  cursor: 'pointer',
  fontFamily: typography.fontFamily.sans,
  fontWeight: typography.fontWeight.semibold,
  '&:hover': { color: colors.primary[600] },
})

export function LoginPage() {
  const navigate = useNavigate()
  const location = useLocation()
  const registered = location.state?.registered === true

  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const { mutate: login, isPending, error } = useLoginMutation()

  const handleSubmit = (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault()
    login({ email, password })
  }

  return (
    <LoginCard padding={8}>
      <Stack gap={6}>

        {/* Logo + title */}
        <Stack gap={4}>
          <Logo>B</Logo>
          <Stack gap={1}>
            <Text variant="h3">로그인</Text>
            <Text variant="body2" color={colors.gray[500]}>
              Base에 오신 것을 환영합니다
            </Text>
          </Stack>
        </Stack>

        {/* Success / Error */}
        {registered && <Alert variant="success">회원가입이 완료됐습니다. 로그인해주세요.</Alert>}
        {error && <Alert variant="danger">{error.message}</Alert>}

        {/* Form */}
        <Form onSubmit={handleSubmit}>
          <Input
            label="이메일"
            type="email"
            placeholder="name@company.com"
            value={email}
            onChange={(e: ChangeEvent<HTMLInputElement>) => setEmail(e.target.value)}
            autoComplete="email"
            fullWidth
          />
          <Stack gap={1}>
            <Input
              label="비밀번호"
              type="password"
              placeholder="비밀번호를 입력하세요"
              value={password}
              onChange={(e: ChangeEvent<HTMLInputElement>) => setPassword(e.target.value)}
              autoComplete="current-password"
              fullWidth
            />
            <Stack direction="row" justify="flex-end">
              <ForgotLink>비밀번호를 잊으셨나요?</ForgotLink>
            </Stack>
          </Stack>
          <Button type="submit" fullWidth loading={isPending}>
            로그인
          </Button>
        </Form>

        {/* Register */}
        <Stack direction="row" gap={1} justify="center">
          <Text variant="body2" color={colors.gray[500]}>아직 계정이 없으신가요?</Text>
          <RegisterLink onClick={() => navigate('/auth/register')}>회원가입</RegisterLink>
        </Stack>

      </Stack>
    </LoginCard>
  )
}
