package com.hhp227.datemate

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

open class TextFieldState(
    private val validator: (String) -> Boolean = { true },
    private val errorFor: (String) -> String = { "" }
) {
    open val isValid: Boolean
        get() = validator(text)

    private var displayErrors: Boolean by mutableStateOf(false)

    var text: String by mutableStateOf("")

    var isFocusedDirty: Boolean by mutableStateOf(false)

    var isFocused: Boolean by mutableStateOf(false)

    open fun getError(): String? {
        return if (showErrors()) {
            errorFor(text)
        } else {
            null
        }
    }

    fun onFocusChange(focused: Boolean) {
        isFocused = focused
        if (focused) isFocusedDirty = true
    }

    fun enableShowErrors() {
        // only show errors if the text was at least once focused
        if (isFocusedDirty) {
            displayErrors = true
        }
    }

    fun showErrors() = !isValid && displayErrors
}