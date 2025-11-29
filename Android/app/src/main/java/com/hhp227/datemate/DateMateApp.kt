package com.hhp227.datemate

import android.app.Application
import android.content.Context
import androidx.datastore.dataStore
import com.hhp227.datemate.common.PreferenceSerializer
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DateMateApp : Application() {
    companion object {
        val Context.userDataStore by dataStore("user.json", PreferenceSerializer)
    }
}