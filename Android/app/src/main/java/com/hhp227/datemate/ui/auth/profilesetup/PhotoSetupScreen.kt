package com.hhp227.datemate.ui.auth.profilesetup

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hhp227.datemate.common.InjectorUtils

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PhotoSetupScreen(
    viewModel: ProfileSetupViewModel = viewModel(factory = InjectorUtils.provideProfileSetupViewModelFactory()),
    onNext: () -> Unit,
    onNavigateUp: () -> Unit
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

    Scaffold(
        topBar = { TopAppBar(title = { Text("프로필 사진 설정 (2/3)") }, navigationIcon = { IconButton(onClick = onNavigateUp) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") } }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "나를 잘 나타내는 사진을 올려주세요.",
                style = MaterialTheme.typography.subtitle1,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.Start).padding(bottom = 16.dp)
            )
            Text(
                text = "Profile Photos (${allUris.size} / Max $maxImages)",
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
                    secondaryUris.forEach { uri ->
                        SelectedProfileImage(
                            uri = uri,
                            onRemove = { viewModel.removeImage(uri) },
                            modifier = Modifier.fillMaxWidth(0.31f).aspectRatio(1f)
                        )
                    }
                    if (allUris.size < maxImages) {
                        SmallImageAddButton(
                            onClick = { launcher.launch("image/*") },
                            modifier = Modifier.fillMaxWidth(0.31f).aspectRatio(1f)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = onNext,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = allUris.isNotEmpty()
            ) {
                Text("다음")
            }
            Spacer(modifier = Modifier.height(24.dp)) // GenderSetupScreen과 동일한 하단 패딩을 위해 24.dp를 추가
        }
    }
}