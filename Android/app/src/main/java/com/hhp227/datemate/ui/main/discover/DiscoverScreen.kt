package com.hhp227.datemate.ui.main.discover

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SentimentDissatisfied
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.hhp227.datemate.common.InjectorUtils
import com.hhp227.datemate.data.model.Profile
import com.hhp227.datemate.ui.theme.DateMateTheme

@Composable
fun DiscoverScreen(
    viewModel: DiscoverViewModel = viewModel(factory = InjectorUtils.provideDiscoverViewModelFactory(LocalContext.current)),
    onNavigateToSubFirst: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val minSectionHeight = LocalConfiguration.current.screenHeightDp.dp * 0.7f

    ScrollView(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Column {
            Text(
                text = "Recommended People for today",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = minSectionHeight), // 최소 높이 설정
                contentAlignment = Alignment.Center
            ) {
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator(color = MaterialTheme.colors.primary)
                    }
                    !uiState.todayRecommendations.isEmpty() -> {
                        TodayRecommendationPager(uiState.todayRecommendations)
                    }
                    else -> {
                        val message = uiState.message ?: "아쉽게도 오늘은 추천 가능한 프로필이 없습니다. 내일 다시 확인해주세요!"

                        EmptyRecommendationView(message = message)
                    }
                }
            }
        }
        Column {
            Text(
                text = "Today's Choice",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            TodaysChoiceSection(onNavigateToSubFirst = onNavigateToSubFirst)
        }
        Column {
            Text(
                text = "Famous People",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items((0..9).toList()) { i ->
                    Card(
                        modifier = Modifier
                            .size(
                                width = (LocalConfiguration.current.screenWidthDp / 3).dp,
                                height = (LocalConfiguration.current.screenWidthDp / 3).dp
                            ),
                        shape = RoundedCornerShape(10.dp),
                        elevation = 4.dp
                    ) {
                        Text(text = "111")
                    }
                }
            }
        }
        Column {
            Text(
                text = "T.O.P Supporter",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items((0..9).toList()) { i ->
                    Card(
                        modifier = Modifier
                            .size(
                                width = (LocalConfiguration.current.screenWidthDp / 3).dp,
                                height = (LocalConfiguration.current.screenWidthDp / 3).dp
                            ),
                        shape = RoundedCornerShape(10.dp),
                        elevation = 4.dp
                    ) {
                        Text(text = "111")
                    }
                }
            }
        }
        TextButton(onClick = {}) {
            Text(text = "Be a TOP Supporter")
        }
        TextButton(onClick = {}) {
            Text(text = "New Recommendation")
        }
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun TodayRecommendationPager(users: List<Profile>) {
    val pagerState = rememberPagerState(initialPage = 0) { users.size }
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    // 화면 높이의 70% 정도를 카드 크기로 지정 (화면을 가득 채우는 큰 카드)
    val cardHeight = screenHeight * 0.7f

    HorizontalPager(
        state = pagerState,
        contentPadding = PaddingValues(horizontal = 20.dp),
        pageSpacing = 10.dp,
        modifier = Modifier.height(cardHeight)
    ) { page ->
        DiscoverFullCard(
            user = users[page],
            onClick = { /* 상세 프로필 이동 */ },
            onLike = { /* 좋아요 액션 */ },
            onPass = { /* 패스 액션 */ }
        )
    }
    /*Spacer(Modifier.height(10.dp))
    Row(
        Modifier
            .fillMaxWidth()
            .height(8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(users.size) { iteration ->
            val color = if (pagerState.currentPage == iteration) MaterialTheme.colors.primary else Color.LightGray
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .clip(CircleShape)
                    .background(color)
                    .size(8.dp)
            )
        }
    }*/
}

@Composable
fun DiscoverFullCard(user: Profile, onClick: () -> Unit, onLike: () -> Unit, onPass: () -> Unit) {
    val PrimaryColor = Color(0xFFFF4081)

    Card(
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = onClick)
            .graphicsLayer { /* 애니메이션 */ },
        shape = RoundedCornerShape(12.dp),
        elevation = 8.dp
    ) {
        Box(Modifier.fillMaxSize()) {
            AsyncImage(
                model = user.photos.firstOrNull(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Transparent,
                                Color.Black.copy(0.8f)
                            )
                        )
                    )
            )
            Column(
                Modifier
                    .align(Alignment.BottomStart)
                    .padding(24.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(PrimaryColor, CircleShape)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text("Today's Pick", color = Color.White, style = MaterialTheme.typography.caption.copy(fontWeight = FontWeight.Bold))
                    }
                    Spacer(Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .border(1.dp, Color.White, CircleShape)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(user.gender, color = Color.White, style = MaterialTheme.typography.caption)
                    }
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    "${user.name}, ${user.ageFormatted}",
                    style = MaterialTheme.typography.h4.copy(fontWeight = FontWeight.Bold, color = Color.White)
                )
                Text(
                    user.job,
                    style = MaterialTheme.typography.subtitle1.copy(color = Color.White.copy(0.9f))
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    user.bio.lines().firstOrNull() ?: "",
                    style = MaterialTheme.typography.body2.copy(color = Color.White.copy(0.8f)),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onPass,
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color.White.copy(alpha = 0.2f), CircleShape)
                            .border(2.dp, Color.White, CircleShape)
                    ) {
                        Icon(Icons.Rounded.Close, null, tint = Color.White, modifier = Modifier.size(32.dp))
                    }
                    Button(
                        onClick = onLike,
                        colors = ButtonDefaults.buttonColors(backgroundColor = PrimaryColor),
                        contentPadding = PaddingValues(horizontal = 32.dp, vertical = 12.dp),
                        modifier = Modifier.height(56.dp)
                    ) {
                        Icon(Icons.Rounded.Favorite, null)
                        Spacer(Modifier.width(8.dp))
                        Text("좋아요")
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyRecommendationView(message: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.SentimentDissatisfied,
            contentDescription = "No Recommendations",
            tint = Color.Gray,
            modifier = Modifier.size(64.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "추천 프로필 없음",
            style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.body2,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )
        Spacer(Modifier.height(24.dp))
        // '필터 변경' 추후 추가
        Button(onClick = { /* 필터 설정 화면으로 이동 등 */ }) {
            Text("추천 기준 변경하기")
        }
    }
}

@Composable
fun TodaysChoiceSection(onNavigateToSubFirst: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height((LocalConfiguration.current.screenWidthDp / 1.5).dp),
        shape = RoundedCornerShape(10.dp),
        elevation = 4.dp
    ) {
        Row(Modifier.fillMaxSize()) {
            // Left Partition
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { onNavigateToSubFirst("Test") }
                    .clip(
                        RoundedCornerShape(
                            topStart = 10.dp,
                            bottomStart = 10.dp,
                            topEnd = 0.dp,
                            bottomEnd = 0.dp
                        )
                    )
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Text("Left")
            }

            // Right Partition
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { /* right click */ }
                    .clip(
                        RoundedCornerShape(
                            topStart = 0.dp,
                            bottomStart = 0.dp,
                            topEnd = 10.dp,
                            bottomEnd = 10.dp
                        )
                    )
                    .background(Color.Gray),
                contentAlignment = Alignment.Center
            ) {
                Text("Right")
            }
        }
    }
}

@Composable
fun TodayRecommendCard(onNavigateToSubFirst: (String) -> Unit) {
    Card(
        modifier = Modifier
            .size(
                width = (LocalConfiguration.current.screenWidthDp / 1.5).dp,
                height = (LocalConfiguration.current.screenWidthDp / 1.5).dp
            ),
        shape = RoundedCornerShape(10.dp),
        elevation = 4.dp
    ) {

        Row(Modifier.fillMaxSize()) {

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { onNavigateToSubFirst("Test") }
                    .clip(RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp))
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Text("Left")
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { }
                    .clip(RoundedCornerShape(topEnd = 10.dp, bottomEnd = 10.dp))
                    .background(Color.Gray),
                contentAlignment = Alignment.Center
            ) {
                Text("Right")
            }
        }
    }
}

@Composable
fun ScrollView(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = verticalArrangement,
        horizontalAlignment = Alignment.CenterHorizontally,
        content = content
    )
}

@Preview(name = "Home in light theme")
@Composable
fun HomePreview() {
    DateMateTheme {
        DiscoverScreen(onNavigateToSubFirst = {})
    }
}

/*
// =================================================================================================
// 더미 데이터 및 상수 정의 (M2와 동일하게 유지)
// =================================================================================================
data class UserProfile(
    val id: Int,
    val name: String,
    val age: Int,
    val job: String,
    val bio: String,
    val gender: Gender,
    val imageUrls: List<String>
)
enum class Gender { MALE, FEMALE }

val PrimaryColor = Color(0xFFFF4081) // 임시 Primary Color

val DummyUser = UserProfile(
    id = 1,
    name = "세아",
    age = 28,
    job = "UX 디자이너",
    bio = "새로운 인연을 찾고 있어요. 🕺",
    gender = Gender.FEMALE,
    imageUrls = listOf("https://picsum.photos/400/600?random=1")
)
val DummyUsers = (1..10).map { i ->
    DummyUser.copy(id = i, name = "사용자 $i", imageUrls = listOf("https://picsum.photos/400/600?random=$i"))
}

// =================================================================================================
// 1. DiscoverScreen (메인 화면)
// =================================================================================================

@Composable
fun DiscoverScreen(
    viewModel: DiscoverViewModel = viewModel(),
    onNavigateToSubFirst: (String) -> Unit
) {
    // ScrollView 대신 Column + verticalScroll 사용
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp) // 섹션 간 간격
    ) {
        // 1. 오늘의 추천 카드 (DiscoverFullCard - Pager)
        Text(
            text = "오늘의 추천 🔥",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.ExtraBold)
        )
        TodayRecommendationPager(users = DummyUsers)

        // 2. 오늘의 반반 선택 카드 (TwoPeopleChoiceSection)
        TwoPeopleChoiceSection(onNavigateToSubFirst = onNavigateToSubFirst)

        // 3. 테마별 추천 섹션들 (ThemedRecommendationSection)
        ThemedRecommendationSection(
            title = "신규 회원 (New)",
            isInitiallyExpanded = true,
            users = DummyUsers.shuffled()
        )
        ThemedRecommendationSection(
            title = "글로벌 친구 (Global)",
            isInitiallyExpanded = true,
            users = DummyUsers.shuffled()
        )
        Spacer(modifier = Modifier.height(10.dp)) // 시각적 분리
        ThemedRecommendationSection(
            title = "최근 접속 (Active)",
            isInitiallyExpanded = false,
            users = DummyUsers.shuffled()
        )
        ThemedRecommendationSection(
            title = "동네 친구 (Nearby)",
            isInitiallyExpanded = false,
            users = DummyUsers.shuffled()
        )

        // 기타 버튼
        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent, contentColor = MaterialTheme.colors.primary),
            elevation = null
        ) {
            Text(text = "더 많은 추천 보기")
        }
        Spacer(modifier = Modifier.height(50.dp)) // 하단 패딩 확보
    }
}

// =================================================================================================
// 1-1. 오늘의 추천 카드 (Horizontal Pager)
// =================================================================================================
@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun TodayRecommendationPager(users: List<UserProfile>) {
    val pagerState = rememberPagerState(initialPage = 0) { users.size }
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    // 화면 높이의 70% 정도를 카드 크기로 지정 (화면을 가득 채우는 큰 카드)
    val cardHeight = screenHeight * 0.7f

    HorizontalPager(
        state = pagerState,
        contentPadding = PaddingValues(horizontal = 20.dp), // 좌우 패딩
        pageSpacing = 10.dp, // 카드 사이 간격
        modifier = Modifier.height(cardHeight)
    ) { page ->
        DiscoverFullCard(
            user = users[page],
            onClick = { /* 상세 프로필 이동 */ },
            onLike = { /* 좋아요 액션 */ },
            onPass = { /* 패스 액션 */ }
        )
    }
    // 인디케이터
    Spacer(Modifier.height(10.dp))
    Row(
        Modifier
            .fillMaxWidth()
            .height(8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(users.size) { iteration ->
            val color = if (pagerState.currentPage == iteration) PrimaryColor else Color.LightGray
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .clip(CircleShape)
                    .background(color)
                    .size(8.dp)
            )
        }
    }
}

// =================================================================================================
// 2. 오늘의 반반 선택 카드 (TwoPeopleChoiceSection)
// =================================================================================================
@Composable
fun TwoPeopleChoiceSection(onNavigateToSubFirst: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "✨ 오늘의 반반 선택",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold)
        )
        // 기존 TodayRecommendSection 로직 (반반 카드)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height((LocalConfiguration.current.screenWidthDp / 1.5).dp),
            shape = RoundedCornerShape(12.dp),
            elevation = 4.dp // Material2 Card elevation
        ) {
            Row(Modifier.fillMaxSize()) {
                // Left Partition
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { onNavigateToSubFirst("Left Pick") }
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Left User Info")
                }
                // Right Partition
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { onNavigateToSubFirst("Right Pick") }
                        .background(Color.Gray),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Right User Info")
                }
            }
        }
    }
}

// =================================================================================================
// 3. 테마별 추천 섹션 (토글 및 50:50 가로 비율)
// =================================================================================================
@Composable
fun ThemedRecommendationSection(
    title: String,
    isInitiallyExpanded: Boolean,
    users: List<UserProfile>
) {
    val isExpanded = remember { mutableStateOf(isInitiallyExpanded) }
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    // 1. 상수 정의
    val paddingHorizontal = 16.dp // 화면 좌우 가장자리 간격 (섹션 타이틀과 동일)
    val itemSpacing = 10.dp // 아이템 사이 간격

    // 2. 카드 너비 계산 (핵심)
    // 목표: (카드 2개 + 3번째 카드 일부)를 화면에 노출
    // 가상의 노출 카드 수: 2.3f (2개는 완전히, 3번째는 약 30% 노출을 목표)
    val visibleItemCount = 2.3f

    // 계산: 화면 전체 너비에서, 양쪽 패딩과 카드 사이 간격(2.3개이므로 2.3 - 1 = 1.3 간격)을 뺀 후,
    // 이를 visibleItemCount로 나눕니다.
    // 하지만 간편하게, 전체 공간을 2.3개의 카드와 2.3개의 간격으로 나누어 계산합니다.

    // 전체 공간 W = N * cardW + (N-1) * spacing + 2 * padding
    // 카드가 2.3개 노출되려면, 전체 너비에서 2.3개의 간격과 2개의 패딩을 빼고 2.3으로 나눕니다.

    // 2.3개의 카드가 노출되는 너비 W' = W - (2 * padding)
    // W' = N * cardW + (N-1) * spacing

    val cardWidth = (screenWidth - (paddingHorizontal * 2) - (itemSpacing * (visibleItemCount - 1))) / visibleItemCount

    // 실제로는 계산이 복잡하므로, 직관적으로 2.3개 노출을 목표로 최종 너비를 설정합니다.
    // (이전 버전의 정렬이 어색했던 이유는 간격 계산이 부정확했기 때문입니다.)

    // 정확히 2.3개가 나오도록 카드 너비 계산:
    // W = 16 + C + 10 + C + 10 + 0.3*C + 16 (오른쪽은 16dp가 아니라 0으로 계산됨)
    // W = 16 + 2.3 * C + 20
    // 2.3 * C = W - 36
    val finalCardWidth = (screenWidth - paddingHorizontal * 2 - itemSpacing) / 2.3f
    // *주의: 이 계산은 2.3개의 카드를 꽉 채우는 너비이며, 첫 번째 카드의 시작과 화면 시작이 16dp로 깔끔하게 맞도록 합니다.

    Column(modifier = Modifier.fillMaxWidth()) {
        // 섹션 타이틀 및 토글 아이콘 (좌우 16dp 패딩)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded.value = !isExpanded.value }
                .padding(horizontal = paddingHorizontal, vertical = 10.dp), // 16dp 패딩
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold)
            )
            Icon(
                imageVector = if (isExpanded.value) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = if (isExpanded.value) "접기" else "펼치기",
                tint = Color.Gray
            )
        }

        // Horizontal 카드 리스트 (펼쳐진 상태일 때만 노출)
        if (isExpanded.value) {
            LazyRow(
                // 1. contentPadding 설정: 좌우에 16dp 패딩 적용
                // 첫 번째 카드가 16dp에서 시작하도록 보장합니다.
                contentPadding = PaddingValues(horizontal = paddingHorizontal),
                // 2. horizontalArrangement 설정: 아이템 사이 간격 10dp 적용
                horizontalArrangement = Arrangement.spacedBy(itemSpacing)
            ) {
                items(users) { user ->
                    ThemedHorizontalCard(user = user, cardWidth = finalCardWidth)
                }
            }
        }
    }
}

// 테마별 추천 섹션 내의 개별 Horizontal 카드
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ThemedHorizontalCard(user: UserProfile, cardWidth: Dp) {
    // 1:1 비율 (정사각형)은 유지합니다.
    Card(
        modifier = Modifier
            .width(cardWidth) // 계산된 너비 적용
            .aspectRatio(1f), // 1:1 비율 (정사각형)
        shape = RoundedCornerShape(12.dp),
        elevation = 4.dp,
        onClick = { /* 테마 카드 클릭 액션 */ }
    ) {
        Box(Modifier.fillMaxSize()) {
            AsyncImage(
                model = user.imageUrls.firstOrNull(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Text(
                text = "${user.name}, ${user.age}",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp),
                style = MaterialTheme.typography.subtitle2
            )
        }
    }
}


// =================================================================================================
// 4. DiscoverFullCard
// =================================================================================================
@Composable
fun DiscoverFullCard(user: UserProfile, onClick: () -> Unit, onLike: () -> Unit, onPass: () -> Unit) {
    val PrimaryColor = Color(0xFFFF4081)

    Card(
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = onClick)
            .graphicsLayer { /* 애니메이션 */ },
        shape = RoundedCornerShape(12.dp),
        elevation = 8.dp // Material2 Card elevation
    ) {
        Box(Modifier.fillMaxSize()) {
            // 1. 이미지
            AsyncImage(
                model = user.imageUrls.firstOrNull(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // 2. 그라데이션
            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Transparent,
                                Color.Black.copy(0.8f)
                            )
                        )
                    )
            )
            // 3. 텍스트 정보 및 버튼
            Column(
                Modifier
                    .align(Alignment.BottomStart)
                    .padding(24.dp)
            ) {
                // 뱃지 등 추가 정보
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(PrimaryColor, CircleShape)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text("Today's Pick", color = Color.White, style = MaterialTheme.typography.caption.copy(fontWeight = FontWeight.Bold)) // M2 스타일 적용
                    }
                    Spacer(Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .border(1.dp, Color.White, CircleShape)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text("${user.gender.name}", color = Color.White, style = MaterialTheme.typography.caption) // M2 스타일 적용
                    }
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    "${user.name}, ${user.age}",
                    style = MaterialTheme.typography.h4.copy(fontWeight = FontWeight.Bold, color = Color.White) // M2 스타일 적용
                )
                Text(
                    user.job,
                    style = MaterialTheme.typography.subtitle1.copy(color = Color.White.copy(0.9f)) // M2 스타일 적용
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    user.bio.lines().firstOrNull() ?: "",
                    style = MaterialTheme.typography.body2.copy(color = Color.White.copy(0.8f)), // M2 스타일 적용
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(20.dp))

                // Action Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onPass,
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color.White.copy(alpha = 0.2f), CircleShape)
                            .border(2.dp, Color.White, CircleShape)
                    ) {
                        Icon(Icons.Rounded.Close, null, tint = Color.White, modifier = Modifier.size(32.dp))
                    }

                    Button(
                        onClick = onLike,
                        colors = ButtonDefaults.buttonColors(backgroundColor = PrimaryColor), // M2 버튼 색상 설정
                        contentPadding = PaddingValues(horizontal = 32.dp, vertical = 12.dp),
                        modifier = Modifier.height(56.dp)
                    ) {
                        Icon(Icons.Rounded.Favorite, null)
                        Spacer(Modifier.width(8.dp))
                        Text("좋아요")
                    }
                }
            }
        }
    }
}

/*@Composable
fun AppleMusicStyleRow(
    items: List<UserProfile>
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp), // Apple Music 좌우 패딩
        horizontalArrangement = Arrangement.spacedBy(14.dp) // Apple Music 간격
    ) {
        items(items) { item ->
            Column(
                modifier = Modifier.width(160.dp) // 고정 크기, Apple Music 스타일
            ) {
                AsyncImage(
                    model = item.imageUrls.first(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(160.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = item.name,
                    fontSize = 16.sp,
                    maxLines = 1
                )
                Text(
                    text = item.bio,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    maxLines = 1
                )
            }
        }
    }
}*/

// =================================================================================================
// 5. 유틸리티 및 프리뷰
// =================================================================================================
@Preview(showBackground = true)
@Composable
fun DiscoverScreenPreview() {
    // 임시 ViewModel (컴파일을 위해 필요)
    MaterialTheme { // Material2 Theme 사용
        DiscoverScreen(viewModel = viewModel()) { /* do nothing */ }
    }
}
*/