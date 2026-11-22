package ua.chnu.kpp.recipes

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import org.koin.compose.viewmodel.koinViewModel
import ua.chnu.kpp.recipes.navigation.Route
import ua.chnu.kpp.recipes.navigation.navSavedStateConfig
import ua.chnu.kpp.recipes.ui.login.LoginRoute
import ua.chnu.kpp.recipes.ui.onboarding.OnboardingRoute
import ua.chnu.kpp.recipes.ui.theme.SmakolykTheme

@Composable
fun App() {
    SmakolykTheme {
        val appViewModel = koinViewModel<AppViewModel>()
        val backStack = rememberNavBackStack(navSavedStateConfig, appViewModel.startDestination)

        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                // gives every destination its own ViewModelStore
                rememberViewModelStoreNavEntryDecorator(),
            ),
            entryProvider = entryProvider {
                entry<Route.Onboarding> {
                    OnboardingRoute(
                        onNavigateToLogin = {
                            // replace onboarding, so "Back" from login closes the app
                            backStack.add(Route.Login)
                            backStack.remove(Route.Onboarding)
                        },
                    )
                }
                entry<Route.Login> {
                    LoginRoute()
                }
            },
        )
    }
}
