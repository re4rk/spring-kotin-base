import { useEffect, useRef } from 'react'
import styled from '@emotion/styled'
import { useNavigate } from 'react-router-dom'
import type { ProductResponse, ProductStatus } from '@base/api'
import {
  Stack,
  Text,
  Button,
  Card,
  Badge,
  Alert,
  Spinner,
  colors,
  spacing,
  typography,
  radii,
} from '@base/ui'
import { useProductsInfiniteQuery } from '../queries'

const PageHeader = styled.div({
  display: 'flex',
  alignItems: 'flex-start',
  justifyContent: 'space-between',
  gap: spacing[4],
  flexWrap: 'wrap',
})

const ProductGrid = styled.div({
  display: 'grid',
  gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))',
  gap: spacing[4],
})

const ProductCard = styled(Card)({
  display: 'flex',
  flexDirection: 'column',
  gap: spacing[3],
  height: '100%',
})

const Thumbnail = styled.div<{ src?: string | null }>(({ src }) => ({
  width: '100%',
  aspectRatio: '16 / 10',
  borderRadius: radii.md,
  background: src
    ? `center / cover no-repeat url(${src})`
    : `linear-gradient(135deg, ${colors.gray[100]}, ${colors.gray[200]})`,
  border: `1px solid ${colors.gray[200]}`,
}))

const ThumbnailPlaceholder = styled.div({
  width: '100%',
  aspectRatio: '16 / 10',
  borderRadius: radii.md,
  background: `linear-gradient(135deg, ${colors.gray[100]}, ${colors.gray[200]})`,
  border: `1px solid ${colors.gray[200]}`,
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  color: colors.gray[400],
  fontSize: typography.fontSize.sm,
  fontFamily: typography.fontFamily.sans,
})

const CardTop = styled.div({
  display: 'flex',
  alignItems: 'flex-start',
  justifyContent: 'space-between',
  gap: spacing[2],
})

const Price = styled.span({
  fontSize: typography.fontSize.lg,
  fontWeight: typography.fontWeight.bold,
  fontFamily: typography.fontFamily.sans,
  color: colors.gray[900],
})

const Meta = styled.div({
  display: 'flex',
  flexWrap: 'wrap',
  gap: spacing[2],
  fontSize: typography.fontSize.xs,
  color: colors.gray[500],
  fontFamily: typography.fontFamily.sans,
})

const Sentinel = styled.div({
  height: '1px',
})

const LoadMore = styled.div({
  display: 'flex',
  justifyContent: 'center',
  padding: spacing[6],
})

const EndMessage = styled(Text)({
  textAlign: 'center',
})

const STATUS_LABEL: Record<ProductStatus, string> = {
  DRAFT: '초안',
  PENDING: '검토중',
  ON_SALE: '판매중',
  SOLD_OUT: '품절',
  DISCONTINUED: '단종',
}

const STATUS_VARIANT: Record<ProductStatus, 'ghost' | 'warning' | 'success' | 'danger' | 'secondary'> = {
  DRAFT: 'ghost',
  PENDING: 'warning',
  ON_SALE: 'success',
  SOLD_OUT: 'danger',
  DISCONTINUED: 'secondary',
}

function formatPrice(price: number) {
  return `${price.toLocaleString('ko-KR')}원`
}

function ProductItem({ product }: { product: ProductResponse }) {
  return (
    <ProductCard padding={4}>
      {product.thumbnailUrl ? (
        <Thumbnail src={product.thumbnailUrl} aria-hidden />
      ) : (
        <ThumbnailPlaceholder>이미지 없음</ThumbnailPlaceholder>
      )}

      <Stack gap={2}>
        <CardTop>
          <Text variant="h4">{product.name}</Text>
          <Badge variant={STATUS_VARIANT[product.status]} size="sm">
            {STATUS_LABEL[product.status]}
          </Badge>
        </CardTop>

        <Price>{formatPrice(product.price)}</Price>

        {product.description && (
          <Text variant="body2" color={colors.gray[600]}>
            {product.description}
          </Text>
        )}

        <Meta>
          {product.category && <span>{product.category}</span>}
          <span>재고 {product.stock.toLocaleString('ko-KR')}</span>
          <span>ID {product.id}</span>
        </Meta>
      </Stack>
    </ProductCard>
  )
}

export function ProductListPage() {
  const navigate = useNavigate()
  const sentinelRef = useRef<HTMLDivElement>(null)
  const {
    data,
    error,
    isLoading,
    isFetchingNextPage,
    hasNextPage,
    fetchNextPage,
  } = useProductsInfiniteQuery()

  const products = data?.pages.flatMap((page) => page.content) ?? []
  const totalCount = data?.pages[0]?.totalElements

  useEffect(() => {
    const sentinel = sentinelRef.current
    if (!sentinel || !hasNextPage || isFetchingNextPage) return

    const observer = new IntersectionObserver(
      (entries) => {
        if (entries[0]?.isIntersecting) {
          fetchNextPage()
        }
      },
      { rootMargin: '240px' },
    )

    observer.observe(sentinel)
    return () => observer.disconnect()
  }, [fetchNextPage, hasNextPage, isFetchingNextPage, products.length])

  return (
    <Stack gap={6}>
      <PageHeader>
        <Stack gap={1}>
          <Text variant="h2">상품 목록</Text>
          <Text variant="body2" color={colors.gray[500]}>
            {totalCount != null ? `총 ${totalCount.toLocaleString('ko-KR')}개` : '등록된 상품을 불러옵니다.'}
          </Text>
        </Stack>
        <Button onClick={() => navigate('/products/new')}>상품 등록</Button>
      </PageHeader>

      {error && <Alert variant="danger">{error.message}</Alert>}

      {isLoading && (
        <LoadMore>
          <Spinner size="md" />
        </LoadMore>
      )}

      {!isLoading && products.length === 0 && (
        <Card padding={6}>
          <Stack gap={3} align="center">
            <Text variant="body1" color={colors.gray[600]}>
              등록된 상품이 없습니다.
            </Text>
            <Button onClick={() => navigate('/products/new')}>첫 상품 등록하기</Button>
          </Stack>
        </Card>
      )}

      {products.length > 0 && (
        <ProductGrid>
          {products.map((product) => (
            <ProductItem key={product.id} product={product} />
          ))}
        </ProductGrid>
      )}

      <Sentinel ref={sentinelRef} />

      {isFetchingNextPage && (
        <LoadMore>
          <Spinner size="md" />
        </LoadMore>
      )}

      {!isLoading && !hasNextPage && products.length > 0 && (
        <EndMessage variant="body2" color={colors.gray[400]}>
          모든 상품을 불러왔습니다.
        </EndMessage>
      )}
    </Stack>
  )
}
