package ua.chnu.kpp.recipes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import org.koin.compose.viewmodel.koinViewModel
import ua.chnu.kpp.recipes.navigation.Route
import ua.chnu.kpp.recipes.navigation.navSavedStateConfig
import ua.chnu.kpp.recipes.ui.common.CollectEffects
import ua.chnu.kpp.recipes.ui.home.HomeRoute
import ua.chnu.kpp.recipes.ui.login.LoginRoute
import ua.chnu.kpp.recipes.ui.onboarding.OnboardingRoute
import ua.chnu.kpp.recipes.ui.theme.SmakolykTheme

@Composable
fun App() {
    SmakolykTheme {
        val appViewModel = koinViewModel<AppViewModel>()
        val startDestination by appViewModel.startDestination.collectAsStateWithLifecycle()

        val start = startDestination
        if (start == null) {
            // splash while tokens are read from the secure storage
            Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background))
        } else {
            AppNavigation(start = start, appViewModel = appViewModel)
        }
    }
}

@Composable
private fun AppNavigation(start: Route, appViewModel: AppViewModel) {
    val backStack = rememberNavBackStack(navSavedStateConfig, start)

    // refresh token rejected -> drop everything and show the login screen
    CollectEffects(appViewModel.sessionExpired) { backStack.replaceAll(Route.Login) }

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
                OnboardingRoute(onNavigateToLogin = { backStack.replaceAll(Route.Login) })
            }
            entry<Route.Login> {
                LoginRoute(onSignedIn = { backStack.replaceAll(Route.Home) })
            }
            entry<Route.Home> {
                HomeRoute(onSignedOut = { backStack.replaceAll(Route.Login) })
            }
        },
    )
}

/** Replaces the whole back stack, so "Back" cannot return to a screen of the previous flow. */
private fun NavBackStack<NavKey>.replaceAll(route: Route) {
    add(route)
    removeAll { it != route }
}
