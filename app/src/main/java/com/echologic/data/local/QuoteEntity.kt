package com.echologic.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quotes")
data class QuoteEntity(
    @PrimaryKey
    val id: String = "",
    val text: String = "",
    val category: String = "",
    val author: String = "",
    val isPremium: Boolean = false,
    val isFavorited: Boolean = false,
    val globalLikesCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
