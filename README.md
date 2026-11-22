# Smakolyk — recipes app (Kotlin Multiplatform, Compose Multiplatform)

Cross-platform recipes client for the open [dummyjson](https://dummyjson.com/docs) API
(Android, iOS, Desktop). Built step by step in labs 4–7 of the "Cross-platform programming" course;
every lab is a separate commit.

## Lab 4 — new KMP project, theme, onboarding pager, localisation

- New KMP project (`androidApp`, `desktopApp`, `iosApp`, `shared`), theme: recipes (dummyjson `/recipes`).
- **Design**: warm "kitchen" palette (paprika / basil / cream), designed with the help of an AI assistant (Claude).
- **Custom theme** (`ui/theme`): light & dark `ColorScheme`, shapes, fonts as Compose resources —
  *Pacifico* for display text and *Nunito* (variable font) for body text (both SIL OFL, from Google Fonts).
- **Kermit** for logging (`Logger.withTag(...)`), **multiplatform-settings** for user settings
  (onboarding-passed flag).
- **Onboarding** with `HorizontalPager` (4 slides with vector illustrations), animated page indicator,
  Skip / Next / Start buttons, page changes observed via `snapshotFlow` and logged.
- **Localisation**: English (`values`) and Ukrainian (`values-uk`).

## Run

- Android: `./gradlew :androidApp:assembleDebug` (or Run in Android Studio)
- Desktop: `./gradlew :desktopApp:run`
- iOS: open `iosApp` in Xcode (macOS only)
