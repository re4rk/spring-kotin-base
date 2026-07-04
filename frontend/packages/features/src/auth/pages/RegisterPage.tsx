import { useState } from 'react'
import type { FormEvent, ChangeEvent } from 'react'
import styled from '@emotion/styled'
import { useNavigate } from 'react-router-dom'
import { Stack, Card, Text, Input, Button, Alert, colors, typography, radii, spacing } from '@base/ui'
import { useRegisterMutation } from '../mutations'

const RegisterCard = styled(Card)({
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

const LoginLink = styled.span({
  fontSize: typography.fontSize.sm,
  color: colors.primary[500],
  cursor: 'pointer',
  fontFamily: typography.fontFamily.sans,
  fontWeight: typography.fontWeight.semibold,
  '&:hover': { color: colors.primary[600] },
})

export function RegisterPage() {
  const navigate = useNavigate()
  const [email, setEmail] = useState('')
  const [name, setName] = useState('')
  const [password, setPassword] = useState('')
  const [confirmPassword, setConfirmPassword] = useState('')
  const [validationError, setValidationError] = useState<string | null>(null)

  const { mutate: register, isPending, error } = useRegisterMutation()

  const handleSubmit = (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault()
    setValidationError(null)

    if (password !== confirmPassword) {
      setValidationError('비밀번호가 일치하지 않습니다.')
      return
    }

    register({ email, name, password }, {
      onSuccess: () => navigate('/auth/login', { state: { registered: true } }),
    })
  }

  const displayError = validationError ?? error?.message ?? null

  return (
    <RegisterCard padding={8}>
      <Stack gap={6}>

        {/* Logo + title */}
        <Stack gap={4}>
          <Logo>B</Logo>
          <Stack gap={1}>
            <Text variant="h3">회원가입</Text>
            <Text variant="body2" color={colors.gray[500]}>
              Base 계정을 만들어 시작하세요
            </Text>
          </Stack>
        </Stack>

        {/* Error */}
        {displayError && <Alert variant="danger">{displayError}</Alert>}

        {/* Form */}
        <Form onSubmit={handleSubmit}>
          <Input
            label="이름"
            type="text"
            placeholder="홍길동"
            value={name}
            onChange={(e: ChangeEvent<HTMLInputElement>) => setName(e.target.value)}
            autoComplete="name"
            fullWidth
            required
          />
          <Input
            label="이메일"
            type="email"
            placeholder="name@company.com"
            value={email}
            onChange={(e: ChangeEvent<HTMLInputElement>) => setEmail(e.target.value)}
            autoComplete="email"
            fullWidth
            required
          />
          <Input
            label="비밀번호"
            type="password"
            placeholder="비밀번호를 입력하세요"
            value={password}
            onChange={(e: ChangeEvent<HTMLInputElement>) => setPassword(e.target.value)}
            autoComplete="new-password"
            fullWidth
            required
          />
          <Input
            label="비밀번호 확인"
            type="password"
            placeholder="비밀번호를 다시 입력하세요"
            value={confirmPassword}
            onChange={(e: ChangeEvent<HTMLInputElement>) => setConfirmPassword(e.target.value)}
            autoComplete="new-password"
            fullWidth
            required
          />
          <Button type="submit" fullWidth loading={isPending}>
            가입하기
          </Button>
        </Form>

        {/* Login */}
        <Stack direction="row" gap={1} justify="center">
          <Text variant="body2" color={colors.gray[500]}>이미 계정이 있으신가요?</Text>
          <LoginLink onClick={() => navigate('/auth/login')}>로그인</LoginLink>
        </Stack>

      </Stack>
    </RegisterCard>
  )
}
