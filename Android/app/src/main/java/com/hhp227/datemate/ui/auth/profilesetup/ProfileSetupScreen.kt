package com.hhp227.datemate.ui.auth.profilesetup

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hhp227.datemate.R

@Composable
fun ProfileSetupScreen(
    onSetupComplete: () -> Unit
) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var bioText by remember { mutableStateOf("") }

    // 갤러리에서 이미지 선택을 위한 Launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Setup Your Profile",
            style = MaterialTheme.typography.h4,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.primary
        )

        Spacer(modifier = Modifier.height(40.dp))

        // 프로필 이미지 영역 (클릭 시 갤러리 오픈)
        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier
                .size(140.dp)
                .clickable { launcher.launch("image/*") }
        ) {
            // 이미지가 선택되었으면 해당 이미지, 아니면 기본 아이콘 표시
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.1f),
                modifier = Modifier.fillMaxSize()
            ) {
                if (imageUri != null) {
                    // 실제 앱에서는 Coil 라이브러리의 AsyncImage 사용 권장
                    // AsyncImage(model = imageUri, contentDescription = null, contentScale = ContentScale.Crop)
                    Image(
                        painter = painterResource(id = R.drawable.ic_logo_light_eng), // 임시 플레이스홀더
                        contentDescription = "Profile Image",
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.padding(24.dp),
                        tint = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
                    )
                }
            }

            // 카메라 아이콘 뱃지
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colors.primary,
                modifier = Modifier.size(40.dp).border(2.dp, MaterialTheme.colors.surface, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Edit",
                    tint = Color.White,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 간단한 자기소개 입력
        OutlinedTextField(
            value = bioText,
            onValueChange = { bioText = it },
            label = { Text("Short Bio") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3
        )

        Spacer(modifier = Modifier.weight(1f))

        // 완료 버튼
        Button(
            onClick = onSetupComplete,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text("Complete Setup")
        }
    }
}