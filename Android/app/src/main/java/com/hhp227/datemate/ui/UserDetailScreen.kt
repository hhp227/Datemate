package com.hhp227.datemate.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hhp227.datemate.R

@Composable
fun UserDetailScreen() {
    ScrollView(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        LazyRow(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items((0..3).toList()) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_background),
                    contentDescription = null,
                    modifier = Modifier
                        .width((LocalConfiguration.current.screenWidthDp - (20 * 2)).dp)
                        .aspectRatio(0.8f)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
        Column {
            Text(
                text = "유저 정보",
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Divider()
            ((0..3).toList()).forEach {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = "정보: ")
                    Text(text = "정보")
                }
            }
        }
        Button(
            onClick = { /*TODO*/ },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
        ) {
            Text(text = "좋아요")
        }
        Button(
            onClick = { /*TODO*/ },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
        ) {
            Text(text = "연락처 오픈")
        }
    }
    Text(text = "Hello UserDetailScreen")
}

@Composable
@Preview
fun UserDetailScreenPreview() {
    UserDetailScreen()
}