# AGENTS.md

## Scope and instruction hierarchy

This file contains repository-wide guidance for Penn Mobile Android.

Keep detailed guidance close to the code it governs. A module or major layer may
provide its own `AGENTS.md`; when working in that subtree, follow both this file
and the closest nested `AGENTS.md`. More-specific instructions take precedence.
Prefer nested files such as `domain/AGENTS.md`, `data/AGENTS.md`, or
`presentation/AGENTS.md` over expanding this root file with layer-specific rules.

## Project overview

Penn Mobile is a single-module Android application. The application module is
`:PennMobile`, with namespace `com.pennapps.labs.pennmobile`.

The codebase contains Kotlin and Java and uses a mixture of:

- Fragments, XML layouts, RecyclerView, and ViewBinding
- Jetpack Compose embedded in existing Fragment/View screens
- Hilt dependency injection
- Retrofit, RxJava, and Kotlin coroutines
- Firebase services
- SharedPreferences
- Dining and GSR app widgets

Keep changes consistent with the architecture used by the affected feature. Do
not introduce a broad architectural migration as part of an unrelated task.

## Source layout

- Production code: `PennMobile/src/main/java/com/pennapps/labs/pennmobile`
- Resources: `PennMobile/src/main/res`
- Unit tests: `PennMobile/src/test`
- Instrumented tests: `PennMobile/src/androidTest`
- Dependency versions and aliases: `gradle/libs.versions.toml`

Major feature packages include `home`, `dining`, `gsr`, `laundry`, `fitness`,
`coursealert`, `studentresources`, and `more`.

## Environment and secrets

Use JDK 17 and the checked-in Gradle wrapper. The project currently targets SDK
35 and supports API 26 and newer.

Local builds may require:

- `PLATFORM_CLIENT_ID` in `local.properties`
- `PLATFORM_REDIRECT_URI` in `local.properties`
- `PennMobile/google-services.json`

These files and values are secrets or machine-local configuration. Never print,
commit, replace, or fabricate them.

## Build and verification

Run commands from the repository root.

For Kotlin changes:

```bash
./gradlew ktlintCheck
./gradlew :PennMobile:testDebugUnitTest
```

For changes that affect compilation or resources:

```bash
./gradlew :PennMobile:assembleDebug
```

For broader Android changes, when the environment supports it:

```bash
./gradlew :PennMobile:lintDebug
```

Use `./gradlew ktlintFormat` only when formatting changes are intended, and
review its full diff afterward. If verification cannot run because an SDK,
emulator, credential, or Firebase file is unavailable, report that limitation.

## Implementation guidelines

- Make the smallest change that fully solves the task.
- Preserve existing behavior outside the requested scope.
- Follow the local pattern of the feature being changed.
- Do not migrate XML screens to Compose unless requested.
- Do not migrate RxJava APIs to coroutines unless requested.
- Prefer existing repository interfaces and Hilt bindings for dependencies.
- Keep network and persistence work out of UI components where an existing
  repository or ViewModel boundary is available.
- Preserve preference keys and serialized API field names unless the task
  includes a migration.
- Put dependency versions and aliases in `gradle/libs.versions.toml`.
- Avoid adding a dependency when the project already has an equivalent facility.
- Never edit generated output under `.gradle/`, `build/`, `PennMobile/build/`,
  `PennMobile/build 2/`, or similarly named generated directories.

## Android-specific safeguards

Treat changes to the following as high risk:

- Manifest permissions and exported components
- OAuth redirects and token storage
- Firebase configuration
- Notification and exact-alarm behavior
- Dining and GSR widgets
- Release signing and publishing workflows

Do not change these areas without explaining the impact and testing the affected
flow. Do not change CI, signing, version codes, or deployment configuration
unless the task explicitly requires it.

## Tests

Add or update tests for changed logic when practical. Cover important error,
empty, loading, and offline paths for network-backed behavior.

Use local JVM tests for pure logic. Use instrumented or device testing when
Android framework behavior is involved. For UI changes, verify the affected
screen and relevant light/dark-theme behavior on an emulator or device when
available. Check widgets separately when changing dining or GSR data or
preferences.

## Repository hygiene

- Inspect `git status` before and after editing.
- Preserve unrelated working-tree changes.
- Do not commit local configuration, credentials, APKs, reports, or generated
  files.
- Keep this root file concise; place module-, layer-, or feature-specific guidance
  in the closest nested `AGENTS.md`.
