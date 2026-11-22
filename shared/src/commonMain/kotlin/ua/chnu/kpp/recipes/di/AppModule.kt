package ua.chnu.kpp.recipes.di

import com.russhwolf.settings.Settings
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import ua.chnu.kpp.recipes.AppViewModel
import ua.chnu.kpp.recipes.data.auth.AuthApi
import ua.chnu.kpp.recipes.data.auth.KSafeTokenStorage
import ua.chnu.kpp.recipes.data.auth.RemoteAuthRepository
import ua.chnu.kpp.recipes.data.auth.SessionEvents
import ua.chnu.kpp.recipes.data.auth.TokenStorage
import ua.chnu.kpp.recipes.data.local.SettingsOnboardingRepository
import ua.chnu.kpp.recipes.data.remote.createHttpClient
import ua.chnu.kpp.recipes.domain.auth.AuthRepository
import ua.chnu.kpp.recipes.domain.auth.GetCurrentUserUseCase
import ua.chnu.kpp.recipes.domain.auth.IsUserSignedInUseCase
import ua.chnu.kpp.recipes.domain.auth.ObserveSessionExpiredUseCase
import ua.chnu.kpp.recipes.domain.auth.SignInUseCase
import ua.chnu.kpp.recipes.domain.auth.SignOutUseCase
import ua.chnu.kpp.recipes.domain.onboarding.CompleteOnboardingUseCase
import ua.chnu.kpp.recipes.domain.onboarding.IsOnboardingPassedUseCase
import ua.chnu.kpp.recipes.domain.onboarding.OnboardingRepository
import ua.chnu.kpp.recipes.ui.home.HomeViewModel
import ua.chnu.kpp.recipes.ui.login.LoginViewModel
import ua.chnu.kpp.recipes.ui.onboarding.OnboardingViewModel

val dataModule = module {
    includes(platformModule)
    single<Settings> { Settings() }
    singleOf(::SettingsOnboardingRepository) { bind<OnboardingRepository>() }

    singleOf(::KSafeTokenStorage) { bind<TokenStorage>() }
    singleOf(::SessionEvents)
    single { createHttpClient(tokenStorage = get(), sessionEvents = get()) }
    singleOf(::AuthApi)
    singleOf(::RemoteAuthRepository) { bind<AuthRepository>() }
}

val domainModule = module {
    factoryOf(::CompleteOnboardingUseCase)
    factoryOf(::IsOnboardingPassedUseCase)
    factoryOf(::SignInUseCase)
    factoryOf(::IsUserSignedInUseCase)
    factoryOf(::GetCurrentUserUseCase)
    factoryOf(::SignOutUseCase)
    factoryOf(::ObserveSessionExpiredUseCase)
}

val presentationModule = module {
    viewModelOf(::AppViewModel)
    viewModelOf(::OnboardingViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::HomeViewModel)
}

val appModule = module {
    includes(dataModule, domainModule, presentationModule)
}
