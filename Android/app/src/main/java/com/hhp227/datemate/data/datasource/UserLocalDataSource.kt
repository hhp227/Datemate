package com.hhp227.datemate.data.datasource

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import com.hhp227.datemate.DateMateApp.Companion.userDataStore
import com.hhp227.datemate.data.model.Preference
import com.hhp227.datemate.data.model.UserCache
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class UserLocalDataSource private constructor(
    private val dataStore: DataStore<Preference>
) {
    val preference: Flow<Preference>
        get() = dataStore.data.catch { e ->
            if (e is IOException) {
                Log.e(TAG, "Error reading preference.", e)
                emit(Preference())
            } else {
                throw e
            }
        }

    val userFlow: Flow<UserCache?>
        get() = preference.map { it.userCache }

    suspend fun storeUser(user: UserCache?) {
        dataStore.updateData { it.copy(user) }
    }

    companion object {
        private val TAG = UserLocalDataSource::class.java.simpleName

        @Volatile
        private var instance: UserLocalDataSource? = null

        fun getInstance(context: Context) =
            instance ?: synchronized(this) {
                instance ?: UserLocalDataSource(context.userDataStore).also { instance = it }
            }
    }
}