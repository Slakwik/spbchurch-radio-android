package com.spbchurch.radio.util

object RadioTitle {
    private val NOISE = listOf(
        "SPBChurch Radio",
        "SPB Church Radio",
        "SPBChurch",
        "SPB Church",
        "Церковь «Преображение»",
        "Церковь Преображение",
        "Преображение"
    )

    private val SEPARATORS = setOf('-', '–', '—', '|', ':', '·', '•', '*', '\t', ' ', '\u00A0')

    fun cleaned(raw: String): String {
        var result = raw
        for (token in NOISE) {
            result = result.replace(token, "", ignoreCase = true)
        }
        while (result.isNotEmpty() && SEPARATORS.contains(result.first())) {
            result = result.drop(1)
        }
        while (result.isNotEmpty() && SEPARATORS.contains(result.last())) {
            result = result.dropLast(1)
        }
        return result.trim()
    }

    fun isSearchable(raw: String): Boolean {
        val cleaned = cleaned(raw)
        if (cleaned.isEmpty()) return false
        val placeholders = setOf("нет данных", "offline", "—", "-", "прямой эфир", "загрузка", "загрузка...")
        return cleaned.lowercase() !in placeholders
    }
}
