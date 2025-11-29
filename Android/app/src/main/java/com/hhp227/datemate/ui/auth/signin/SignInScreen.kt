package com.hhp227.datemate.ui.auth.signin

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hhp227.datemate.R
import com.hhp227.datemate.common.InjectorUtils
import com.hhp227.datemate.ui.auth.EmailField
import com.hhp227.datemate.ui.auth.PasswordField
import com.hhp227.datemate.ui.theme.DateMateTheme

@Composable
fun SignInScreen(
    viewModel: SignInViewModel = viewModel(factory = InjectorUtils.provideSignInViewModelFactory(LocalContext.current.applicationContext)),
    onSignUp: () -> Unit,
    onForgotPassword: () -> Unit,
    onProfileSetup: () -> Unit
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
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { viewModel.signIn(uiState.email, uiState.password) },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = uiState.isSubmitEnabled && !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colors.primary,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(text = stringResource(id = R.string.sign_in))
                }
            }
            if (uiState.message != null) {
                Text(
                    text = uiState.message!!,
                    color = MaterialTheme.colors.error,
                    style = MaterialTheme.typography.caption,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            TextButton(
                onClick = onSignUp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Sign Up")
            }
            TextButton(onClick = onForgotPassword) { Text("Forgot Password?") }
        }
    }
    LaunchedEffect(uiState.isAlreadySignIn) {
        if (uiState.isAlreadySignIn) {
            onProfileSetup()
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

@Preview(name = "Sign in light theme")
@Composable
fun SignInPreview() {
    DateMateTheme {
        SignInScreen(onSignUp = {}, onForgotPassword = {}, onProfileSetup = {})
    }
}

@Preview(name = "Sign in dark theme")
@Composable
fun SignInPreviewDark() {
    DateMateTheme(darkTheme = true) {
        SignInScreen(onSignUp = {}, onForgotPassword = {}, onProfileSetup = {})
    }
}
