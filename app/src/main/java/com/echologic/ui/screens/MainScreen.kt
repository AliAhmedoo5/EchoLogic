package com.echologic.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import android.content.Intent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import android.widget.Toast
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.echologic.ui.theme.*
import com.echologic.viewmodel.MainViewModel
import com.echologic.ui.components.AdBanner
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.coroutineScope

@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel()
) {
    val quote by viewModel.currentQuote.collectAsState()
    val showHeart by viewModel.showHeart.collectAsState()
    val isPremium by viewModel.isPremium.collectAsState()
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // ── Quote Area (tappable) ──
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp)
                .padding(top = 80.dp, bottom = 160.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Empty line replacement for isHacker state removal
            
            // Category and New badges
            quote?.let { q ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val badgeColor = categoryColor(q.category)
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = badgeColor.copy(alpha = 0.15f),
                        modifier = Modifier
                            .border(
                                width = 2.dp,
                                color = badgeColor,
                                shape = MaterialTheme.shapes.small
                            )
                    ) {
                        Text(
                            text = q.category.uppercase(),
                            style = MaterialTheme.typography.labelLarge,
                            color = badgeColor,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }

                    // NEW badge if created within the last 3 days
                    val threeDaysInMillis = 3 * 24 * 60 * 60 * 1000L
                    if (System.currentTimeMillis() - q.createdAt < threeDaysInMillis) {
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            modifier = Modifier
                                .border(
                                    width = 2.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = MaterialTheme.shapes.small
                                )
                        ) {
                            Text(
                                text = "NEW",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── The Quote ── (double-tap to favorite)
            Box(
                modifier = Modifier
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onDoubleTap = {
                                viewModel.toggleFavorite()
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = quote?.text ?: "Brain off. Logic on. ⚡",
                    transitionSpec = {
                        (fadeIn(animationSpec = tween(300, easing = LinearOutSlowInEasing)) +
                                slideInVertically(
                                    animationSpec = tween(300, easing = LinearOutSlowInEasing),
                                    initialOffsetY = { it / 6 }
                                )).togetherWith(
                            fadeOut(animationSpec = tween(200, easing = FastOutLinearInEasing)) +
                                    scaleOut(targetScale = 0.95f, animationSpec = tween(200))
                        )
                    },
                    label = "QuoteTransition"
                ) { text ->
                    Text(
                        text = "\"$text\"",
                        style = MaterialTheme.typography.displayLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Author
            AnimatedContent(
                targetState = quote?.author ?: "",
                transitionSpec = {
                    fadeIn(animationSpec = tween(400, delayMillis = 100)) togetherWith 
                    fadeOut(animationSpec = tween(200))
                },
                label = "AuthorTransition"
            ) { author ->
                if (author.isNotEmpty()) {
                    Text(
                        text = "— $author",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Favorite, Share & Copy indicator
            quote?.let { q ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    IconButton(
                        onClick = { viewModel.toggleFavorite() },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = if (q.isFavorited) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (q.isFavorited) AccentPink else TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    IconButton(
                        onClick = {
                            com.echologic.util.ShareUtil.shareQuoteAsImage(
                                context = context,
                                quoteText = q.text,
                                author = q.author,
                                category = q.category
                            )
                        },
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = "Share Quote",
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            val copyText = "\"${q.text}\" — ${q.author} (via EchoLogic)"
                            clipboardManager.setText(AnnotatedString(copyText))
                            Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ContentCopy,
                            contentDescription = "Copy Quote",
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // ── Heart Animation (Instagram-style) ──
        if (showHeart) {
            HeartAnimation(
                onFinished = { viewModel.dismissHeart() }
            )
        }

        // ── Top Bar ──
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(top = 24.dp, start = 24.dp, end = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.weight(1f)) // pushes title to center

            Text(
                text = "ECHOLOGIC",
                style = MaterialTheme.typography.labelLarge.copy(
                    letterSpacing = 4.sp
                ),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(2f),
                textAlign = TextAlign.Center
            )

            // Streak Counter
            val currentStreak by viewModel.currentStreak.collectAsState()
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentStreak > 0) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = "Streak",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = currentStreak.toString(),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }

        // ── Bottom Section (Stacked) ──
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Hint
            Text(
                text = "double-tap to favorite",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Echo Button
            EchoButton(
                onClick = { viewModel.fetchNewQuote() }
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            // Ad Banner (if not premium)
            if (!isPremium) {
                AdBanner(
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun EchoButton(onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "buttonScale"
    )

    Button(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = Modifier
            .scale(scale)
            .height(64.dp)
            .width(200.dp),
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 2.dp
        ),
        border = null
    ) {
        Text(
            text = "⚡ ECHO",
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = 18.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Black,
                letterSpacing = 3.sp
            )
        )
    }
}

@Composable
fun HeartAnimation(onFinished: () -> Unit) {
    val scale = remember { Animatable(0f) }
    val opacity = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        // Pop in (Fast and decisive)
        scale.animateTo(
            targetValue = 1.2f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessHigh
            )
        )
        // Hold briefly
        delay(250)
        // Fade out and shrink (Sleek exit)
        coroutineScope {
            launch {
                scale.animateTo(0.9f, animationSpec = tween(300, easing = FastOutSlowInEasing))
            }
            launch {
                opacity.animateTo(0f, animationSpec = tween(300, easing = FastOutSlowInEasing))
            }
        }
        onFinished()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Favorite,
            contentDescription = "Favorited!",
            tint = AccentPink,
            modifier = Modifier
                .size(120.dp)
                .scale(scale.value)
                .alpha(opacity.value)
        )
    }
}
