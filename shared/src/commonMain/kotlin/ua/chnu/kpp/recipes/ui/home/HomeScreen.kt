package ua.chnu.kpp.recipes.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import ua.chnu.kpp.recipes.resources.Res
import ua.chnu.kpp.recipes.resources.home_greeting
import ua.chnu.kpp.recipes.resources.home_load_error
import ua.chnu.kpp.recipes.resources.home_retry
import ua.chnu.kpp.recipes.resources.home_sign_out
import ua.chnu.kpp.recipes.resources.home_subtitle
import ua.chnu.kpp.recipes.ui.common.CollectEffects

@Composable
fun HomeRoute(
    onSignedOut: () -> Unit,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    CollectEffects(viewModel.effects) { effect ->
        when (effect) {
            HomeEffect.SignedOut -> onSignedOut()
        }
    }
    HomeScreen(state = state, onIntent = viewModel::onIntent)
}

@Composable
internal fun HomeScreen(state: HomeState, onIntent: (HomeIntent) -> Unit) {
    Surface(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.safeDrawingPadding().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            when {
                state.isLoading -> CircularProgressIndicator()

                state.hasError -> {
                    Text(stringResource(Res.string.home_load_error), style = MaterialTheme.typography.titleMedium)
                    TextButton(onClick = { onIntent(HomeIntent.Retry) }) { Text(stringResource(Res.string.home_retry)) }
                }

                else -> {
                    val name = state.user?.firstName?.ifBlank { null } ?: state.user?.username.orEmpty()
                    Text(
                        text = stringResource(Res.string.home_greeting, name),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = stringResource(Res.string.home_subtitle),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                    )
                }
            }
            Spacer(Modifier.height(32.dp))
            OutlinedButton(onClick = { onIntent(HomeIntent.SignOut) }) {
                Text(stringResource(Res.string.home_sign_out))
            }
        }
    }
}
