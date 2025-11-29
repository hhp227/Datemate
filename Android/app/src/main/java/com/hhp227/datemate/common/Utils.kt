package com.hhp227.datemate.common

object Utils {
    // 사용자가 '010'으로 시작하게 입력했을 경우 '+82'로 자동 변환 처리
    fun formatToE164(number: String): String {
        val cleanNumber = number.trim()
        return when {
            // 1. 이미 '+'로 시작하면 그대로 둠
            cleanNumber.startsWith("+") -> cleanNumber

            // 2. '010' 등 '0'으로 시작하면 한국 국가코드(+82)로 치환 (0 제거)
            cleanNumber.startsWith("0") -> "+82${cleanNumber.substring(1)}"

            // 3. 그 외의 경우 (예: 8210...) '+'만 붙여줌
            else -> "+$cleanNumber"
        }
    }
}