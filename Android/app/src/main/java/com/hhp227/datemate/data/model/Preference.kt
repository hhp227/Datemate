package com.hhp227.datemate.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Preference(val userCache: UserCache? = null)