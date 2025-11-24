package com.hhp227.datemate.ui.main.discover

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class DiscoverViewModel : ViewModel() {
    private var _list = mutableStateListOf<Int>()
    val list: List<Int> = _list

    private var page = 0

    init {
        loadMore()
    }

    fun loadMore() {
        val newItems = (page * 10 until (page + 1) * 10).toList()
        _list.addAll(newItems)
        page++
    }
}