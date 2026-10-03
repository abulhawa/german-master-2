package com.germanverbmaster.android.data.sync

import com.germanverbmaster.android.data.util.PosNormalizer

object PartOfSpeechMapper {
    /**
     * Maps Android and legacy POS values to the compact tags accepted by Supabase history tables.
     */
    fun toCanonical(pos: String): String = PosNormalizer.normalize(pos)
}
