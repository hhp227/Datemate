package com.hhp227.datemate.ui.auth.profilesetup

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.hhp227.datemate.common.InjectorUtils
import com.hhp227.datemate.data.model.Gender
import com.hhp227.datemate.ui.auth.TextFieldError

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileSetupScreen(
    viewModel: ProfileSetupViewModel = viewModel(factory = InjectorUtils.provideProfileSetupViewModelFactory()),
    onSetupComplete: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val allUris = uiState.selectedImageUris
    val maxImages = 7
    val primaryUri = allUris.firstOrNull()
    val secondaryUris = if (allUris.size > 1) allUris.subList(1, allUris.size) else emptyList()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        viewModel.onImagesSelected(uris)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Setup Your Profile",
            style = MaterialTheme.typography.h4,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.primary,
            modifier = Modifier.padding(bottom = 40.dp)
        )
        Text(
            text = "성별을 선택해 주세요",
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        GenderSelector(
            selectedGender = uiState.selectedGender,
            onGenderSelected = viewModel::onGenderSelected
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = uiState.nickname,
            onValueChange = viewModel::onNicknameChange,
            label = { Text("Nickname") },
            modifier = Modifier.fillMaxWidth(),
            isError = uiState.nicknameError != null
        )
        if (uiState.nicknameError != null) TextFieldError(textError = uiState.nicknameError!!)
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Profile Photos (${allUris.size} / Max $maxImages)", // 텍스트 업데이트
            style = MaterialTheme.typography.subtitle1,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.align(Alignment.Start).padding(bottom = 8.dp)
        )
        if (allUris.isEmpty()) {
            PrimaryImageAddButton(onClick = { launcher.launch("image/*") })
        } else {
            PrimaryProfileImage(
                uri = primaryUri!!,
                onRemove = { viewModel.removeImage(primaryUri) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                maxItemsInEachRow = 3
            ) {
                if (allUris.size < maxImages) {
                    SmallImageAddButton(
                        onClick = { launcher.launch("image/*") },
                        modifier = Modifier.fillMaxWidth(0.31f).aspectRatio(1f)
                    )
                }
                secondaryUris.forEach { uri ->
                    SelectedProfileImage(
                        uri = uri,
                        onRemove = { viewModel.removeImage(uri) },
                        modifier = Modifier.fillMaxWidth(0.31f).aspectRatio(1f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = viewModel::completeProfileSetup,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(16.dp),
            enabled = uiState.isSubmitEnabled && !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colors.onPrimary, modifier = Modifier.size(24.dp))
            } else {
                Text("완료")
            }
        }
    }
}

@Composable
fun PrimaryImageAddButton(onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(2.dp, MaterialTheme.colors.primary),
        color = MaterialTheme.colors.surface,
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Primary Photo",
                tint = MaterialTheme.colors.primary,
                modifier = Modifier.size(48.dp)
            )
            Text("대표 사진 추가", color = MaterialTheme.colors.primary)
        }
    }
}

@Composable
fun SmallImageAddButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colors.primary.copy(alpha = 0.6f)),
        color = MaterialTheme.colors.surface,
        modifier = modifier
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Photo",
                tint = MaterialTheme.colors.primary.copy(alpha = 0.8f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun SelectedProfileImage(uri: Uri, onRemove: () -> Unit, modifier: Modifier = Modifier) {
    val painter = rememberAsyncImagePainter(uri)

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopEnd
    ) {
        Image(
            painter = painter,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
        )
        IconButton(
            onClick = onRemove,
            modifier = Modifier
                .offset(x = 4.dp, y = (-4).dp)
                .size(20.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.6f))
                .border(1.5.dp, Color.White, CircleShape)
                .padding(0.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove",
                tint = Color.White,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}

@Composable
fun PrimaryProfileImage(uri: Uri, onRemove: () -> Unit) {
    val painter = rememberAsyncImagePainter(uri)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .border(2.dp, MaterialTheme.colors.primary, RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.TopEnd
    ) {
        Image(
            painter = painter,
            contentDescription = "Primary Profile Photo",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp))
        )
        IconButton(
            onClick = onRemove,
            modifier = Modifier
                .offset(x = 8.dp, y = (-8).dp)
                .size(24.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.6f))
                .border(2.dp, Color.White, CircleShape)
                .padding(0.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove Primary Image",
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
fun GenderSelector(
    selectedGender: Gender?,
    onGenderSelected: (Gender) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        GenderChip(
            gender = Gender.MALE,
            label = "남성",
            isSelected = selectedGender == Gender.MALE,
            onClick = { onGenderSelected(Gender.MALE) },
            modifier = Modifier.weight(1f)
        )
        GenderChip(
            gender = Gender.FEMALE,
            label = "여성",
            isSelected = selectedGender == Gender.FEMALE,
            onClick = { onGenderSelected(Gender.FEMALE) },
            modifier = Modifier.weight(1f)
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GenderChip(
    gender: Gender,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) MaterialTheme.colors.primary else Color.LightGray.copy(alpha = 0.5f),
        border = if (isSelected) null else BorderStroke(1.dp, Color.Gray.copy(alpha = 0.5f))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                color = if (isSelected) MaterialTheme.colors.onPrimary else Color.DarkGray,
                style = MaterialTheme.typography.button
            )
        }
    }
}