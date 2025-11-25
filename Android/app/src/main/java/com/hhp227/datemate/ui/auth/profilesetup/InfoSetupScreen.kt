package com.hhp227.datemate.ui.auth.profilesetup

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hhp227.datemate.common.InjectorUtils
import com.hhp227.datemate.ui.auth.TextFieldError
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun InfoSetupScreen(
    viewModel: ProfileSetupViewModel = viewModel(factory = InjectorUtils.provideProfileSetupViewModelFactory()),
    onSetupComplete: () -> Unit,
    onNavigateUp: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val dateFormatter = remember { SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREA) }

    // Date Picker Dialog 설정
    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            // DatePicker의 월(month)은 0부터 시작하므로 그대로 사용
            val selectedCalendar = Calendar.getInstance().apply {
                set(year, month, dayOfMonth, 0, 0, 0) // 시간을 0으로 설정하여 정확한 날짜만 저장
                set(Calendar.MILLISECOND, 0)
            }
            viewModel.onBirthdaySelected(selectedCalendar.timeInMillis)
        },
        Calendar.getInstance().get(Calendar.YEAR) - 20,
        Calendar.getInstance().get(Calendar.MONTH),
        Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    )

    LaunchedEffect(uiState.isSetupComplete) {
        if (uiState.isSetupComplete) {
            viewModel.consumeSetupCompleteEvent()
            onSetupComplete()
        }
    }
    Scaffold(
        topBar = { TopAppBar(title = { Text("프로필 정보 입력 (3/3)") }, navigationIcon = { IconButton(onClick = onNavigateUp) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") } }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "나에 대한 매력적인 정보를 알려주세요.",
                style = MaterialTheme.typography.subtitle1,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.Start).padding(top = 16.dp, bottom = 24.dp)
            )
            OutlinedTextField(
                value = uiState.fullName,
                onValueChange = viewModel::onFullNameChange,
                label = { Text("Full Name (필수)") },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.fullNameError != null,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
            )
            if (uiState.fullNameError != null) TextFieldError(textError = uiState.fullNameError!!)
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = if (uiState.birthdayMillis != null) dateFormatter.format(Date(uiState.birthdayMillis!!)) else "",
                onValueChange = { /* 읽기 전용 */ },
                label = { Text("생년월일 (필수)") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { datePickerDialog.show() }) {
                        Icon(Icons.Default.CalendarToday, contentDescription = "Select Birthday")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { datePickerDialog.show() },
                isError = uiState.birthdayError != null
            )
            if (uiState.birthdayError != null) TextFieldError(textError = uiState.birthdayError!!)
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = uiState.bio,
                onValueChange = viewModel::onBioChange,
                label = { Text("자기소개 (필수)") },
                placeholder = { Text("나를 어필할 수 있는 매력적인 문구를 작성해주세요. (200자 이내)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp),
                singleLine = false,
                maxLines = 5,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
            )
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                value = uiState.job,
                onValueChange = viewModel::onJobChange,
                label = { Text("직업 (필수)") },
                placeholder = { Text("직업이나 하는 일을 입력해주세요.") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.jobError != null
            )
            if (uiState.jobError != null) TextFieldError(textError = uiState.jobError!!)
            // * 기타 정보 (키, 관심사 등)는 Dropdown이나 Chip 방식으로 구현 가능

            Spacer(modifier = Modifier.height(40.dp))
            Button(
                onClick = { viewModel.completeProfileSetup() },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = uiState.isSubmitEnabled && !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = MaterialTheme.colors.onPrimary, modifier = Modifier.size(24.dp))
                } else {
                    Text("프로필 설정 완료")
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}