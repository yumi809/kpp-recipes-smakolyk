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

## Lab 5 — Navigation 3, Koin, MVI for onboarding, login screen

- **Navigation 3** (`navigation/Route.kt`, `App.kt`): serializable `NavKey`s, `NavDisplay`,
  per-destination `ViewModelStore`; onboarding is replaced by login in the back stack.
- **Koin** DI (`di/AppModule.kt`: data / domain / presentation modules, `initKoin()` on every platform).
- **multiplatform-settings** behind `OnboardingRepository` (`data/local/SettingsOnboardingRepository`).
- **Onboarding MVI**: `OnboardingState / OnboardingIntent / OnboardingEffect`, `OnboardingViewModel`;
  use cases `CompleteOnboardingUseCase` (saves result) and `IsOnboardingPassedUseCase`.
- **Main `AppViewModel`** checks onboarding via the use case and picks the start destination.
- **Login screen** design + MVI (`ui/login`): validation of username / password (`LoginValidator`),
  keyboard actions — *Next* on username moves focus to password, *Done* on password = *Sign in* button,
  show/hide password, focus moves to the first invalid field.
- Unit tests: `LoginValidatorTest`, `OnboardingUseCasesTest`.

## Run

- Android: `./gradlew :androidApp:assembleDebug` (or Run in Android Studio)
- Desktop: `./gradlew :desktopApp:run`
- iOS: open `iosApp` in Xcode (macOS only)
