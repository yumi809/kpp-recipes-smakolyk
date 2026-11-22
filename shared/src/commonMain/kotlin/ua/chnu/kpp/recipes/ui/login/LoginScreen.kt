package ua.chnu.kpp.recipes.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import ua.chnu.kpp.recipes.ui.common.PaneLayout
import ua.chnu.kpp.recipes.ui.common.rememberPaneLayout
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import ua.chnu.kpp.recipes.resources.Res
import ua.chnu.kpp.recipes.resources.app_name
import ua.chnu.kpp.recipes.resources.error_password_empty
import ua.chnu.kpp.recipes.resources.error_password_short
import ua.chnu.kpp.recipes.resources.error_username_chars
import ua.chnu.kpp.recipes.resources.error_username_empty
import ua.chnu.kpp.recipes.resources.error_username_short
import ua.chnu.kpp.recipes.resources.ic_visibility
import ua.chnu.kpp.recipes.resources.ic_visibility_off
import ua.chnu.kpp.recipes.resources.login_demo_hint
import org.jetbrains.compose.resources.StringResource
import ua.chnu.kpp.recipes.resources.login_error_credentials
import ua.chnu.kpp.recipes.resources.login_error_network
import ua.chnu.kpp.recipes.resources.login_error_server
import ua.chnu.kpp.recipes.resources.login_error_unknown
import ua.chnu.kpp.recipes.resources.login_hide_password
import ua.chnu.kpp.recipes.resources.login_password
import ua.chnu.kpp.recipes.resources.login_show_password
import ua.chnu.kpp.recipes.resources.login_sign_in
import ua.chnu.kpp.recipes.resources.login_subtitle
import ua.chnu.kpp.recipes.resources.login_title
import ua.chnu.kpp.recipes.resources.login_username
import ua.chnu.kpp.recipes.resources.onboarding_chef
import ua.chnu.kpp.recipes.ui.common.CollectEffects
import ua.chnu.kpp.recipes.ui.theme.SmakolykTheme

@Composable
fun LoginRoute(
    onSignedIn: () -> Unit,
    viewModel: LoginViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val focus = remember { LoginFocus() }

    CollectEffects(viewModel.effects) { effect ->
        when (effect) {
            is LoginEffect.FocusField -> focus.request(effect.field)
            LoginEffect.NavigateToHome -> onSignedIn()
            is LoginEffect.ShowError -> {
                snackbarHostState.currentSnackbarData?.dismiss()
                snackbarHostState.showSnackbar(getString(effect.reason.message()))
            }
        }
    }

    LoginScreen(
        state = state,
        focus = focus,
        layout = rememberPaneLayout(),
        snackbarHostState = snackbarHostState,
        onIntent = viewModel::onIntent,
    )
}

/** Focus requesters for both fields, so effects and keyboard actions can move focus. */
class LoginFocus {
    val username = FocusRequester()
    val password = FocusRequester()

    fun request(field: LoginField) = when (field) {
        LoginField.USERNAME -> username.requestFocus()
        LoginField.PASSWORD -> password.requestFocus()
    }
}

@Composable
internal fun LoginScreen(
    state: LoginState,
    focus: LoginFocus,
    layout: PaneLayout,
    snackbarHostState: SnackbarHostState,
    onIntent: (LoginIntent) -> Unit,
) {
    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        if (layout == PaneLayout.TWO) {
            // Wide window: brand panel on the left, the form on the right.
            Row(Modifier.fillMaxSize().padding(padding)) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f))
                        .padding(32.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    LoginHeader()
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .imePadding()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 40.dp, vertical = 32.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    LoginForm(
                        state = state,
                        focus = focus,
                        onIntent = onIntent,
                        modifier = Modifier.widthIn(max = 420.dp),
                    )
                }
            }
            return@Scaffold
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            LoginHeader()
            Spacer(Modifier.height(32.dp))
            LoginForm(
                state = state,
                focus = focus,
                onIntent = onIntent,
                modifier = Modifier.widthIn(max = 420.dp),
            )
        }
    }
}

@Composable
internal fun LoginHeader(modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(Res.drawable.onboarding_chef),
            contentDescription = null,
            modifier = Modifier.size(120.dp),
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(Res.string.app_name),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(Res.string.login_title),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(Res.string.login_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
internal fun LoginForm(
    state: LoginState,
    focus: LoginFocus,
    onIntent: (LoginIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current

    fun submit() {
        keyboard?.hide()
        focusManager.clearFocus()
        onIntent(LoginIntent.Submit)
    }

    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = state.username,
            onValueChange = { onIntent(LoginIntent.UsernameChanged(it)) },
            label = { Text(stringResource(Res.string.login_username)) },
            singleLine = true,
            isError = state.usernameError != null,
            supportingText = state.usernameError?.let { error -> { Text(usernameErrorText(error)) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii, imeAction = ImeAction.Next),
            // keyboard "Next" moves the cursor to the password field
            keyboardActions = KeyboardActions(onNext = { focus.password.requestFocus() }),
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth().focusRequester(focus.username),
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = state.password,
            onValueChange = { onIntent(LoginIntent.PasswordChanged(it)) },
            label = { Text(stringResource(Res.string.login_password)) },
            singleLine = true,
            isError = state.passwordError != null,
            supportingText = state.passwordError?.let { error -> { Text(passwordErrorText(error)) } },
            visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { onIntent(LoginIntent.TogglePasswordVisibility) }) {
                    Icon(
                        painter = painterResource(if (state.isPasswordVisible) Res.drawable.ic_visibility_off else Res.drawable.ic_visibility),
                        contentDescription = stringResource(
                            if (state.isPasswordVisible) Res.string.login_hide_password else Res.string.login_show_password
                        ),
                    )
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            // keyboard "Done" does the same as the Sign in button
            keyboardActions = KeyboardActions(onDone = { submit() }),
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth().focusRequester(focus.password),
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = ::submit,
            enabled = state.canSubmit,
            modifier = Modifier.fillMaxWidth().height(56.dp),
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
            } else {
                Text(stringResource(Res.string.login_sign_in), style = MaterialTheme.typography.labelLarge)
            }
        }
        Spacer(Modifier.height(12.dp))
        Text(
            text = stringResource(Res.string.login_demo_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

private fun LoginFailure.message(): StringResource = when (this) {
    LoginFailure.INVALID_CREDENTIALS -> Res.string.login_error_credentials
    LoginFailure.NO_CONNECTION -> Res.string.login_error_network
    LoginFailure.SERVER -> Res.string.login_error_server
    LoginFailure.UNKNOWN -> Res.string.login_error_unknown
}

@Composable
private fun usernameErrorText(error: UsernameError): String = when (error) {
    UsernameError.EMPTY -> stringResource(Res.string.error_username_empty)
    UsernameError.TOO_SHORT -> stringResource(Res.string.error_username_short, LoginValidator.USERNAME_MIN_LENGTH)
    UsernameError.INVALID_CHARS -> stringResource(Res.string.error_username_chars)
}

@Composable
private fun passwordErrorText(error: PasswordError): String = when (error) {
    PasswordError.EMPTY -> stringResource(Res.string.error_password_empty)
    PasswordError.TOO_SHORT -> stringResource(Res.string.error_password_short, LoginValidator.PASSWORD_MIN_LENGTH)
}

@Preview
@Composable
private fun LoginScreenPreview() {
    SmakolykTheme {
        LoginScreen(
            state = LoginState(username = "emilys", passwordError = PasswordError.TOO_SHORT),
            focus = remember { LoginFocus() },
            layout = PaneLayout.SINGLE,
            snackbarHostState = remember { SnackbarHostState() },
            onIntent = {},
        )
    }
}
