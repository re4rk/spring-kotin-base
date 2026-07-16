package com.ark

import com.tngtech.archunit.base.DescribedPredicate
import com.tngtech.archunit.core.domain.JavaClass
import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses

@AnalyzeClasses(packages = ["com.ark"], importOptions = [ImportOption.DoNotIncludeTests::class])
class PackageDependencyTest {
    @ArchTest
    val baseMustNotDependOnOtherArkPackages: ArchRule =
        noClasses()
            .that()
            .resideInAPackage("com.ark.base..")
            .should()
            .dependOnClassesThat(otherArkPackagesThan("base"))
            .because("com.ark.base must not depend on other com.ark.* packages")

    @ArchTest
    fun featurePackagesMayOnlyDependOnBaseAmongArkPackages(classes: JavaClasses) {
        featurePackageNames(classes).forEach { feature ->
            noClasses()
                .that()
                .resideInAPackage("com.ark.$feature..")
                .should()
                .dependOnClassesThat(otherArkPackagesThan("base", feature))
                .because("com.ark.$feature may only depend on com.ark.base among com.ark.* packages")
                .check(classes)
        }
    }

    private fun featurePackageNames(classes: JavaClasses): Set<String> =
        classes
            .asSequence()
            .map { it.packageName }
            .filter { it.startsWith("com.ark.") }
            .map { it.removePrefix("com.ark.").substringBefore('.') }
            .filter { it.isNotEmpty() && it != "base" }
            .toSet()

    private fun otherArkPackagesThan(vararg allowed: String): DescribedPredicate<JavaClass> {
        val allowedSet = allowed.toSet()
        return object : DescribedPredicate<JavaClass>("reside in other com.ark.* packages than $allowedSet") {
            override fun test(input: JavaClass): Boolean {
                val pkg = input.packageName
                if (!pkg.startsWith("com.ark.")) return false
                val segment = pkg.removePrefix("com.ark.").substringBefore('.')
                // com.ark.ArkApplication etc. (exactly com.ark) is allowed as composition root
                if (segment.isEmpty()) return false
                return segment !in allowedSet
            }
        }
    }
}
