# 🚗 Mileage Tracker (Android nativo) — Rastreador de Millas para Deducción de Impuestos (NC / IRS)

App **100% nativa de Android**, escrita en **Kotlin + Jetpack Compose**, que rastrea por GPS las millas recorridas durante viajes de trabajo y calcula una **estimación de deducción de impuestos** usando la tasa estándar de millaje del IRS, con notas específicas para **North Carolina (NC)**.

> ⚠️ **Versión 1.0 (primera versión / MVP).** Proyecto personal de portafolio — no es un producto financiero certificado. Ver [Aviso legal](#-aviso-legal).

---

## 📱 Funcionalidades

- **Start Work / Stop Work**: un botón para iniciar y detener el rastreo GPS de un viaje.
- **Millas en tiempo real** mientras conduces, calculadas con la fórmula de Haversine.
- **Cálculo automático de deducción** usando las tasas oficiales del IRS (incluye el cambio de tasa de julio 2026).
- **Historial de viajes** guardado en una base de datos local real (Room/SQLite).
- **Resumen fiscal** por mes / trimestre / año, con notas sobre cómo aplica en NC.
- Interfaz moderna con **Material 3** y Jetpack Compose.

---

## 🧱 Stack tecnológico

| Capa | Tecnología |
|---|---|
| Lenguaje | [Kotlin](https://kotlinlang.org/) |
| UI | [Jetpack Compose](https://developer.android.com/jetpack/compose) + Material 3 |
| Navegación | [Navigation Compose](https://developer.android.com/jetpack/compose/navigation) |
| Estado / Arquitectura | ViewModel + StateFlow (patrón MVVM) |
| Ubicación GPS | [FusedLocationProviderClient](https://developers.google.com/android/reference/com/google/android/gms/location/FusedLocationProviderClient) (Google Play Services) |
| Base de datos local | [Room](https://developer.android.com/training/data-storage/room) (SQLite) |
| Asincronía | Kotlin Coroutines + Flow |

No requiere backend ni servidor: todos los datos se guardan **localmente en el dispositivo**.

---

## 📂 Estructura del proyecto (arquitectura por capas)

```
MileageTrackerNative/
├── app/
│   ├── build.gradle.kts                  # Dependencias y configuración de compilación
│   └── src/main/
│       ├── AndroidManifest.xml           # Permisos y configuración general
│       └── java/com/tuusuario/mileagetracker/
│           ├── MainActivity.kt           # Punto de entrada
│           ├── data/
│           │   ├── local/                # Room: Entity, DAO, Database
│           │   └── repository/           # Repository (única puerta a los datos)
│           ├── util/                     # Lógica pura: cálculo GPS y tasas del IRS
│           ├── location/                 # Wrapper de FusedLocationProviderClient
│           └── ui/
│               ├── theme/                # Colores, tipografía, tema Material 3
│               ├── navigation/           # Navegación por pestañas
│               ├── components/           # Botón, tarjetas reutilizables
│               ├── home/                 # Pantalla + ViewModel de "Start Work"
│               ├── history/              # Pantalla + ViewModel de historial
│               └── summary/              # Pantalla + ViewModel de resumen fiscal
├── build.gradle.kts                      # Configuración a nivel de proyecto
├── settings.gradle.kts                   # Módulos del proyecto
└── gradle.properties
```

Arquitectura **MVVM** (Model-View-ViewModel): cada pantalla tiene un `Screen.kt` (solo dibuja UI) y un `ViewModel.kt` (toda la lógica y el estado). Esto separa claramente "qué se ve" de "qué hace la app" — la misma filosofía que ya usamos en la versión React Native del proyecto, adaptada a las herramientas nativas de Android.

---

## 🚀 Cómo correr el proyecto en Android Studio

### Requisitos previos
- [Android Studio](https://developer.android.com/studio) (versión reciente, Koala o superior)
- JDK 17 (Android Studio lo incluye internamente)
- Un emulador de Android configurado, o un celular Android real con "Depuración USB" activada

### Pasos

1. Abre **Android Studio** → `File` → `Open` → selecciona la carpeta `MileageTrackerNative`.
2. Espera a que Gradle sincronice automáticamente (barra de progreso abajo).
3. Selecciona un dispositivo (emulador o celular físico) en la barra superior.
4. Presiona el botón ▶ **Run** (o `Shift + F10`).
5. La app pedirá permiso de ubicación la primera vez que presiones "Start Work" — acéptalo.

> La guía completa, paso a paso desde cero (instalar Android Studio, crear el proyecto, entender cada archivo, y subirlo a GitHub) está en el PDF: **`GUIA_ANDROID_STUDIO_PASO_A_PASO.pdf`** incluido en este repositorio.

---

## 🧮 ¿Cómo se calcula la deducción?

1. Mientras el rastreo está activo, `LocationTracker` registra puntos GPS cada pocos segundos usando `FusedLocationProviderClient`.
2. Al presionar **Stop Work**, se calcula la distancia total con la [fórmula de Haversine](https://en.wikipedia.org/wiki/Haversine_formula).
3. Esa distancia se multiplica por la **tasa estándar de millaje del IRS** vigente en la fecha del viaje (ver `util/TaxRates.kt`).

| Período | Tasa por milla |
|---|---|
| 1 ene 2026 – 30 jun 2026 | $0.725 |
| 1 jul 2026 – 31 dic 2026 | $0.76 |

Fuente oficial: [irs.gov/tax-professionals/standard-mileage-rates](https://www.irs.gov/tax-professionals/standard-mileage-rates)

---

## 🗺️ Roadmap (próximas versiones)

- [ ] Rastreo en segundo plano con un `Foreground Service` (para que siga funcionando con la pantalla apagada)
- [ ] Exportar historial a PDF / CSV
- [ ] Clasificación de viajes (negocio / personal / médico / caritativo)
- [ ] Modo oscuro
- [ ] Publicar en Google Play

---

## ⚖️ Aviso legal

Esta aplicación es un **proyecto educativo / de portafolio**. Los cálculos son estimaciones basadas en tasas públicas del IRS y **no constituyen asesoría legal, contable ni fiscal**. Antes de presentar cualquier declaración de impuestos, consulta a un contador certificado (CPA) o a las fuentes oficiales:

- IRS: https://www.irs.gov
- NC Department of Revenue: https://www.ncdor.gov

---

## 🤝 Contribuciones

Este es un proyecto personal en su primera versión (v1.0). Sugerencias, issues y pull requests son bienvenidos.

## 📄 Licencia

Distribuido bajo la licencia MIT. Ver [`LICENSE`](./LICENSE) para más detalles.

---

Hecho con ❤️ usando Kotlin + Jetpack Compose.
