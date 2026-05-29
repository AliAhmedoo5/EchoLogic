package com.echologic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.echologic.data.local.QuoteEntity
import com.echologic.data.repository.QuoteRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val repository: QuoteRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    enum class SortOrder { NEWEST, OLDEST, AUTHOR_AZ }
    private val _sortOrder = MutableStateFlow(SortOrder.NEWEST)
    val sortOrder: StateFlow<SortOrder> = _sortOrder

    val favoritedQuotes: StateFlow<List<QuoteEntity>> = combine(
        _selectedCategory.flatMapLatest { category ->
            if (category == null) repository.getFavoritedQuotes()
            else repository.getFavoritedQuotesByCategory(category)
        },
        _searchQuery,
        _sortOrder
    ) { quotes, query, sort ->
        var result = quotes
        
        // Search
        if (query.isNotBlank()) {
            val lowerQuery = query.lowercase()
            result = result.filter { 
                it.text.lowercase().contains(lowerQuery) || 
                it.author.lowercase().contains(lowerQuery) 
            }
        }
        
        // Sort
        result = when (sort) {
            SortOrder.NEWEST -> result.sortedByDescending { it.createdAt }
            SortOrder.OLDEST -> result.sortedBy { it.createdAt }
            SortOrder.AUTHOR_AZ -> result.sortedBy { it.author }
        }
        
        result
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: StateFlow<List<String>> = repository.getFavoritedCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateSortOrder(order: SortOrder) {
        _sortOrder.value = order
    }

    fun selectCategory(category: String?) {
        _selectedCategory.value = category
    }

    fun removeFavorite(quoteId: String) {
        viewModelScope.launch {
            repository.toggleFavorite(auth.currentUser?.uid, quoteId, false)
        }
    }
}

