package com.germanverbmaster.android.data.util

import java.time.Instant

object DateTimeUtils {
    /**
     * Normalizes an ISO-8601 date string to a consistent format.
     * Uses [Instant.toString] which produces the canonical representation:
     * e.g., "2023-10-01T10:00:00Z"
     */
    fun normalizeIso8601(isoString: String?): String {
        if (isoString.isNullOrBlank()) return ""
        return try {
            // Robust normalization:
            // 1. Convert space to T (Postgres to ISO)
            // 2. Ensure it has a timezone (Z) if none present
            // 3. Parse and truncate to second-precision
            val normalized = isoString.replace(" ", "T")
            val withZ = if (!normalized.contains("Z") && !normalized.contains("+")) {
                "${normalized}Z"
            } else normalized
            
            Instant.parse(withZ)
                .with(java.time.temporal.ChronoField.NANO_OF_SECOND, 0)
                .toString()
        } catch (e: Exception) {
            // Final fallback for malformed strings: take first 19 chars if they look like a date
            if (isoString.length >= 19 && isoString[4] == '-' && isoString[7] == '-') {
                isoString.substring(0, 10) + "T" + isoString.substring(11, 19) + "Z"
            } else isoString
        }
    }
}
