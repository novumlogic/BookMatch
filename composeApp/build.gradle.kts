import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.google.services)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    sourceSets {

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.activity.ktx)
            implementation(libs.androidx.credentials)
            implementation(libs.googleid)
            implementation(libs.play.services.auth)
            // optional - needed for credentials support from play services, for devices running
            // Android 13 and below.
            implementation(libs.androidx.credentials.play.services.auth)

            // Import the BoM for the Firebase platform
            implementation(project.dependencies.platform(libs.firebase.bom))
            implementation(libs.firebase.analytics)

        }
        commonMain.dependencies {
            val composeBom = project.dependencies.platform(libs.compose.bom)
            implementation(composeBom)

            implementation(libs.androidx.material3)
            implementation(libs.androidx.foundation)
            implementation(libs.ui)

            implementation(libs.androidx.ui.tooling.preview)

            implementation(libs.coil.compose)
            implementation(libs.androidx.datastore.preferences)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.androidx.lifecycle.viewmodel.compose)

            implementation(libs.androidx.material3.window.size)
            implementation(projects.shared)
            implementation(libs.gotrue.kt)
        }
    }
}

android {

    namespace = "com.novumlogic.bookmatch"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "com.novumlogic.bookmatch"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }

}

