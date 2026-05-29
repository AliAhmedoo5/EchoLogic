package com.echologic.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface QuoteDao {
    @Query("SELECT * FROM quotes ORDER BY RANDOM() LIMIT 1")
    fun getRandomQuote(): Flow<QuoteEntity>

    @Query("SELECT * FROM quotes WHERE id = :id")
    suspend fun getQuoteById(id: String): QuoteEntity?

    @Query("SELECT * FROM quotes WHERE id NOT IN (:excludeIds) ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomQuoteExcluding(excludeIds: List<String>): QuoteEntity?

    @Query("SELECT * FROM quotes WHERE category IN (:categories) AND id NOT IN (:excludeIds) ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomQuoteFromCategories(categories: List<String>, excludeIds: List<String>): QuoteEntity?

    @Query("SELECT * FROM quotes ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomQuoteOnce(): QuoteEntity?

    @Query("SELECT * FROM quotes WHERE isFavorited = 1 ORDER BY category ASC")
    fun getFavoritedQuotes(): Flow<List<QuoteEntity>>

    @Query("SELECT * FROM quotes WHERE isFavorited = 1 AND category = :category ORDER BY text ASC")
    fun getFavoritedQuotesByCategory(category: String): Flow<List<QuoteEntity>>

    @Query("SELECT DISTINCT category FROM quotes WHERE isFavorited = 1")
    fun getFavoritedCategories(): Flow<List<String>>

    @Query("UPDATE quotes SET isFavorited = :isFavorited WHERE id = :quoteId")
    suspend fun updateFavorite(quoteId: String, isFavorited: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuote(quote: QuoteEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIgnore(quotes: List<QuoteEntity>)

    @Query("UPDATE quotes SET text = :text, author = :author, category = :category, globalLikesCount = :likes WHERE id = :id")
    suspend fun updateCloudFields(id: String, text: String, author: String, category: String, likes: Int)

    @Query("SELECT COUNT(*) FROM quotes")
    suspend fun getQuoteCount(): Int

    @Query("SELECT id FROM quotes WHERE isFavorited = 1")
    suspend fun getFavoritedIds(): List<String>

    @Query("UPDATE quotes SET isFavorited = 1 WHERE id IN (:ids)")
    suspend fun setFavoritesByIds(ids: List<String>)

    @Query("UPDATE quotes SET isFavorited = 0")
    suspend fun resetAllFavorites()
}
