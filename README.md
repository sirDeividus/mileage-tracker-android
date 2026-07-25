# 🚗 Mileage Tracker (Android nativo) — Rastreador de Millas para Deducción de Impuestos (NC / IRS)

App **100% nativa de Android**, escrita en **Kotlin + Jetpack Compose**, que rastrea por GPS las millas recorridas durante viajes de trabajo y calcula una **estimación de deducción de impuestos** usando la tasa estándar de millaje del IRS — válida para usuarios de **cualquiera de los 50 estados de EE.UU.**

> 🆕 **Versión 2.0.** Ver [changelog completo](#-changelog) más abajo, o el PDF `MEJORAS_V2.pdf` incluido en este repositorio.

---

## 🆕 Changelog

### v2.3
- **Registro de peajes**: al finalizar un viaje puedes anotar cuánto pagaste en peajes (opcional). El IRS permite deducir peajes y estacionamiento de negocio **por separado** de la deducción estándar por millaje — el Resumen fiscal ahora muestra la deducción combinada (millaje + peajes) y el desglose de cada una.
- **Respaldo de datos**: en Ajustes puedes **Exportar** un archivo de respaldo (a Google Drive, Descargas, correo, etc.) e **Importar** ese archivo si reinstalas la app o cambias de celular, sin perder tu historial. Además, se activó el "Auto Backup" de Android como capa adicional automática.
- Nueva migración de base de datos (`MIGRATION_2_3`) para agregar la columna de peajes sin perder viajes ya guardados.

### v2.2
- **Estimación válida para los 50 estados**: ya no está fija a North Carolina. En Ajustes eliges tu estado y la pantalla de Resumen muestra la nota correcta (la tasa del IRS es federal, así que es la misma para todos; solo cambiaba el texto informativo).
- **Recordatorio diario**: un modal recuerda activar "Start Work" antes de salir a trabajar y "Stop Work" al llegar a casa, para no perder millas. Aparece como máximo una vez al día.
- **Saludo + frase motivadora** en la parte superior de Inicio, según la hora del día.
- **Idioma español/inglés** seleccionable en Ajustes — la mayoría de las pantallas usan el sistema de textos traducibles (`Strings.kt`).
- **Tema claro / oscuro / automático** seleccionable en Ajustes.
- Nueva pestaña "Ajustes" (idioma, tema, estado).

### v2.0
- Rastreo en segundo plano real (Foreground Service).
- Selector de plataforma de trabajo (DoorDash, Uber, etc.).
- Botón de donación por PayPal.
- Actualización sin pérdida de datos (migración de base de datos).

### v1.0
- Primera versión: rastreo GPS en primer plano, historial, resumen fiscal.

---

## ⚠️ Nota sobre los logos de las plataformas

Los nombres DoorDash, Uber, Amazon Flex, etc. se muestran con un **color distintivo y un ícono genérico**, no con los logos oficiales de cada marca — reproducir esos logos sin licencia no está permitido, ni siquiera en un proyecto personal público. Si quieres los logos reales, tendrías que obtener el permiso/licencia de cada marca o usar sus kits de prensa oficiales bajo sus términos de uso.

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

- [ ] **Mapa visual offline** (v2.1): el rastreo de millas YA funciona sin internet (usa GPS satelital, no datos móviles), pero mostrar un mapa visual de la ruta recorrida requiere integrar un SDK de mapas con soporte de tiles offline (ej. OSMDroid, que es gratuito y no requiere facturación, a diferencia de Google Maps Platform). Quedó fuera de esta versión para no entregar una integración a medias sin las credenciales que tú deberías gestionar.
- [ ] Exportar historial a PDF / CSV
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
