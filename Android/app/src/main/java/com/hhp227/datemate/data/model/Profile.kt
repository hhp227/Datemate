package com.hhp227.datemate.data.model

import com.google.firebase.Timestamp
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Calendar

data class Profile(
    var uid: String = "",
    val name: String = "",
    val gender: String = "",
    val bio: String = "",
    val birthday: Timestamp? = null,
    val job: String = "",
    val country: String = "",
    val photos: List<String> = emptyList(),
    val randomKey: Double = 0.0,
    val updatedAt: Timestamp? = null
) {
    val ageFormatted: String
        get(): String {
            val birth = this.birthday ?: return "00"
            val birthCal = Calendar.getInstance().apply {
                time = birth.toDate()
            }
            val nowCal = Calendar.getInstance()
            var age = nowCal.get(Calendar.YEAR) - birthCal.get(Calendar.YEAR)

            // 생일이 안 지났으면 나이 -1
            val currentMonth = nowCal.get(Calendar.MONTH)
            val birthMonth = birthCal.get(Calendar.MONTH)
            val currentDay = nowCal.get(Calendar.DAY_OF_MONTH)
            val birthDay = birthCal.get(Calendar.DAY_OF_MONTH)

            if (currentMonth < birthMonth || (currentMonth == birthMonth && currentDay < birthDay)) {
                age--
            }
            if (age < 0) age = 0
            return "%02d".format(age)
    }
}
