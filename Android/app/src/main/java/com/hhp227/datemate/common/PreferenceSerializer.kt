package com.hhp227.datemate.common

import androidx.datastore.core.Serializer
import com.hhp227.datemate.data.model.Preference
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object PreferenceSerializer : Serializer<Preference> {
    override val defaultValue: Preference
        get() = Preference(null)

    override suspend fun readFrom(input: InputStream): Preference {
        return try {
            Json.decodeFromString(Preference.serializer(), input.readBytes().decodeToString())
        } catch (e: SerializationException) {
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: Preference, output: OutputStream) {
        output.write(Json.encodeToString(Preference.serializer(), t).encodeToByteArray())
    }
}