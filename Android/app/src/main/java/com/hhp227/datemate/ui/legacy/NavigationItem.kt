package com.hhp227.datemate.ui.legacy

import com.hhp227.datemate.R

sealed class NavigationItem(var route: String, var icon: Int, var title: String) {
    object Home : NavigationItem("home", R.drawable.ic_launcher_foreground, "Home")
    object Lounge : NavigationItem("lounge", R.drawable.ic_launcher_foreground, "Lounge")
}