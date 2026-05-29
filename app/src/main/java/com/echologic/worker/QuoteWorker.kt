package com.echologic.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.echologic.data.repository.QuoteRepository
import com.echologic.util.NotificationUtil
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull

@HiltWorker
class QuoteWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val quoteRepository: QuoteRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            NotificationUtil.createNotificationChannel(context)
            
            // Get a random quote
            val quote = quoteRepository.getRandomQuote()
            
            if (quote != null) {
                NotificationUtil.showNotification(context, quote.text, quote.author)
            }
            
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
