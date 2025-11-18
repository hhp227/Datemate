package com.hhp227.datemate.ui.signin

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hhp227.datemate.R
import com.hhp227.datemate.common.InjectorUtils
import com.hhp227.datemate.ui.theme.DateMateTheme

@Composable
fun SignInScreen(
    viewModel: SignInViewModel = viewModel(factory = InjectorUtils.provideSignInViewModelFactory()),
    onSignUp: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.wrapContentHeight(align = Alignment.CenterVertically)) {
            Logo(modifier = Modifier.align(Alignment.CenterHorizontally).padding(horizontal = 48.dp))
            Text(
                text = stringResource(id = R.string.app_tagline),
                style = MaterialTheme.typography.subtitle1,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 24.dp).fillMaxWidth()
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val focusRequester = remember { FocusRequester() }

            EmailField(
                value = uiState.email,
                error = uiState.emailError,
                onValueChange = viewModel::onEmailChanged,
                onImeAction = { focusRequester.requestFocus() }
            )
            Spacer(modifier = Modifier.height(16.dp))
            PasswordField(
                value = uiState.password,
                error = uiState.passwordError,
                onValueChange = viewModel::onPasswordChanged,
                onImeAction = { viewModel.signIn(uiState.email, uiState.password) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { viewModel.signIn(uiState.email, uiState.password) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                enabled = uiState.isSignInEnabled
            ) {
                Text(text = stringResource(id = R.string.sign_in))
            }
            TextButton(
                onClick = { onSignUp() },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Sign Up")
            }
        }
    }
}

@Composable
private fun Logo(modifier: Modifier = Modifier, lightTheme: Boolean = MaterialTheme.colors.isLight) {
    val assetId = if (lightTheme) {
        R.drawable.ic_logo_light_eng
    } else {
        R.drawable.ic_logo_dark_eng
    }

    Image(
        painter = painterResource(id = assetId),
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.Fit
    )
}

@Composable
fun EmailField(
    value: String,
    error: String?,
    onValueChange: (String) -> Unit,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {}
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Email") },
        isError = error != null,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = imeAction),
        keyboardActions = KeyboardActions(onDone = { onImeAction() })
    )
    if (error != null) {
        TextFieldError(textError = error)
    }
}

@Composable
fun PasswordField(
    value: String,
    error: String?,
    onValueChange: (String) -> Unit,
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: () -> Unit = {}
) {
    val showPassword = remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Password") },
        visualTransformation =
            if (showPassword.value) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = { showPassword.value = !showPassword.value }) {
                Icon(
                    imageVector =
                        if (showPassword.value) Icons.Default.Visibility
                        else Icons.Default.VisibilityOff,
                    contentDescription = null
                )
            }
        },
        isError = error != null,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = imeAction),
        keyboardActions = KeyboardActions(onDone = { onImeAction() })
    )
    if (error != null) {
        TextFieldError(textError = error)
    }
}

/**
 * To be removed when [TextField]s support error
 */
@Composable
fun TextFieldError(textError: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = textError,
            modifier = Modifier.fillMaxWidth(),
            style = LocalTextStyle.current.copy(color = MaterialTheme.colors.error)
        )
    }
}

@Preview(name = "Sign in light theme")
@Composable
fun SignInPreview() {
    DateMateTheme {
        SignInScreen(onSignUp = {})
    }
}

@Preview(name = "Sign in dark theme")
@Composable
fun SignInPreviewDark() {
    DateMateTheme(darkTheme = true) {
        SignInScreen(onSignUp = {})
    }
}
