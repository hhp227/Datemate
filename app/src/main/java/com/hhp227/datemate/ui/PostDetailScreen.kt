package com.hhp227.datemate.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.navigationBarsWithImePadding
import com.hhp227.datemate.R
import com.hhp227.datemate.data.CommentRepository
import com.hhp227.datemate.data.PostRepository
import com.hhp227.datemate.model.Comment
import com.hhp227.datemate.util.viewModelProviderFactoryOf
import com.hhp227.datemate.viewmodel.PostDetailViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PostDetailScreen(
    sheetState: ModalBottomSheetState,
    postKey: String,
    viewModel: PostDetailViewModel = viewModel(factory = viewModelProviderFactoryOf { PostDetailViewModel(PostRepository(), CommentRepository(), postKey) })
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()

    Column {
        LazyColumn(Modifier.weight(1f)) {
            val postState by viewModel.postState
            val commentsState by viewModel.commentsState

            postState.post?.let { post ->
                @OptIn(ExperimentalFoundationApi::class)
                stickyHeader {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_action_account_circle_40),
                                contentDescription = null,
                                modifier = Modifier.size(40.dp, 40.dp)
                            )
                            Text(text = post.author)
                        }
                        Column {
                            Text(text = post.title, maxLines = 1)
                            Text(text = post.body)
                        }
                    }
                    Divider()
                }
            }
            itemsIndexed(commentsState.comments) { i, comment ->
                CommentItem(comment = comment)
            }
        }
        UserInput(
            onMessageSent = {},
            resetScroll = {
                coroutineScope.launch {
                    scrollState.scrollToItem(0)
                }
            },
            modifier = Modifier.navigationBarsWithImePadding()
        )
    }
    if (sheetState.isVisible) {
        BackHandler {
            coroutineScope.launch {
                sheetState.hide()
            }
        }
    }
}

@Composable
fun CommentItem(comment: Comment) {
    Row {
        Image(
            painter = painterResource(id = R.drawable.ic_action_account_circle_40),
            contentDescription = null,
            modifier = Modifier.size(32.dp, 32.dp)
        )
        Column {
            Text(text = comment.author)
            Text(text = comment.text)
        }
    }
}

@Composable
fun UserInput(
    onMessageSent: (String) -> Unit,
    modifier: Modifier = Modifier,
    resetScroll: () -> Unit = {},
) {
    var currentInputSelector by rememberSaveable { mutableStateOf(InputSelector.NONE) }
    val dismissKeyboard = { currentInputSelector = InputSelector.NONE }
    var textState by remember { mutableStateOf(TextFieldValue()) }
    var textFieldFocusState by remember { mutableStateOf(false) }

    // Intercept back navigation if there's a InputSelector visible
    if (currentInputSelector != InputSelector.NONE) {
        BackHandler(onBack = dismissKeyboard)
    }
    Column(modifier) {
        Divider()
        UserInputText(
            textFieldValue = textState,
            onTextChanged = { textState = it },
            // Only show the keyboard if there's no input selector and text field has focus
            keyboardShown = currentInputSelector == InputSelector.NONE && textFieldFocusState,
            // Close extended selector if text field receives focus
            onTextFieldFocused = { focused ->
                if (focused) {
                    currentInputSelector = InputSelector.NONE
                    resetScroll()
                }
                textFieldFocusState = focused
            },
            focusState = textFieldFocusState
        )
    }
}

val KeyboardShownKey = SemanticsPropertyKey<Boolean>("KeyboardShownKey")
var SemanticsPropertyReceiver.keyboardShownProperty by KeyboardShownKey

@Composable
private fun UserInputText(
    keyboardType: KeyboardType = KeyboardType.Text,
    onTextChanged: (TextFieldValue) -> Unit,
    textFieldValue: TextFieldValue,
    keyboardShown: Boolean,
    onTextFieldFocused: (Boolean) -> Unit,
    focusState: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .height(48.dp)
            .semantics {
                contentDescription = "Text input"
                keyboardShownProperty = keyboardShown
            },
        horizontalArrangement = Arrangement.End
    ) {
        Surface {
            Box(
                modifier = Modifier.height(48.dp)
                    .weight(1f)
                    .align(Alignment.Bottom)
            ) {
                var lastFocusState by remember { mutableStateOf(false) }

                BasicTextField(
                    value = textFieldValue,
                    onValueChange = { onTextChanged(it) },
                    modifier = Modifier.fillMaxWidth()
                        .padding(start = 16.dp)
                        .align(Alignment.CenterStart)
                        .onFocusChanged { state ->
                            if (lastFocusState != state.isFocused) {
                                onTextFieldFocused(state.isFocused)
                            }
                            lastFocusState = state.isFocused
                        },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = keyboardType,
                        imeAction = ImeAction.Send
                    ),
                    maxLines = 1,
                    cursorBrush = SolidColor(LocalContentColor.current),
                    textStyle = LocalTextStyle.current.copy(color = LocalContentColor.current)
                )

                val disableContentColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled)

                if (textFieldValue.text.isEmpty() && !focusState) {
                    Text(
                        modifier = Modifier.align(Alignment.CenterStart)
                            .padding(start = 16.dp),
                        text = "Enter Message",
                        style = MaterialTheme.typography.body1.copy(color = disableContentColor)
                    )
                }
            }
        }
    }
}

enum class InputSelector {
    NONE,
    MAP,
    DM,
    EMOJI,
    PHONE,
    PICTURE
}