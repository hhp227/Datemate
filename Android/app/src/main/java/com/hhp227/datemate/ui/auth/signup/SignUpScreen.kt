package com.hhp227.datemate.ui.auth.signup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hhp227.datemate.common.InjectorUtils
import com.hhp227.datemate.ui.auth.EmailField
import com.hhp227.datemate.ui.auth.GeneralTextField
import com.hhp227.datemate.ui.auth.PasswordField

@Composable
fun SignUpScreen(
    viewModel: SignUpViewModel = viewModel(factory = InjectorUtils.provideSignUpViewModelFactory()),
    onSignUpSuccess: () -> Unit,
    onBackToSignIn: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(uiState.isSignUpSuccess) {
        if (uiState.isSignUpSuccess) {
            viewModel.consumeSuccessEvent()
            onSignUpSuccess()
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Create an Account",
            style = MaterialTheme.typography.h5,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GeneralTextField(
                label = "Full Name",
                value = uiState.name,
                onValueChange = viewModel::onNameChange,
                imeAction = ImeAction.Next
            )
            Spacer(modifier = Modifier.height(16.dp))
            EmailField(
                value = uiState.email,
                error = uiState.emailError,
                onValueChange = viewModel::onEmailChange,
                imeAction = ImeAction.Next
            )
            Spacer(modifier = Modifier.height(16.dp))
            PasswordField(
                value = uiState.password,
                error = uiState.passwordError,
                onValueChange = viewModel::onPasswordChange,
                imeAction = ImeAction.Next
            )
            Spacer(modifier = Modifier.height(16.dp))
            PasswordField(
                value = uiState.confirmPassword,
                error = uiState.confirmPasswordError, // 비밀번호 불일치 에러 등
                onValueChange = viewModel::onConfirmPasswordChange,
                imeAction = ImeAction.Done,
                onImeAction = {
                    if(uiState.isSignUpEnabled) {
                        viewModel.signUp()
                    }
                }
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { viewModel.signUp() },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = uiState.isSignUpEnabled
            ) {
                Text(text = "Sign Up", style = MaterialTheme.typography.button)
            }
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = onBackToSignIn) {
                Text(
                    text = buildAnnotatedString {
                        append("Already have an account? ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Sign In")
                        }
                    }
                )
            }
        }
    }
    if (uiState.errorMessage != null) {
        // Toast나 Snackbar 로직 추가 가능
    }
}