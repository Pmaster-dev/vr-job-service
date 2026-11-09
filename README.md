# Job-Magician (Android Edition)

**Deaf-First Vocational Rehabilitation and Workforce Development Mobile Application**

Part of the [360 Magicians](https://github.com/360magicians) ecosystem and [VR4Deaf](https://github.com/pinkycollie/vr4deaf) initiative.

## Overview

Job-Magician is a comprehensive vocational rehabilitation and workforce development Android application designed with accessibility and inclusion at its core. Built natively using **Kotlin**, **Jetpack Compose**, and **Material Design 3**, this mobile app integrates with Texas Workforce Solutions to provide:

- **Deaf-First Architecture**: Native ASL video training guides, Deaf-first quick toggles, and direct VRS / ASL interpreter support requests.
- **Integrated Workforce Solutions Platform**: Coordinated service management linking Texas Workforce Solutions and 360 Magicians resources.
- **Job Specialist 7-Step Cycle**: Interactive step-by-step VR workflow from intake and assessment to placement tracking.
- **Client Services Dashboard**: Live progress indicators, skills assessment reports, personalized training course tracking, and job application management.
- **Job Opportunities & Resources**: Filterable WIOA job matches, ADA accommodation guides, and state VR documentation.

## Tech Stack & Architecture

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (Material Design 3)
- **Architecture**: MVVM with `ViewModel` and `StateFlow` / `collectAsStateWithLifecycle`
- **Build System**: Gradle (Kotlin DSL `.gradle.kts`)
- **Compatibility**: Android SDK 24+ (Android 7.0+)
- **Design & Insets**: Full edge-to-edge layout support with `WindowInsets.safeDrawing`

## Compliance & Standards

- **Workforce Innovation and Opportunity Act (WIOA)**: Federal workforce development rules
- **Texas Workforce Commission (TWC)**: Vocational rehabilitation standards
- **Americans with Disabilities Act (ADA)** & **Section 508**: Accessibility and accommodation rules
