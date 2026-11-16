# Plan 001: Implement Customizable Todo App for Android (PRD.md)

> **Executor instructions**: Follow this plan step by step. Run every
> verification command and confirm the expected result before moving to the
> next step. If anything in the "STOP conditions" section occurs, stop and
> report — do not improvise.

## Status

- **Priority**: P1
- **Effort**: L
- **Risk**: LOW
- **Depends on**: none
- **Category**: feature / foundation
- **Planned at**: initial setup, 2026-11-14
- **Status**: DONE (Verified via testDebugUnitTest and assembleDebug)

## Why this matters

The repository contains only `PRD.md`, specifying an offline-first, native Android todo app for managing one-time and recurring routines. Unlike conventional todo lists where recurring tasks are a single status flag, this app requires an explicit distinction between Task definitions and individual TaskOccurrences. Each occurrence has its own completion state, allowing an interactive home-screen widget to check off occurrences directly without side effects on future occurrences.

## Current state

- Workspace contains `PRD.md` and `.agents/`.
- Local machine has Java 17 and Android SDK Platform 34 (`android-34`), build-tools `34.0.0`, and `android-cli`.
- No source files currently exist.

## Target Architecture

- **Language / Framework**: Kotlin 2.0+, Jetpack Compose, Material 3
- **Local DB**: Room (`tasks` and `task_occurrences` tables with unique constraint `(taskId, occurrenceDate)`)
- **Widget**: Jetpack Glance (`GlanceAppWidget`)
- **Preferences**: DataStore (`datastore-preferences`)
- **Recurrence Engine**: Pure Kotlin domain logic calculating occurrences for standard, weekly, monthly-by-date (skipping missing dates like 31st), interval (every N days), and selected dates within a 60–90 day rolling window.
- **Localization**: Indonesian-ready with strings stored in `strings.xml`.

## Step-by-Step Execution Plan

### Step 1: Project Scaffolding
- Initialize the Android Gradle structure with:
  - `compileSdk = 34`, `targetSdk = 34`, `minSdk = 24`
  - Dependencies: Room, Navigation Compose, Glance AppWidget, DataStore, JUnit 4, Kotlinx Coroutines Test
- **Verification**: Run `.\gradlew --version` or verify Gradle configuration loads without error.

### Step 2: Room Schema & Recurrence Engine
- Define `TaskEntity`, `TaskOccurrenceEntity`, and `TaskWithOccurrence`.
- Implement `RecurrenceCalculator` (pure Kotlin domain service) supporting:
  - `ONCE`
  - `DAILY`
  - `WEEKLY` (multiple days of week)
  - `MONTHLY` (day of month, skipping months where day does not exist)
  - `INTERVAL` (every N days from anchor)
  - `SELECTED_DATES` (explicit set)
- Implement `OccurrenceReconciler` to generate up to 60-90 days of occurrences and preserve completed history on edits.
- Implement DAOs and Database.
- Write unit tests for `RecurrenceCalculatorTest` and `OccurrenceReconcilerTest`.
- **Verification**: Run `.\gradlew testDebugUnitTest` and ensure all unit tests pass.

### Step 3: Repository & ViewModels
- Implement `TaskRepository` bridging Room, RecurrenceEngine, and DataStore.
- Implement `TodayViewModel`, `UpcomingViewModel`, `AllTasksViewModel`, and `TaskFormViewModel`.
- **Verification**: Unit tests for ViewModels.

### Step 4: Jetpack Compose UI
- Implement `AppNavigation` and screens:
  - `TodayScreen`: Date header, incomplete tasks, collapsible completed tasks, quick add FAB, empty state.
  - `UpcomingScreen`: Occurrences grouped by date.
  - `AllTasksScreen`: Task definitions, readable schedule summary, archive/delete actions.
  - `TaskFormScreen`: Dynamic schedule form fields, date range, color picker, next occurrence live preview.
  - `TaskDetailScreen`: Schedule rules and history log.
  - `SettingsScreen`: Theme and widget settings.
- Implement Indonesian strings in `values/strings.xml` and `values-in/strings.xml`.
- **Verification**: Run `.\gradlew assembleDebug` to confirm clean compilation.

### Step 5: Jetpack Glance Home-Screen Widget
- Implement `TodayWidget : GlanceAppWidget` and `TodayWidgetReceiver : GlanceAppWidgetReceiver`.
- Render today's progress (`X / Y completed`), incomplete tasks list, and completion checkbox.
- Checkbox action toggles occurrence status in Room and refreshes Glance widget.
- Register widget provider in `AndroidManifest.xml`.
- **Verification**: Run `.\gradlew assembleDebug` and verify APK build.

## Out of Scope (MVP)
- Push notifications / alarm reminders (deferred per PRD Section 5.6 and 16).
- Cloud sync / multi-device login (explicit non-goal per PRD Section 2).
- Subtasks / complex dependencies (explicit non-goal per PRD Section 2).

## STOP Conditions
- If any Gradle build incompatibility occurs with Android SDK 34 or JDK 17, STOP and inspect exact compiler version constraints.
- Do not add backend or cloud dependencies.
