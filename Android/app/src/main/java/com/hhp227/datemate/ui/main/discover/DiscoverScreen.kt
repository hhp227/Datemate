package com.hhp227.datemate.ui.main.discover

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.SentimentDissatisfied
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.runtime.*
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
import androidx.compose.ui.unit.Dp
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

    if (!uiState.isLoading) {
        ScrollView(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            TodayRecommendationSection("Recommended People for today", uiState.todayRecommendations)
            TodaysChoiceSection(
                title = "Today's Choice",
                uiState = uiState,
                onClick = {
                    if (uiState.selectedProfile == null) {
                        viewModel.selectChoice(it)  // 첫 클릭 → 애니메이션 실행
                    } else if (uiState.selectedProfile?.uid == it.uid) {
                        onNavigateToSubFirst(it.uid) // 두 번째 클릭 → 화면 이동
                    }
                }
            )

            // 테마별 섹션 시작
            ThemedRecommendationSection(
                title = "Popular Members",
                isInitiallyExpanded = true,
                users = uiState.themedPopular
            )
            ThemedRecommendationSection(
                title = "New Members",
                isInitiallyExpanded = true,
                users = uiState.themedNewMembers
            )
            ThemedRecommendationSection(
                title = "Global Friends",
                isInitiallyExpanded = false,
                users = uiState.themedGlobalFriends
            )
            ThemedRecommendationSection(
                title = "Recent Active Members",
                isInitiallyExpanded = false,
                users = uiState.themedRecentActive
            )
            // TODO
            /*ThemedRecommendationSection(
                title = "동네 친구 (Nearby)",
                isInitiallyExpanded = false,
                users = viewModel.DummyUsers.shuffled()
            )*/
            TextButton(onClick = {}) {
                Text(text = "Be a TOP Supporter")
            }
            TextButton(onClick = {}) {
                Text(text = "New Recommendation")
            }
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()   // 둥근 로딩바
        }
    }
}

@Composable
fun TodayRecommendationSection(title: String, profiles: List<Profile>) {
    val minSectionHeight = LocalConfiguration.current.screenHeightDp.dp * 0.7f

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp), // 16dp 패딩
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = minSectionHeight), // 최소 높이 설정
            contentAlignment = Alignment.Center
        ) {
            if (!profiles.isEmpty()) {
                TodayRecommendationPager(profiles)
            } else {
                EmptyRecommendationView(message = "아쉽게도 오늘은 추천 가능한 프로필이 없습니다. 내일 다시 확인해주세요!")
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
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
fun TodaysChoiceSection(
    title: String,
    uiState: DiscoverUiState,
    onClick: (Profile) -> Unit
) {
    val left = uiState.leftProfile
    val right = uiState.rightProfile
    val selected = uiState.selectedProfile

    if (left == null || right == null) {
        Text("오늘의 추천이 부족합니다.", modifier = Modifier.padding(20.dp))
    } else {
        val animationSpec = tween<Float>(durationMillis = 400)
        val animatedWeightLeft by animateFloatAsState(
            targetValue = when {
                selected == null -> 1f
                selected.uid == left.uid -> 2f
                else -> 0f
            },
            animationSpec = animationSpec
        )
        val animatedWeightRight by animateFloatAsState(
            targetValue = when {
                selected == null -> 1f
                selected.uid == right.uid -> 2f
                else -> 0f
            },
            animationSpec = animationSpec
        )

        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp), // 16dp 패딩
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            if (uiState.isLoading) {
                Text("Loading...", modifier = Modifier.padding(20.dp))
                return
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height((LocalConfiguration.current.screenWidthDp / 1.2).dp),
                shape = RoundedCornerShape(10.dp),
                elevation = 4.dp
            ) {
                Row(Modifier.fillMaxSize()) {

                    // LEFT
                    if (animatedWeightLeft > 0f) {
                        ChoiceProfileCard(
                            profile = left,
                            isSelected = selected?.uid == left.uid,
                            isInitial = selected == null,
                            isLeftCard = true, // ⬅️ 왼쪽 카드임을 명시적으로 전달
                            modifier = Modifier
                                .weight(animatedWeightLeft)
                                .fillMaxHeight()
                                .clickable { onClick(left) }
                        )
                    }

                    // RIGHT
                    if (animatedWeightRight > 0f) {
                        ChoiceProfileCard(
                            profile = right,
                            isSelected = selected?.uid == right.uid,
                            isInitial = selected == null,
                            isLeftCard = false, // ⬅️ 오른쪽 카드임을 명시적으로 전달
                            modifier = Modifier
                                .weight(animatedWeightRight)
                                .fillMaxHeight()
                                .clickable { onClick(right) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChoiceProfileCard(
    profile: Profile,
    isSelected: Boolean,
    isInitial: Boolean,  // 초기 50:50 상태인지 여부 (선택 표시용)
    isLeftCard: Boolean,
    modifier: Modifier = Modifier
) {
    val alpha = if (isSelected) 1f else 0.85f // 선택된 카드는 좀 더 선명하게

    Box(modifier = modifier) {
        AsyncImage(
            model = profile.photos.firstOrNull(),
            contentDescription = "Profile Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.6f * alpha)
                        ),
                        startY = 0f
                    )
                )
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)
        ) {
            // (1) 선택 전: "CHOICE" 라벨 표시
            if (isInitial) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colors.primary)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "CHOICE",
                        color = Color.White,
                        style = MaterialTheme.typography.caption.copy(fontWeight = FontWeight.Bold)
                    )
                }
                Spacer(Modifier.height(4.dp))
            } else if (isSelected) {
                // (2) 선택 후: 안내 메시지 표시 (선택되었다는 점을 강조)
                Text(
                    text = "당신의 선택입니다!",
                    color = Color.White.copy(0.7f),
                    style = MaterialTheme.typography.caption.copy(fontWeight = FontWeight.Medium)
                )
                Spacer(Modifier.height(4.dp))
            }


            // (3) 이름 및 나이
            Text(
                text = "${profile.name}, ${profile.ageFormatted}",
                color = Color.White,
                style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.ExtraBold),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
            // (4) 직업/소개
            Text(
                text = profile.job,
                color = Color.White.copy(0.8f),
                style = MaterialTheme.typography.body2,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
        if (isInitial && isLeftCard) {
            // 왼쪽 카드에만 오른쪽 테두리를 추가하여 시각적 구분
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.CenterEnd)
                    .width(1.dp)
                    .background(Color.White.copy(0.3f))
            )
        }
    }
}

@Composable
fun ThemedRecommendationSection(
    title: String,
    isInitiallyExpanded: Boolean,
    users: List<Profile>
) {
    val isExpanded = remember { mutableStateOf(isInitiallyExpanded) }
    val itemPadding = 20.dp
    val itemSpacing = 10.dp // 아이템 사이 간격

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp), // 16dp 패딩
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Icon(
                imageVector = if (isExpanded.value) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = if (isExpanded.value) "접기" else "펼치기",
                tint = Color.Gray
            )
        }
        if (isExpanded.value) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = itemPadding),
                horizontalArrangement = Arrangement.spacedBy(itemSpacing)
            ) {
                items(users) { user ->
                    ThemedHorizontalCard(user = user, cardWidth = ((LocalConfiguration.current.screenWidthDp - ((itemPadding.value * 2) + itemSpacing.value)) / 2).dp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ThemedHorizontalCard(user: Profile, cardWidth: Dp) {
    Card(
        modifier = Modifier
            .width(cardWidth)
            .aspectRatio(1f), // 1:1 비율 (정사각형)
        shape = RoundedCornerShape(12.dp),
        elevation = 4.dp,
        onClick = { /* 테마 카드 클릭 액션 */ }
    ) {
        Box(Modifier.fillMaxSize()) {
            AsyncImage(
                model = user.photos.firstOrNull(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Text(
                text = "${user.name}, ${user.ageFormatted}",
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