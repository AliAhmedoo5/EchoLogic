package com.echologic.data.repository

import android.util.Log
import com.echologic.data.local.QuoteDao
import com.echologic.data.local.QuoteEntity
import com.echologic.data.local.QuoteSeeder
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class QuoteRepository @Inject constructor(
    private val quoteDao: QuoteDao,
    private val firestore: FirebaseFirestore,
    private val analytics: FirebaseAnalytics
) {

    /**
     * Seeds the database with initial quotes if it's empty.
     * Called once on app startup from MainViewModel.
     */
    suspend fun ensureSeeded() {
        try {
            if (quoteDao.getQuoteCount() == 0) {
                quoteDao.insertIgnore(QuoteSeeder.getInitialQuotes())
                Log.d("QuoteRepository", "Seeded ${QuoteSeeder.getInitialQuotes().size} quotes")
            }
        } catch (e: Exception) {
            Log.e("QuoteRepository", "Failed to seed quotes: ${e.message}")
        }
    }

    suspend fun getRandomQuote(
        excludeIds: List<String> = emptyList(),
        categories: List<String> = emptyList()
    ): QuoteEntity? {
        return try {
            if (categories.isNotEmpty()) {
                quoteDao.getRandomQuoteFromCategories(categories, excludeIds)
            } else if (excludeIds.isNotEmpty()) {
                quoteDao.getRandomQuoteExcluding(excludeIds)
            } else {
                quoteDao.getRandomQuoteOnce()
            }
        } catch (e: Exception) {
            Log.w("QuoteRepository", "Error fetching quote: ${e.message}")
            null
        }
    }

    fun getFavoritedQuotes(): Flow<List<QuoteEntity>> {
        return quoteDao.getFavoritedQuotes()
            .catch { e ->
                Log.w("QuoteRepository", "Error fetching favorites: ${e.message}")
                emit(emptyList())
            }
    }

    fun getFavoritedQuotesByCategory(category: String): Flow<List<QuoteEntity>> {
        return quoteDao.getFavoritedQuotesByCategory(category)
            .catch { e ->
                Log.w("QuoteRepository", "Error fetching by category: ${e.message}")
                emit(emptyList())
            }
    }

    fun getFavoritedCategories(): Flow<List<String>> {
        return quoteDao.getFavoritedCategories()
            .catch { e ->
                Log.w("QuoteRepository", "Error fetching categories: ${e.message}")
                emit(emptyList())
            }
    }

    suspend fun toggleFavorite(userId: String?, quoteId: String, isFavorited: Boolean) {
        try {
            // Always update LOCAL database first (instant for the user)
            quoteDao.updateFavorite(quoteId, isFavorited)
            Log.d("QuoteRepository", "toggleFavorite: Local updated. userId=$userId quoteId=$quoteId fav=$isFavorited")
            
            // Sync with Global Likes count
            try {
                val quoteRef = firestore.collection("quotes").document(quoteId)
                firestore.runTransaction { transaction ->
                    val snapshot = transaction.get(quoteRef)
                    if (snapshot.exists()) {
                        val currentLikes = snapshot.getLong("globalLikesCount") ?: 0L
                        val newLikes = if (isFavorited) currentLikes + 1L else currentLikes - 1L
                        transaction.update(quoteRef, "globalLikesCount", maxOf(0L, newLikes))
                    }
                }.await()
            } catch (e: Exception) {
                Log.w("QuoteRepository", "Global likes sync failed (non-fatal): ${e.message}")
            }

            // Sync with User-specific favorites collection
            if (userId != null) {
                val userFavRef = firestore.collection("users").document(userId)
                    .collection("favorites").document(quoteId)
                
                if (isFavorited) {
                    val quote = quoteDao.getQuoteById(quoteId)
                    val favoriteData = mapOf(
                        "id" to quoteId,
                        "text" to (quote?.text ?: ""),
                        "author" to (quote?.author ?: "Unknown"),
                        "category" to (quote?.category ?: "General"),
                        "timestamp" to com.google.firebase.Timestamp.now()
                    )
                    userFavRef.set(favoriteData)
                    Log.d("QuoteRepository", "✅ Favorite SAVED to cloud for user=$userId quote=$quoteId")
                } else {
                    userFavRef.delete()
                    Log.d("QuoteRepository", "🗑️ Favorite REMOVED from cloud for user=$userId quote=$quoteId")
                }
            } else {
                Log.w("QuoteRepository", "⚠️ userId is NULL - favorite NOT saved to cloud!")
            }

            // Log event to Analytics
            if (isFavorited) {
                val bundle = android.os.Bundle().apply {
                    putString(FirebaseAnalytics.Param.ITEM_ID, quoteId)
                    putString("action", "liked")
                }
                analytics.logEvent("quote_interaction", bundle)
            }
        } catch (e: Exception) {
            Log.e("QuoteRepository", "Error toggling favorite: ${e.message}", e)
        }
    }

    suspend fun refreshQuotes() {
        try {
            val snapshots = firestore.collection("quotes").get().await()
            val cloudQuotes = snapshots.mapNotNull { it.toObject(QuoteEntity::class.java) }
            
            if (cloudQuotes.isNotEmpty()) {
                // 1. Insert new ones without touching anything
                quoteDao.insertIgnore(cloudQuotes)
                
                // 2. Update existing ones but ONLY the non-local fields
                for (quote in cloudQuotes) {
                    quoteDao.updateCloudFields(
                        id = quote.id,
                        text = quote.text,
                        author = quote.author,
                        category = quote.category,
                        likes = quote.globalLikesCount
                    )
                }
            }
        } catch (e: Exception) {
            Log.w("QuoteRepository", "Firestore sync skipped: ${e.message}")
        }
    }
    
    suspend fun uploadSeededQuotesToFirestore() {
        try {
            val quotes = QuoteSeeder.getInitialQuotes()
            val batch = firestore.batch()
            for (quote in quotes) {
                val ref = firestore.collection("quotes").document(quote.id)
                // Only upload "pure" quote data to the global collection
                val cloudData = mapOf(
                    "id" to quote.id,
                    "text" to quote.text,
                    "author" to quote.author,
                    "category" to quote.category,
                    "isPremium" to quote.isPremium,
                    "globalLikesCount" to quote.globalLikesCount
                    // NOTE: We do NOT upload isFavorited here. 
                    // That is local-only state in this version.
                )
                batch.set(ref, cloudData, com.google.firebase.firestore.SetOptions.merge())
            }
            batch.commit().await()
            Log.d("QuoteRepository", "Successfully uploaded ${quotes.size} quotes to Firestore")
        } catch (e: Exception) {
            Log.e("QuoteRepository", "Failed to upload quotes to Firestore: ${e.message}")
            throw e
        }
    }

    suspend fun mergeFavoritesWithCloud(userId: String) {
        try {
            Log.d("QuoteRepository", "mergeFavoritesWithCloud START for user=$userId")
            
            // Get Cloud Favorites for this user
            val snapshots = firestore.collection("users").document(userId)
                .collection("favorites").get().await()
            val cloudFavIds = snapshots.documents.map { it.id }
            
            Log.d("QuoteRepository", "Cloud favorites found: ${cloudFavIds.size} → $cloudFavIds")
            
            if (cloudFavIds.isEmpty()) {
                Log.d("QuoteRepository", "No cloud favorites to restore for user=$userId")
                return
            }
            
            // For each cloud favorite, ensure the quote exists locally and mark it as favorited
            for (doc in snapshots.documents) {
                val id = doc.id
                val existingQuote = quoteDao.getQuoteById(id)
                
                if (existingQuote != null) {
                    // Quote exists locally — just mark it favorited
                    quoteDao.updateFavorite(id, true)
                    Log.d("QuoteRepository", "Restored favorite (existing): $id")
                } else {
                    // Quote doesn't exist locally — insert it from cloud data
                    val text = doc.getString("text") ?: ""
                    val author = doc.getString("author") ?: "Unknown"
                    val category = doc.getString("category") ?: "General"
                    
                    val entity = QuoteEntity(
                        id = id,
                        text = text,
                        author = author,
                        category = category,
                        isFavorited = true
                    )
                    quoteDao.insertQuote(entity)
                    Log.d("QuoteRepository", "Restored favorite (new insert): $id")
                }
            }
            
            Log.d("QuoteRepository", "mergeFavoritesWithCloud COMPLETE: restored ${cloudFavIds.size} favorites")
        } catch (e: Exception) {
            Log.e("QuoteRepository", "Cloud favorites merge failed: ${e.message}", e)
        }
    }

    suspend fun submitQuote(text: String, author: String, category: String, userId: String?) {
        try {
            val submission = mapOf(
                "text" to text,
                "author" to author,
                "category" to category,
                "submittedBy" to (userId ?: "anonymous"),
                "isApproved" to false,
                "timestamp" to com.google.firebase.Timestamp.now()
            )
            firestore.collection("submissions").add(submission).await()
            Log.d("QuoteRepository", "Quote submitted successfully for moderation")
        } catch (e: Exception) {
            Log.e("QuoteRepository", "Failed to submit quote: ${e.message}")
            throw e
        }
    }
}
