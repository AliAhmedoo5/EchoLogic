package com.echologic.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.echologic.ui.theme.*
import com.echologic.viewmodel.LibraryViewModel
import androidx.compose.foundation.ExperimentalFoundationApi

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LibraryScreen(
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val favoritedQuotes by viewModel.favoritedQuotes.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 16.dp)
    ) {
        // ── Header ──
        Text(
            text = "YOUR LIBRARY",
            style = MaterialTheme.typography.labelLarge.copy(
                letterSpacing = 4.sp
            ),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(bottom = 8.dp)
        )

        Text(
            text = "Saved Quotes",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        val searchQuery by viewModel.searchQuery.collectAsState()
        val sortOrder by viewModel.sortOrder.collectAsState()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                placeholder = { Text("Search quotes...", style = MaterialTheme.typography.bodySmall) },
                leadingIcon = { 
                    Icon(
                        imageVector = Icons.Filled.Search, 
                        contentDescription = "Search",
                        modifier = Modifier.size(18.dp)
                    ) 
                },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)
                ),
                textStyle = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.width(12.dp))

            var sortExpanded by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
            Box {
                IconButton(onClick = { sortExpanded = true }) {
                    Icon(
                        imageVector = Icons.Filled.Menu,
                        contentDescription = "Sort",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                DropdownMenu(
                    expanded = sortExpanded,
                    onDismissRequest = { sortExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Newest Added") },
                        onClick = { 
                            viewModel.updateSortOrder(LibraryViewModel.SortOrder.NEWEST)
                            sortExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Oldest Added") },
                        onClick = { 
                            viewModel.updateSortOrder(LibraryViewModel.SortOrder.OLDEST)
                            sortExpanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Author (A-Z)") },
                        onClick = { 
                            viewModel.updateSortOrder(LibraryViewModel.SortOrder.AUTHOR_AZ)
                            sortExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ── Category Filter Chips ──
        if (categories.isNotEmpty()) {
            LazyRow(
                modifier = Modifier.padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // "All" chip
                item {
                    CategoryChip(
                        label = "All",
                        color = MaterialTheme.colorScheme.primary,
                        isSelected = selectedCategory == null,
                        onClick = { viewModel.selectCategory(null) }
                    )
                }
                items(categories) { category ->
                    CategoryChip(
                        label = category,
                        color = categoryColor(category),
                        isSelected = selectedCategory == category,
                        onClick = { viewModel.selectCategory(category) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // ── Quote List or Empty State ──
        if (favoritedQuotes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No favorites yet",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Double-tap quotes on the feed\nto save them here",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            val context = LocalContext.current
            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(favoritedQuotes, key = { it.id }) { quote ->
                    Box(modifier = Modifier.animateItemPlacement()) {
                        QuoteCard(
                            text = quote.text,
                            category = quote.category,
                            author = quote.author,
                            onRemove = { viewModel.removeFavorite(quote.id) },
                            onShare = {
                                com.echologic.util.ShareUtil.shareQuoteAsImage(
                                    context = context,
                                    quoteText = quote.text,
                                    author = quote.author,
                                    category = quote.category
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryChip(
    label: String,
    color: androidx.compose.ui.graphics.Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) color.copy(alpha = 0.2f) else androidx.compose.ui.graphics.Color.Transparent,
        label = "chipBg"
    )

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(4.dp),
        color = bgColor,
        modifier = Modifier.border(
            width = 2.dp,
            color = if (isSelected) color else TextMuted,
            shape = RoundedCornerShape(4.dp)
        )
    ) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) color else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun QuoteCard(
    text: String,
    category: String,
    author: String,
    onRemove: () -> Unit,
    onShare: () -> Unit = {}
) {
    val accentColor = categoryColor(category)

    Surface(
        shape = RoundedCornerShape(4.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = accentColor.copy(alpha = 0.3f),
                shape = RoundedCornerShape(4.dp)
            )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category label
                Text(
                    text = category.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = accentColor
                )

                // Action buttons
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = onShare,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = "Share quote",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(
                        onClick = onRemove,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Remove from favorites",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "\"$text\"",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "— $author",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}
