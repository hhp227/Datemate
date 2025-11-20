package com.hhp227.datemate.ui.main.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hhp227.datemate.ui.theme.DateMateTheme

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onNavigateToSubFirst: (String) -> Unit
) {
    ScrollView(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Column {
            Text(
                text = "Recommended People for today",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            //TodayRecommendSection(onNavigateToSubFirst = onNavigateToSubFirst, items = viewModel.list, onLoadMore = viewModel::loadMore)
            TodayRecommendSection(onNavigateToSubFirst = onNavigateToSubFirst)
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

@Composable
fun TodayRecommendSection(onNavigateToSubFirst: (String) -> Unit) {
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

/*@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TodayRecommendSection(
    onNavigateToSubFirst: (String) -> Unit,
    items: List<Int>,
    onLoadMore: () -> Unit
) {
    val listState = rememberLazyListState()

    // Snap behavior
    val flingBehavior = rememberSnapFlingBehavior(listState)

    // Load more when reaching end
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
            lastVisible != null && lastVisible >= items.size - 1
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) onLoadMore()
    }

    LazyRow(
        state = listState,
        flingBehavior = flingBehavior,
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items) {
            TodayRecommendCard(onNavigateToSubFirst)
        }
    }
}*/

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
        HomeScreen(onNavigateToSubFirst = {})
    }
}