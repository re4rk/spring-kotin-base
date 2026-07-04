import { useState } from 'react'
import type { FormEvent, ChangeEvent } from 'react'
import styled from '@emotion/styled'
import { useNavigate } from 'react-router-dom'
import {
  Stack,
  Text,
  Input,
  Textarea,
  Button,
  Card,
  Alert,
  colors,
  spacing,
} from '@base/ui'
import { useCreateProductMutation } from '../mutations'

const Form = styled.form({
  display: 'flex',
  flexDirection: 'column',
  gap: spacing[4],
})

const FormRow = styled.div({
  display: 'grid',
  gridTemplateColumns: '1fr 1fr',
  gap: spacing[4],
  '@media (max-width: 640px)': {
    gridTemplateColumns: '1fr',
  },
})

const Actions = styled.div({
  display: 'flex',
  gap: spacing[3],
  justifyContent: 'flex-end',
  flexWrap: 'wrap',
})

function emptyToUndefined(value: string): string | undefined {
  const trimmed = value.trim()
  return trimmed.length > 0 ? trimmed : undefined
}

export function CreateProductPage() {
  const navigate = useNavigate()
  const { mutate: createProduct, isPending, error, isSuccess, data } = useCreateProductMutation()

  const [name, setName] = useState('')
  const [price, setPrice] = useState('')
  const [description, setDescription] = useState('')
  const [category, setCategory] = useState('')
  const [thumbnailUrl, setThumbnailUrl] = useState('')
  const [validationError, setValidationError] = useState<string | null>(null)

  const handleSubmit = (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault()
    setValidationError(null)

    const trimmedName = name.trim()
    if (!trimmedName) {
      setValidationError('상품명을 입력해 주세요.')
      return
    }

    const parsedPrice = Number(price.replace(/,/g, ''))
    if (!Number.isFinite(parsedPrice) || parsedPrice <= 0 || !Number.isInteger(parsedPrice)) {
      setValidationError('가격은 1원 이상의 정수로 입력해 주세요.')
      return
    }

    createProduct({
      name: trimmedName,
      price: parsedPrice,
      description: emptyToUndefined(description),
      category: emptyToUndefined(category),
      thumbnailUrl: emptyToUndefined(thumbnailUrl),
    })
  }

  const displayError = validationError ?? error?.message ?? null

  return (
    <Stack gap={6}>
      <Stack gap={1}>
        <Text variant="h2">상품 등록</Text>
        <Text variant="body2" color={colors.gray[500]}>
          새 상품을 등록합니다. 등록 후 DRAFT 상태로 저장됩니다.
        </Text>
      </Stack>

      {isSuccess && data && (
        <Alert variant="success">
          「{data.name}」 상품이 등록되었습니다. (ID: {data.id}, 상태: {data.status})
        </Alert>
      )}

      {displayError && <Alert variant="danger">{displayError}</Alert>}

      <Card padding={6}>
        <Form onSubmit={handleSubmit}>
          <Input
            label="상품명"
            value={name}
            onChange={(e: ChangeEvent<HTMLInputElement>) => setName(e.target.value)}
            placeholder="예: 베이스 티셔츠"
            required
            disabled={isPending}
          />

          <FormRow>
            <Input
              label="가격 (원)"
              type="number"
              min={1}
              step={1}
              value={price}
              onChange={(e: ChangeEvent<HTMLInputElement>) => setPrice(e.target.value)}
              placeholder="9900"
              required
              disabled={isPending}
            />
            <Input
              label="카테고리"
              value={category}
              onChange={(e: ChangeEvent<HTMLInputElement>) => setCategory(e.target.value)}
              placeholder="예: 의류"
              disabled={isPending}
            />
          </FormRow>

          <Textarea
            label="설명"
            value={description}
            onChange={(e: ChangeEvent<HTMLTextAreaElement>) => setDescription(e.target.value)}
            placeholder="상품 설명을 입력하세요"
            rows={4}
            disabled={isPending}
          />

          <Input
            label="썸네일 URL"
            type="url"
            value={thumbnailUrl}
            onChange={(e: ChangeEvent<HTMLInputElement>) => setThumbnailUrl(e.target.value)}
            placeholder="https://example.com/image.jpg"
            disabled={isPending}
          />

          <Actions>
            <Button type="button" variant="ghost" onClick={() => navigate('/dashboard')} disabled={isPending}>
              취소
            </Button>
            <Button type="submit" loading={isPending}>
              등록
            </Button>
          </Actions>
        </Form>
      </Card>
    </Stack>
  )
}
