# Kids Workbook — Android App (Age 7)

A native Android app (Kotlin + Jetpack Compose + Room) covering **English, Reading,
Mathematics, Fine Art, Physical/Life/Health Education, Verbal Reasoning, and
Quantitative Reasoning**, with a local performance database and a pluggable
architecture for importing third-party content engines/SDKs.

## Requirements
- Android Studio (Koala/2024.1 or newer)
- JDK 17 (bundled with recent Android Studio)
- Android SDK 34, min SDK 24 (Android 7.0+)

## Getting it running
1. Open the `WorkbookApp/` folder in Android Studio ("Open an existing project").
2. Let Gradle sync (it will download the AndroidX/Compose/Room dependencies —
   needs internet access the first time).
3. Run on an emulator or device (▶ button). The app has no backend server;
   everything works offline out of the box.
4. First launch → profile setup screen (tap the circle to add a photo, enter
   name and age) → Save → subject dashboard.

## What's implemented

| Requirement | Where |
|---|---|
| English, Reading, Math, Fine Art, PLHE, Verbal & Quantitative Reasoning | `data/content/models/Subject.kt`, JSON in `assets/content/` |
| Performance database | Room: `data/local/entities/PerformanceRecord.kt`, `data/local/dao/PerformanceDao.kt` |
| Student photo/name/age upload | `ui/screens/onboarding/ProfileSetupScreen.kt`, `data/repository/StudentRepository.kt` |
| Auto date & time | Never entered by the user — `System.currentTimeMillis()` defaults on `StudentProfile.createdAt/updatedAt` and `PerformanceRecord.timestampEpochMillis`, and duration is measured automatically in `ActivityScreen.kt` |
| Imported engines (third-party SDKs) | `data/content/ContentEngine.kt` interface, `ContentEngineProvider.kt`, `ThirdPartyContentEngineTemplate.kt` |

## Architecture

```
UI (Compose screens)
   ↓
ViewModels (Profile / Content / Performance)
   ↓
Repositories (StudentRepository, PerformanceRepository)
   ↓                                   ↓
Room database                    ContentEngine (interface)
(performance + profiles)               ↓
                              LocalContentEngine (default, bundled JSON)
                                        or
                              Your vendor SDK/API (per subject)
```

The UI and the Room performance database never talk to a content vendor
directly — they only go through `ContentEngine`. This is what makes the
"imported engines" swappable per subject with no ripple effect elsewhere.

## Wiring in a real third-party content SDK
1. Duplicate `data/content/ThirdPartyContentEngineTemplate.kt`, rename the class
   (e.g. `ReadingEggsEngine`).
2. Inside it, call the vendor's SDK/REST API and map its lessons/questions into
   this app's `Activity` / `Question` models (`data/content/models/Content.kt`).
3. In `data/content/ContentEngineProvider.kt`, change the line for that one
   subject to return your new class instead of `LocalContentEngine`.
4. Add any required API key/config to `app/build.gradle.kts` (`BuildConfig` field)
   or a local, git-ignored properties file — never hard-code a key in source.
5. If the SDK needs network access, the `INTERNET` permission is already
   declared in `AndroidManifest.xml`.

No other file needs to change — Room, the dashboard, and the progress screen
work the same regardless of which engine serves a subject.

## Performance database schema
- `student_profile`: id, name, age, photoPath, createdAt, updatedAt (auto)
- `performance_record`: id, studentId (FK), subject, activityId, activityTitle,
  correctAnswers, totalQuestions, durationSeconds, timestampEpochMillis (auto)

The dashboard (`PerformanceScreen.kt`) shows average score per subject and a
chronological history, all computed with Room queries — no manual bookkeeping.

## Before you ship
- Replace the placeholder launcher icon (`res/drawable/ic_launcher_foreground.xml`)
  using Android Studio's Image Asset tool.
- Room currently uses `fallbackToDestructiveMigration()` — fine for v1, but once
  you ship an update that changes the database schema, write a real `Migration`
  so existing families don't lose their child's performance history.
- Add more activities per subject in `app/src/main/assets/content/*.json`, or
  connect a real content engine as described above.
- If you plan to support multiple children on one device, extend the "active
  student" logic in `StudentDao`/`ProfileViewModel` (currently the most
  recently created profile is treated as active) into an explicit profile switcher.
- This project has not been compiled/run in this environment (no Android SDK
  here) — do a build in Android Studio and fix any dependency-version drift
  before relying on it.
