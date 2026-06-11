import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.vanniktech.mavenPublish)
}

group = "io.github.chandu4221"
version = "3.1.0"

kotlin {
    jvm()
    androidLibrary {
        namespace = "io.github.chandu4221.kvality"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        withHostTestBuilder {}.configure {}
        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
    }
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            //put your multiplatform dependencies here
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

mavenPublishing {
    publishToMavenCentral()

    signAllPublications()

    coordinates(group.toString(), "kvality-core", version.toString())


    pom {
        name = "Kvality"
        description = "Schema-first validation for Kotlin Multiplatform"
        inceptionYear = "2025"
        url = "https://github.com/chandu4221/kvality"
        licenses {
            license {
                name = "Apache-2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0"
                distribution = "repo"
            }
        }
        developers {
            developer {
                id = "chandu4221"
                name = "Chandrashekhar"
                url = "https://github.com/chandu4221"
            }
        }
        scm {
            url = "https://github.com/chandu4221/kvality"
            connection = "scm:git:git://github.com/chandu4221/kvality.git"
            developerConnection = "scm:git:ssh://git@github.com/chandu4221/kvality.git"
        }
    }
}
