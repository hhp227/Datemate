package com.hhp227.datemate.ui.auth.phoneauth

import android.app.Activity
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hhp227.datemate.common.InjectorUtils
import com.hhp227.datemate.common.Utils

@Composable
fun PhoneAuthScreen(
    viewModel: PhoneAuthViewModel = viewModel(factory = InjectorUtils.providePhoneAuthViewModelFactory(LocalContext.current)),
    onVerified: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var phoneNumber by remember { mutableStateOf("") }
    var otpCode by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    LaunchedEffect(uiState.isVerified) {
        if (uiState.isVerified) onVerified()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (!uiState.isCodeSent) "휴대폰 인증" else "인증번호 입력",
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (!uiState.isCodeSent) "안전한 사용을 위해 휴대폰 번호를 입력해주세요." else "문자로 전송된 6자리 코드를 입력해주세요.",
            style = MaterialTheme.typography.body2,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(40.dp))
        AnimatedContent(targetState = uiState.isCodeSent, label = "AuthStep") { isCodeSent ->
            if (!isCodeSent) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { input ->
                            if (input.length <= 20 && input.all { it.isDigit() || it == '+' }) {
                                phoneNumber = input
                            }
                        },
                        label = { Text("휴대폰 번호 (예: 01012345678)") },
                        placeholder = { Text("+821012345678") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = MaterialTheme.colors.primary,
                            cursorColor = MaterialTheme.colors.primary
                        )
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            val formattedNumber = Utils.formatToE164(phoneNumber)

                            focusManager.clearFocus()
                            viewModel.sendOtp(formattedNumber) { context as Activity }
                        },
                        enabled = phoneNumber.length >= 10 && !uiState.isLoading,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.primary,
                            contentColor = Color.White
                        )
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colors.primary,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("인증번호 받기", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    TextButton(onClick = { /* 번호 수정 로직 */ }) {
                        Text("번호가 $phoneNumber 맞나요?", style = MaterialTheme.typography.caption)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    OtpInputField(
                        otpText = otpCode,
                        onOtpModified = { value, complete ->
                            otpCode = value

                            if (complete) {
                                focusManager.clearFocus()
                                viewModel.verifyOtp(value)
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = { viewModel.verifyOtp(otpCode) },
                        enabled = otpCode.length == 6 && !uiState.isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colors.primary,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("인증하기", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
        if (uiState.errorMessage != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = uiState.errorMessage ?: "",
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.body2
            )
        }
    }
}

@Composable
fun OtpInputField(
    otpText: String,
    onOtpModified: (String, Boolean) -> Unit
) {
    BasicTextField(
        value = otpText,
        onValueChange = {
            if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                onOtpModified(it, it.length == 6)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        decorationBox = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(6) { index ->
                    val char = when {
                        index < otpText.length -> otpText[index].toString()
                        else -> ""
                    }
                    val isFocused = otpText.length == index
                    val primaryColor = MaterialTheme.colors.primary
                    val backgroundColor = if (char.isNotEmpty()) primaryColor.copy(alpha = 0.1f) else Color.Transparent
                    val borderColor = if (isFocused) primaryColor else Color.LightGray

                    Box(
                        modifier = Modifier
                            .width(45.dp)
                            .height(50.dp)
                            .background(
                                color = backgroundColor,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .border(
                                width = if (isFocused) 2.dp else 1.dp,
                                color = borderColor,
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = char,
                            style = MaterialTheme.typography.h6,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            color = if(char.isNotEmpty()) primaryColor else Color.Black
                        )
                    }
                }
            }
        }
    )
}