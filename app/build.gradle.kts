plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    // KSP genera el código de Room (DAOs y base de datos) en tiempo de compilación
    alias(libs.plugins.ksp)
}

android {
    namespace = "cl.duoc.juntazaror"
    compileSdk = 35

    defaultConfig {
        applicationId = "cl.duoc.juntazaror"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    testOptions {
        // Permite usar clases de Android (ej: Log) en las pruebas unitarias sin que fallen
        unitTests.isReturnDefaultValues = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

// Guía 15: Kotest corre sobre la plataforma JUnit 5
tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

dependencies {
    // Núcleo Android + ciclo de vida
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.kotlinx.coroutines.android)

    // Jetpack Compose + Material Design 3
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material3.window.size)
    implementation(libs.androidx.material.icons.extended)

    // Navegación entre pantallas
    implementation(libs.androidx.navigation.compose)

    // Persistencia local: Room (SQLite) + DataStore Preferences
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.datastore.preferences)

    // Carga de imágenes (cámara / galería) desde un Uri
    implementation(libs.coil.compose)

    // Guía 14: consumo de API REST (Retrofit + conversor JSON Gson)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)

    // Guía 15: pruebas unitarias (Kotest + JUnit 5 + MockK + corrutinas)
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.junit5.jupiter)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)

    // Guía 15: pruebas de interfaz (Compose + JUnit 4, se ejecutan en el emulador/celular)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
