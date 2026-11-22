package ua.chnu.kpp.recipes.di

import com.russhwolf.settings.Settings
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import ua.chnu.kpp.recipes.AppViewModel
import ua.chnu.kpp.recipes.data.local.SettingsOnboardingRepository
import ua.chnu.kpp.recipes.domain.onboarding.CompleteOnboardingUseCase
import ua.chnu.kpp.recipes.domain.onboarding.IsOnboardingPassedUseCase
import ua.chnu.kpp.recipes.domain.onboarding.OnboardingRepository
import ua.chnu.kpp.recipes.ui.login.LoginViewModel
import ua.chnu.kpp.recipes.ui.onboarding.OnboardingViewModel

val dataModule = module {
    single<Settings> { Settings() }
    singleOf(::SettingsOnboardingRepository) { bind<OnboardingRepository>() }
}

val domainModule = module {
    factoryOf(::CompleteOnboardingUseCase)
    factoryOf(::IsOnboardingPassedUseCase)
}

val presentationModule = module {
    viewModelOf(::AppViewModel)
    viewModelOf(::OnboardingViewModel)
    viewModelOf(::LoginViewModel)
}

val appModule = module {
    includes(dataModule, domainModule, presentationModule)
}
