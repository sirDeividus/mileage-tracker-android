// build.gradle.kts (nivel de PROYECTO, no de app)
// -----------------------------------------------------------------------
// Aquí solo declaramos qué "plugins" de Gradle van a estar disponibles
// para los módulos del proyecto (en nuestro caso, el módulo "app").
// No se aplican aquí directamente (apply false), cada módulo decide
// cuáles usar en su propio build.gradle.kts.
// -----------------------------------------------------------------------

plugins {
    id("com.android.application") version "8.5.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.24" apply false
    id("com.google.devtools.ksp") version "1.9.24-1.0.20" apply false
}
