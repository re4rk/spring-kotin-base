package com.ark.base.config

import com.ark.base.product.Product
import com.ark.base.product.ProductRepository
import com.ark.base.product.ProductStatus
import com.ark.base.product.option.ProductOption
import com.ark.base.product.option.ProductOptionGroup
import com.ark.base.product.option.ProductSku
import com.ark.base.user.PasswordEncoder
import com.ark.base.user.User
import com.ark.base.user.UserRepository
import com.ark.base.user.UserRole
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Profile("local")
@Component
class LocalDataInitializer(
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
    private val passwordEncoder: PasswordEncoder,
) : ApplicationRunner {
    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    override fun run(args: ApplicationArguments) {
        seedUsers()
        seedProducts()
    }

    private fun seedUsers() {
        if (userRepository.count() > 0) {
            log.info("[Seed] 유저 데이터 이미 존재 — skip")
            return
        }
        val admin =
            User(email = "admin@test.com", name = "관리자", password = "password123!", passwordEncoder = passwordEncoder)
                .also { it.role = UserRole.ADMIN }
        val users =
            listOf(
                admin,
                User(email = "user1@test.com", name = "테스트유저1", password = "password123!", passwordEncoder = passwordEncoder),
                User(email = "user2@test.com", name = "테스트유저2", password = "password123!", passwordEncoder = passwordEncoder),
            )
        userRepository.saveAll(users)
        log.info("[Seed] 유저 {}명 생성 완료", users.size)
    }

    private fun seedProducts() {
        if (productRepository.count() > 0) {
            log.info("[Seed] 상품 데이터 이미 존재 — skip")
            return
        }
        val products = listOf(buildTshirt(), buildJeans(), buildHoodie())
        productRepository.saveAll(products)
        log.info("[Seed] 상품 {}개 생성 완료", products.size)
    }

    private fun buildTshirt(): Product {
        val product =
            Product(
                name = "베이직 티셔츠",
                price = 29_000,
                description = "사계절 입기 좋은 베이직 티셔츠",
                category = "상의",
                status = ProductStatus.ON_SALE,
            )

        val colorGroup = ProductOptionGroup(name = "색상", sortOrder = 0)
        val white = ProductOption(optionGroup = colorGroup, name = "화이트", sortOrder = 0)
        val black = ProductOption(optionGroup = colorGroup, name = "블랙", sortOrder = 1)
        colorGroup.addOption(white)
        colorGroup.addOption(black)

        val sizeGroup = ProductOptionGroup(name = "사이즈", sortOrder = 1)
        val sizeS = ProductOption(optionGroup = sizeGroup, name = "S", sortOrder = 0)
        val sizeM = ProductOption(optionGroup = sizeGroup, name = "M", sortOrder = 1)
        val sizeL = ProductOption(optionGroup = sizeGroup, name = "L", sortOrder = 2)
        sizeGroup.addOption(sizeS)
        sizeGroup.addOption(sizeM)
        sizeGroup.addOption(sizeL)

        product.addOptionGroup(colorGroup)
        product.addOptionGroup(sizeGroup)

        product.addSku(ProductSku(options = mutableListOf(white, sizeS), stock = 10))
        product.addSku(ProductSku(options = mutableListOf(white, sizeM), stock = 20))
        product.addSku(ProductSku(options = mutableListOf(white, sizeL), stock = 15))
        product.addSku(ProductSku(options = mutableListOf(black, sizeS), stock = 10))
        product.addSku(ProductSku(options = mutableListOf(black, sizeM), stock = 25))
        product.addSku(ProductSku(options = mutableListOf(black, sizeL), stock = 20))

        return product
    }

    private fun buildJeans(): Product {
        val product =
            Product(
                name = "슬림핏 청바지",
                price = 79_000,
                description = "편안한 슬림핏 청바지",
                category = "하의",
                status = ProductStatus.ON_SALE,
            )

        val sizeGroup = ProductOptionGroup(name = "사이즈", sortOrder = 0)
        val size28 = ProductOption(optionGroup = sizeGroup, name = "28", sortOrder = 0)
        val size30 = ProductOption(optionGroup = sizeGroup, name = "30", sortOrder = 1)
        val size32 = ProductOption(optionGroup = sizeGroup, name = "32", sortOrder = 2)
        sizeGroup.addOption(size28)
        sizeGroup.addOption(size30)
        sizeGroup.addOption(size32)

        product.addOptionGroup(sizeGroup)

        product.addSku(ProductSku(options = mutableListOf(size28), stock = 15))
        product.addSku(ProductSku(options = mutableListOf(size30), stock = 30))
        product.addSku(ProductSku(options = mutableListOf(size32), stock = 20))

        return product
    }

    private fun buildHoodie(): Product =
        Product(
            name = "기본 후드티",
            price = 59_000,
            description = "편안한 기본 후드티",
            category = "상의",
        )
}
