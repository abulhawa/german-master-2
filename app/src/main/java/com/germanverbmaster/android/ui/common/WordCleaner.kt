package com.germanverbmaster.android.ui.common

/**
 * Utility to strip metadata (like plurals or usage notes) from word strings.
 * Example: "Gasthof, -ö-e" -> "Gasthof"
 * Example: "Inn, -ö-e" -> "Inn"
 * Example: "Nation(s)" -> "Nation"
 * Example: "Urin, (meist Sg.)" -> "Urin"
 */
object WordCleaner {
    fun clean(text: String?): String {
        if (text == null) return ""
        
        // 1. Remove anything after a comma
        val commaIndex = text.indexOf(',')
        var result = if (commaIndex != -1) {
            text.substring(0, commaIndex)
        } else {
            text
        }

        // 2. Remove anything in parentheses at the end or following the word
        // Handles "Nation(s)" -> "Nation"
        result = result.replace(Regex("\\s*\\(.*?\\).*$"), "")
        
        // 3. Remove trailing (s) if not handled by above
        result = result.removeSuffix("(s)")

        return result.trim()
    }
}
