package com.hhp227.datemate

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hhp227.datemate.viewmodel.SignInViewModel
import com.kortek.myapplication.ui.theme.DateMateTheme

@Composable
fun SignInScreen(viewModel: SignInViewModel?) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Column(modifier = Modifier.wrapContentHeight(align = Alignment.CenterVertically)) {
            Logo(modifier = Modifier.align(Alignment.CenterHorizontally).padding(horizontal = 76.dp))
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
            val emailState = remember { EmailState() }
            val passwordState = remember { PasswordState() }

            Email(emailState, onImeAction = { focusRequester.requestFocus() })
            Spacer(modifier = Modifier.height(16.dp))
            Password(
                label = stringResource(id = R.string.password),
                passwordState = passwordState,
                modifier = Modifier.focusRequester(focusRequester),
                onImeAction = { viewModel?.signIn(emailState.text, passwordState.text) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { viewModel?.signIn(emailState.text, passwordState.text) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                enabled = emailState.isValid && passwordState.isValid
            ) {
                Text(text = stringResource(id = R.string.sign_in))
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
        modifier = modifier,
        contentDescription = null
    )
}

@Composable
fun Email(
    emailState: TextFieldState = remember { EmailState() },
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {}
) {
    OutlinedTextField(
        value = emailState.text,
        onValueChange = {
            emailState.text = it
        },
        label = {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = stringResource(id = R.string.email),
                    style = MaterialTheme.typography.body2
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                emailState.onFocusChange(focusState.isFocused)
                if (!focusState.isFocused) {
                    emailState.enableShowErrors()
                }
            },
        textStyle = MaterialTheme.typography.body2,
        isError = emailState.showErrors(),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = imeAction),
        keyboardActions = KeyboardActions(onDone = { onImeAction() })
    )
    emailState.getError()?.let { error -> TextFieldError(textError = error) }
}

@Composable
fun Password(
    label: String,
    passwordState: TextFieldState,
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: () -> Unit = {}
) {
    val showPassword = remember { mutableStateOf(false) }
    OutlinedTextField(
        value = passwordState.text,
        onValueChange = {
            passwordState.text = it
            passwordState.enableShowErrors()
        },
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                passwordState.onFocusChange(focusState.isFocused)
                if (!focusState.isFocused) {
                    passwordState.enableShowErrors()
                }
            },
        textStyle = MaterialTheme.typography.body2,
        label = {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.body2
                )
            }
        },
        trailingIcon = {
            if (showPassword.value) {
                IconButton(onClick = { showPassword.value = false }) {
                    Icon(
                        imageVector = Icons.Filled.Visibility,
                        contentDescription = stringResource(id = R.string.hide_password)
                    )
                }
            } else {
                IconButton(onClick = { showPassword.value = true }) {
                    Icon(
                        imageVector = Icons.Filled.VisibilityOff,
                        contentDescription = stringResource(id = R.string.show_password)
                    )
                }
            }
        },
        visualTransformation = if (showPassword.value) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        isError = passwordState.showErrors(),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = imeAction),
        keyboardActions = KeyboardActions(onDone = { onImeAction() })
    )
    passwordState.getError()?.let { error -> TextFieldError(textError = error) }
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
        SignInScreen(null)
    }
}

@Preview(name = "Sign in dark theme")
@Composable
fun SignInPreviewDark() {
    DateMateTheme(darkTheme = true) {
        SignInScreen(null)
    }
}
