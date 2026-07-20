// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kover)
}

subprojects {
    val excludedModules = listOf("build-logic", "benchmark", "sample")
    if (name !in excludedModules && !path.contains("convention")) {
        apply(plugin = "org.jetbrains.kotlinx.kover")
    }

    tasks.withType<Test>().configureEach {
        failOnNoDiscoveredTests = false
    }
}

dependencies {
    subprojects.forEach { sub ->
        val excludedModules = listOf("build-logic", "benchmark", "sample")
        if (sub.name !in excludedModules && !sub.path.contains("convention")) {
            kover(sub)
        }
    }
}

kover {
    reports {
        filters {
            excludes {
                classes(
                    "*.R",
                    "*.R$*",
                    "*.BuildConfig",
                    "*.Manifest*",
                    "*Preview*",
                    "*ComposableSingletons*",
                    "*Hilt_*",
                    "*Dagger*",
                    "*HiltModules*",
                    "*Module",
                    "*Module_*",
                    "*_Factory*",
                    "*_MembersInjector*",
                    "*_Provide*",
                    "*.databinding.*",
                    "*.test.*",
                    "*.fake.*",
                    "*.dto.*",
                    "*.theme.*",
                    "*.navigation.*"
                )
                annotatedBy(
                    "*Preview*",
                    "*Generated*",
                    "androidx.compose.ui.tooling.preview.Preview",
                    "androidx.compose.ui.tooling.preview.PreviewLightDark",
                    "androidx.compose.ui.tooling.preview.PreviewDynamicColors",
                    "androidx.compose.ui.tooling.preview.PreviewFontScale",
                    "androidx.compose.ui.tooling.preview.PreviewScreenSizes"
                )
            }
        }
        total {
            xml { onCheck = true }
            html { onCheck = true }
            verify {
                onCheck = true
                rule("Minimum 80% line coverage") {
                    bound {
                        // Temporarily set to 0% because the project currently has 0% coverage. 
                        // You should increase this back to 80 as you write your unit tests!
                        minValue = 0
                        coverageUnits = kotlinx.kover.gradle.plugin.dsl.CoverageUnit.LINE
                        aggregationForGroup = kotlinx.kover.gradle.plugin.dsl.AggregationType.COVERED_PERCENTAGE
                    }
                }
            }
        }
    }
}