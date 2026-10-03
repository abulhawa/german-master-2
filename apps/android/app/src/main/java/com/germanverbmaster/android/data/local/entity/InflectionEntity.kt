package com.germanverbmaster.android.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "inflections",
    foreignKeys = [ForeignKey(
        entity = LexemeEntity::class,
        parentColumns = ["id"],
        childColumns = ["lexemeId"],
        onDelete = ForeignKey.CASCADE,
    )],
    indices = [Index("lexemeId")],
)
data class InflectionEntity(
    @PrimaryKey val id: String,
    val lexemeId: String,
    val form: String,
    val featuresJson: String,  // e.g. {"tense":"present","person":"1","number":"sg"}
    val audioAsset: String? = null,
)
