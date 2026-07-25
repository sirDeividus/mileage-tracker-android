// app/build.gradle.kts
// -----------------------------------------------------------------------
// Este es el archivo MÁS IMPORTANTE de configuración: define cómo se
// compila nuestra app y qué librerías (dependencias) usamos.
// Es el equivalente de "package.json" en el mundo de Node/React.
// -----------------------------------------------------------------------

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp") // Necesario para que Room genere código automáticamente
}

android {
    namespace = "com.tuusuario.mileagetracker"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.tuusuario.mileagetracker"
        minSdk = 26          // Android 8.0 en adelante (cubre +95% de dispositivos activos)
        targetSdk = 34
        versionCode = 4
        versionName = "2.3"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    // Habilita Jetpack Compose (el framework de UI declarativa moderno de Android,
    // equivalente conceptual a como escribíamos componentes en React)
    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
}

dependencies {
    // ---- Núcleo de Android / Kotlin ----
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
    implementation("androidx.activity:activity-compose:1.9.1")

    // ---- Jetpack Compose (UI) ----
    implementation(platform("androidx.compose:compose-bom:2024.06.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    // ---- Navegación entre pantallas (equivalente a React Navigation) ----
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // ---- ViewModel integrado con Compose (maneja el estado de cada pantalla) ----
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4")

    // ---- Room: base de datos local (equivalente a AsyncStorage, pero con SQL real) ----
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // ---- Ubicación GPS (Google Play Services) ----
    implementation("com.google.android.gms:play-services-location:21.3.0")

    // ---- Corrutinas de Kotlin (para tareas asíncronas: GPS, base de datos) ----
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    // ---- Pruebas ----
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}
