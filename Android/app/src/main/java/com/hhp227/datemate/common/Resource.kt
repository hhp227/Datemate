package com.hhp227.datemate.common

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T>(data: T? = null) : Resource<T>(data)
}

fun <T> Flow<T>.asResource(): Flow<Resource<T>> =
    this
        .map<T, Resource<T>> { Resource.Success(it) }
        .onStart { emit(Resource.Loading()) }
        .catch { e -> emit(Resource.Error(e.message ?: "Unknown error")) }