# LinkStage

![Kotlin](https://img.shields.io/badge/Kotlin-Android-blueviolet?logo=kotlin)
![Android](https://img.shields.io/badge/Android-SDK%2036-green?logo=android)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-blue?logo=jetpackcompose)
![Supabase](https://img.shields.io/badge/Supabase-Backend-3ECF8E?logo=supabase)
![Gradle](https://img.shields.io/badge/Gradle-Build-02303A?logo=gradle)
![Version](https://img.shields.io/badge/Version-1.0.0-blue)
![License](https://img.shields.io/badge/License-Academic-lightgrey)

Android application for academic internship management.

LinkStage supports the internship lifecycle between students, institutions, academic supervisors and administrators, including authentication, internship offers, applications, communication, activity tracking, final reports and evaluation workflows.

## Version

Current release: `1.0.0`

## Tech Stack

- Kotlin
- Android SDK 36
- Jetpack Compose
- Material 3
- Supabase
  - Auth
  - PostgREST
  - Storage
  - Realtime
- Room
- Retrofit
- Coil
- Kotlin Serialization
- Gradle
- GitHub Actions

## Architecture

The project follows a layered architecture:

```text
UI -> ViewModel -> RepositoryInterface -> Repository -> Data source
```

Main data sources:

- Supabase for authentication, database access, realtime features and file storage
- Room for local persistence
- Retrofit for external REST integrations

## Project Structure

```text
app/src/main/java/turmaA/grupoB/LinkStage
├── data
│   ├── remote
│   │   ├── api
│   │   ├── model
│   │   └── supabase
│   ├── repository
│   │   ├── application
│   │   ├── auth
│   │   ├── communication
│   │   ├── evaluation
│   │   ├── flags
│   │   ├── institution
│   │   ├── internship
│   │   ├── offer
│   │   ├── profile
│   │   ├── report
│   │   ├── storage
│   │   ├── student
│   │   └── supervisor
│   ├── room
│   └── util
├── ui
│   ├── admin
│   ├── aluno
│   ├── auth
│   ├── chat
│   ├── common
│   ├── instituicao
│   ├── introSliders
│   ├── navigation
│   ├── orientador
│   ├── splash
│   └── theme
└── viewmodel
    ├── activity
    ├── admin
    ├── advisorhome
    ├── application
    ├── apply
    ├── auth
    ├── chat
    ├── communication
    ├── discover
    ├── evaluation
    ├── flags
    ├── home
    ├── instituicao
    ├── Institution
    ├── institutionhome
    ├── internships
    ├── offer
    ├── offerform
    ├── orientador
    ├── profile
    ├── report
    ├── settings
    ├── student
    └── supervisor
```

## Functional Areas

- Authentication and user profiles
- Student internship offer discovery
- Student applications
- Institution offer and application management
- Academic supervisor internship tracking
- Administrator dashboards and management screens
- Chat and communication flows
- Activity logs
- Final reports
- Evaluation workflows
- File uploads through Supabase Storage
- Local activity persistence with Room
- External country/flag data through Retrofit

## Configuration

Create a `local.properties` file in the project root:

```properties
SUPABASE_URL=https://<project-ref>.supabase.co
SUPABASE_ANON_KEY=<anon-key>
RESTCOUNTRIES_API_KEY=<api-key>
```

Only the Supabase anon key must be used by the Android application.

Do not include Supabase service role keys or production secrets in the Android client.

## Build

Windows PowerShell:

```powershell
.\gradlew clean build
.\gradlew :app:assembleDebug
.\gradlew :app:assembleRelease
.\gradlew :app:bundleRelease
```

Linux/macOS:

```bash
./gradlew clean build
./gradlew :app:assembleDebug
./gradlew :app:assembleRelease
./gradlew :app:bundleRelease
```

## Tests

Local unit tests:

```bash
./gradlew testDebugUnitTest
```

Instrumented Android tests:

```bash
./gradlew connectedAndroidTest
```

Instrumented tests require an emulator or physical Android device.

Test locations:

```text
app/src/test
app/src/androidTest
```

## CI/CD

The repository includes GitHub Actions workflows for continuous integration, release generation and instrumented Android tests.

### Android CI

Workflow:

```text
.github/workflows/ci.yml
```

Runs on:

```text
pull_request -> develop, main
push         -> develop, main
```

Main command:

```bash
./gradlew clean build
```

Required GitHub secrets:

```text
SUPABASE_URL
SUPABASE_ANON_KEY
```

### Android Integration Tests

Workflow:

```text
.github/workflows/android-integration-tests.yml
```

Runs manually with:

```text
workflow_dispatch
```

Main command:

```bash
./gradlew connectedAndroidTest
```

The workflow runs the tests on an Android emulator with API 36.

### Release

Workflow:

```text
.github/workflows/cd.yml
```

Runs on:

```text
push -> main
```

Builds:

```text
APK
AAB
```

Generated artifacts are published to GitHub Releases.

Release artifact naming:

```text
LinkStage-<version>.apk
LinkStage-<version>.aab
```

## Release Notes

Version `1.0.0` represents the first complete release candidate of the LinkStage Android application, including the main role-based flows for students, institutions, academic supervisors and administrators.

## Git Workflow

Main branches:

```text
main
develop
```

Recommended branch naming:

```text
feature/<name>
fix/<name>
chore/<name>
docs/<name>
test/<name>
refactor/<name>
```

## Conventional Commits

Examples:

```text
feat(student): add final report upload
fix(auth): handle missing session
fix(student): prevent settings avatar crash
chore(release): prepare 1.0.0
docs: update README
test(viewmodel): add communication tests
```
