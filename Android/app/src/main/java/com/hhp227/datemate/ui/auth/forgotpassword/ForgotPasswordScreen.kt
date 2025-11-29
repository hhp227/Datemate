package com.hhp227.datemate.ui.auth.forgotpassword

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hhp227.datemate.common.InjectorUtils
import com.hhp227.datemate.ui.auth.EmailField
import kotlinx.coroutines.delay

@Composable
fun ForgotPasswordScreen(
    viewModel: ForgotPasswordViewModel = viewModel(factory = InjectorUtils.provideForgotPasswordViewModelFactory(LocalContext.current.applicationContext)),
    onBackToSignIn: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isEmailSent) {
        if (uiState.isEmailSent) {
            // 예시: 3초 후 로그인 화면으로 자동 복귀
            delay(3000)
            viewModel.consumeMessage()
            onBackToSignIn()
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(64.dp))
        Text(
            text = "Forgot Your Password?",
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "Enter your email address to receive a password reset link.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.subtitle1,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(bottom = 32.dp)
        )
        EmailField(
            value = uiState.email,
            error = uiState.emailError,
            onValueChange = viewModel::onEmailChange,
            imeAction = ImeAction.Done,
            onImeAction = { viewModel.sendResetEmail(uiState.email) }
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { viewModel.sendResetEmail(uiState.email) },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            enabled = !uiState.isLoading,
            shape = RoundedCornerShape(16.dp)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colors.primary,
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text(text = "Send Reset Link")
            }
        }
        if (uiState.message != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = uiState.message!!,
                color = if (uiState.isEmailSent) Color(0xFF4CAF50) else MaterialTheme.colors.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        TextButton(onClick = onBackToSignIn) {
            Text("Back to Sign In")
        }
    }
}