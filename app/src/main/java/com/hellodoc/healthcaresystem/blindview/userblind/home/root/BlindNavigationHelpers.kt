package com.hellodoc.healthcaresystem.blindview.userblind.home.root

object BlindNavigationHelpers {
    /**
     * Formats hour and minute into a natural Vietnamese string for TTS.
     * If minute is 0, it omits the "phút" part.
     * Example: (7, 0) -> "7 giờ", (7, 30) -> "7 giờ 30 phút"
     */
    fun formatTimeForTTS(hour: Int, minute: Int): String {
        return if (minute == 0) "$hour giờ" else "$hour giờ $minute phút"
    }

    /**
     * Parses a time string (e.g., "07:30" or "7:30") and formats it for TTS.
     */
    fun formatTimeStringForTTS(time: String): String {
        return try {
            val parts = time.split(":")
            val h = parts[0].trim().toInt()
            val m = if (parts.size >= 2) parts[1].trim().toInt() else 0
            formatTimeForTTS(h, m)
        } catch (e: Exception) {
            time // Fallback to raw string if parsing fails
        }
    }

    /**
     * Calculates the remaining time from [now] to [target] and returns a natural Vietnamese string.
     * - Same day: "còn X giờ Y phút"
     * - > 1 day: "còn X ngày"
     * - > 1 month: "còn X tháng"
     * - > 1 year: "còn X năm"
     */
    fun getRemainingTimeText(target: java.time.ZonedDateTime, now: java.time.ZonedDateTime): String {
        if (target.isBefore(now)) return ""

        val years = java.time.temporal.ChronoUnit.YEARS.between(now, target)
        if (years >= 1) return "còn $years năm nữa là tới thời gian"

        val months = java.time.temporal.ChronoUnit.MONTHS.between(now, target)
        if (months >= 1) return "còn $months tháng nữa là tới thời gian"

        val days = java.time.temporal.ChronoUnit.DAYS.between(now, target)
        if (days >= 1) return "còn $days ngày nữa là tới thời gian"

        val hours = java.time.temporal.ChronoUnit.HOURS.between(now, target)
        val minutes = java.time.temporal.ChronoUnit.MINUTES.between(now, target) % 60
        
        val hourPart = if (hours > 0) "$hours giờ" else ""
        val minutePart = if (minutes > 0) "$minutes phút" else ""
        
        val combined = listOf(hourPart, minutePart).filter { it.isNotEmpty() }.joinToString(" ")
        return if (combined.isNotEmpty()) "còn $combined nữa là tới thời gian" else ""
    }
}
