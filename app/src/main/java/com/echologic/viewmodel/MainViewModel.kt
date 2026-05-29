package com.echologic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.echologic.data.local.QuoteEntity
import com.echologic.data.repository.QuoteRepository
import com.echologic.data.repository.SettingsRepository
import com.echologic.data.repository.StreakRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: QuoteRepository,
    private val settingsRepository: SettingsRepository,
    private val streakRepository: StreakRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    val currentStreak: StateFlow<Int> = streakRepository.currentStreak

    private val _currentQuote = MutableStateFlow<QuoteEntity?>(null)
    val currentQuote: StateFlow<QuoteEntity?> = _currentQuote

    private val _showHeart = MutableStateFlow(false)
    val showHeart: StateFlow<Boolean> = _showHeart

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _refreshCount = MutableStateFlow(0)
    val refreshCount: StateFlow<Int> = _refreshCount

    val isPremium: StateFlow<Boolean> = settingsRepository.isPremium
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val selectedCategories: StateFlow<Set<String>> = settingsRepository.selectedCategories

    init {
        viewModelScope.launch {
            // Seed database locally on first launch if empty
            repository.ensureSeeded()
            
            // Local-First: Load first quote immediately
            _isLoading.value = false
            fetchNewQuote()

            // Observe category changes to immediately refresh if current quote is invalid
            launch {
                selectedCategories.collect { selected ->
                    val current = _currentQuote.value
                    if (current != null && selected.isNotEmpty() && !selected.contains(current.category)) {
                        fetchNewQuote()
                    }
                }
            }

            // Background: Pull latest from Firestore
            launch {
                settingsRepository.syncAvailableCategories()
                repository.refreshQuotes()
            }
            
            // Background: Auto-Sync local data to Cloud
            launch {
                try {
                    repository.uploadSeededQuotesToFirestore()
                } catch (e: Exception) {
                    // Silently fail or log for background sync
                }
            }
        }
    }

    private val recentQuoteIds = mutableListOf<String>()

    fun fetchNewQuote() {
        viewModelScope.launch {
            val categories = selectedCategories.value.toList()
            var quote = repository.getRandomQuote(
                excludeIds = recentQuoteIds,
                categories = categories
            )
            
            // Fallback if filtering or exclusion returns nothing
            if (quote == null) {
                recentQuoteIds.clear()
                quote = repository.getRandomQuote(
                    excludeIds = emptyList(),
                    categories = categories
                )
            }

            if (quote != null) {
                _currentQuote.value = quote
                _refreshCount.value++
                
                recentQuoteIds.add(quote.id)
                if (recentQuoteIds.size > 20) {
                    recentQuoteIds.removeAt(0)
                }
            }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            _currentQuote.value?.let { quote ->
                val newState = !quote.isFavorited
                
                // Optimistic UI update
                _currentQuote.value = quote.copy(isFavorited = newState)
                if (newState) {
                    _showHeart.value = true
                }

                // Background sync
                repository.toggleFavorite(auth.currentUser?.uid, quote.id, newState)
            }
        }
    }

    fun dismissHeart() {
        _showHeart.value = false
    }
}
