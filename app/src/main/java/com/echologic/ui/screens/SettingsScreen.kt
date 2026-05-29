package com.echologic.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalContext
import com.echologic.ui.theme.*
import com.echologic.viewmodel.SettingsViewModel
import com.echologic.data.repository.SettingsRepository

/** Formats hour and minute to a readable 12-hour time string. */
private fun formatTime(hour: Int, minute: Int): String {
    val amPm = if (hour < 12) "AM" else "PM"
    val displayHour = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    return String.format("%d:%02d %s", displayHour, minute, amPm)
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val isPremium by viewModel.isPremium.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val accentColorInt by viewModel.accentColor.collectAsState()
    val availableCategories by viewModel.availableCategories.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp)
            .padding(bottom = 32.dp)
    ) {
        // ── Header ──
        Text(
            text = "SETTINGS",
            style = MaterialTheme.typography.labelLarge.copy(
                letterSpacing = 4.sp
            ),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Customize",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Spacer(modifier = Modifier.height(32.dp))

        // ── Premium Upgrade Card ──
        if (!isPremium) {
            Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.small
                    )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "GO PRO",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• Remove all ads\n• Custom Accent Colors\n• High-Retention Daily Notifications\n• Custom Typography & Personalization",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { 
                            (context as? android.app.Activity)?.let { activity ->
                                viewModel.upgradeToPremium(activity) 
                            }
                        },
                        shape = MaterialTheme.shapes.small,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "UPGRADE — ONE TIME",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        } else {
            Surface(
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.secondary,
                        shape = MaterialTheme.shapes.small
                    )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "You're PRO!",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Categories (FREE for all users) ──
        SettingsSection(title = "CATEGORIES") {
            val selected = viewModel.selectedCategories.collectAsState().value

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                availableCategories.forEach { category ->
                    CategoryItem(
                        name = category,
                        isSelected = selected.contains(category),
                        isEnabled = true,
                        onToggle = { viewModel.toggleCategory(category) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Community ──
        var showSubmitDialog by remember { mutableStateOf(false) }

        if (showSubmitDialog) {
            com.echologic.ui.components.SubmitQuoteDialog(
                onDismiss = { showSubmitDialog = false },
                onSubmit = { text, author, category ->
                    showSubmitDialog = false
                    viewModel.submitQuote(text, author, category) { success ->
                        if (success) {
                            Toast.makeText(context, "Quote submitted for review!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Failed to submit quote.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            )
        }

        SettingsSection(title = "COMMUNITY") {
            SettingsLinkItem(
                label = "Submit a Quote",
                icon = Icons.Filled.Add
            ) {
                showSubmitDialog = true
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Premium Features ──
        val isDailyNotificationEnabled = viewModel.isDailyNotificationEnabled.collectAsState().value
        val notificationHour by viewModel.notificationHour.collectAsState()
        val notificationMinute by viewModel.notificationMinute.collectAsState()
        var showTimePicker by remember { mutableStateOf(false) }

        // Permission launcher for POST_NOTIFICATIONS (Android 13+)
        val notificationPermissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
            contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                viewModel.toggleDailyNotification(true)
                Toast.makeText(context, "Daily quotes enabled!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Notification permission denied. Enable in Settings.", Toast.LENGTH_LONG).show()
            }
        }

        // Time Picker Dialog
        if (showTimePicker) {
            val timePickerState = rememberTimePickerState(
                initialHour = notificationHour,
                initialMinute = notificationMinute,
                is24Hour = false
            )
            AlertDialog(
                onDismissRequest = { showTimePicker = false },
                title = { 
                    Text(
                        "Set Notification Time",
                        style = MaterialTheme.typography.titleMedium
                    ) 
                },
                text = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TimePicker(state = timePickerState)
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.setNotificationTime(timePickerState.hour, timePickerState.minute)
                        showTimePicker = false
                        Toast.makeText(
                            context,
                            "Notification set for ${formatTime(timePickerState.hour, timePickerState.minute)}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }) {
                        Text("SET")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showTimePicker = false }) {
                        Text("CANCEL")
                    }
                },
                shape = MaterialTheme.shapes.small,
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.onSurface,
                textContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        SettingsSection(title = "PREMIUM FEATURES") {
            SettingsToggleItem(
                label = "Daily Quote Notification",
                icon = Icons.Filled.Notifications,
                isChecked = isDailyNotificationEnabled,
                onCheckedChange = { enabled ->
                    if (isPremium) {
                        if (enabled) {
                            // Check notification permission on Android 13+
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                                val permission = android.Manifest.permission.POST_NOTIFICATIONS
                                if (androidx.core.content.ContextCompat.checkSelfPermission(context, permission) 
                                    != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                                    notificationPermissionLauncher.launch(permission)
                                    return@SettingsToggleItem
                                }
                            }
                            viewModel.toggleDailyNotification(true)
                        } else {
                            viewModel.toggleDailyNotification(false)
                        }
                    } else {
                        Toast.makeText(context, "This is a Premium feature.", Toast.LENGTH_SHORT).show()
                    }
                }
            )

            // Show time picker when notifications are enabled
            if (isDailyNotificationEnabled && isPremium) {
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showTimePicker = true }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Notifications,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Notification Time",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = formatTime(notificationHour, notificationMinute),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "CHANGE",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Appearance ──
        SettingsSection(title = "APPEARANCE") {
            SettingsToggleItem(
                label = "Dark Mode",
                icon = Icons.Filled.Star, // Using Star as a placeholder for "Dark/Light"
                isChecked = isDarkMode,
                onCheckedChange = { viewModel.setDarkMode(it) }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Accent Color",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            val presets = listOf(
                0xFFF4FF4D.toInt(), // Yellow
                0xFF00FFFF.toInt(), // Cyan
                0xFFFF00FF.toInt(), // Pink
                0xFFA855F7.toInt(), // Purple
                0xFF10B981.toInt(), // Green
                0xFFF59E0B.toInt()  // Orange
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                presets.forEach { colorInt ->
                    val color = Color(colorInt)
                    val isSelected = accentColorInt == colorInt
                    val scale by animateFloatAsState(
                        targetValue = if (isSelected) 1.1f else 1f,
                        animationSpec = spring(stiffness = Spring.StiffnessMedium),
                        label = "accentScale"
                    )

                    Surface(
                        modifier = Modifier
                            .size(40.dp)
                            .scale(scale)
                            .border(
                                width = if (isSelected) 3.dp else 0.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable {
                                if (isPremium) {
                                    viewModel.setAccentColor(colorInt)
                                } else {
                                    Toast.makeText(context, "Upgrade to Premium to change accent color!", Toast.LENGTH_SHORT).show()
                                }
                            },
                        shape = CircleShape,
                        color = color
                    ) {}
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // ── About ──
        SettingsSection(title = "ABOUT") {
            var tapCount by remember { mutableIntStateOf(0) }
            
            Text(
                text = "EchoLogic v1.0",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.clickable { 
                    if (com.echologic.BuildConfig.DEBUG) {
                        tapCount++
                        if (tapCount >= 7) {
                            viewModel.forceUnlockPremium()
                            Toast.makeText(context, "Developer Mode: Premium Unlocked ⚡", Toast.LENGTH_SHORT).show()
                            tapCount = 0
                        }
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
            
            // Helpful Links
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SettingsLinkItem("Rate App", Icons.Filled.Star) {
                    val packageName = context.packageName
                    try {
                        uriHandler.openUri("market://details?id=$packageName")
                    } catch (e: Exception) {
                        uriHandler.openUri("https://play.google.com/store/apps/details?id=$packageName")
                    }
                }
                
                SettingsLinkItem("Support & Feedback", Icons.Filled.Email) {
                    val intent = android.content.Intent(android.content.Intent.ACTION_SENDTO).apply {
                        data = android.net.Uri.parse("mailto:support@echologic.app")
                        putExtra(android.content.Intent.EXTRA_SUBJECT, "EchoLogic Feedback")
                    }
                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "No email app found", Toast.LENGTH_SHORT).show()
                    }
                }

                SettingsLinkItem("Share EchoLogic", Icons.Filled.Share) {
                    val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        val packageName = context.packageName
                        val text = "Get smarter with EchoLogic! Download it here: https://play.google.com/store/apps/details?id=$packageName"
                        putExtra(android.content.Intent.EXTRA_TEXT, text)
                    }
                    context.startActivity(android.content.Intent.createChooser(shareIntent, "Share with"))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Privacy Policy",
                style = MaterialTheme.typography.bodySmall.copy(
                    textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline
                ),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { 
                    uriHandler.openUri("https://echologic.app/privacy") 
                }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Terms of Service",
                style = MaterialTheme.typography.bodySmall.copy(
                    textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline
                ),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { 
                    uriHandler.openUri("https://echologic.app/terms") 
                }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // ── Account ──
        var showDeleteDialog by remember { mutableStateOf(false) }
        
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete Account?") },
                text = { Text("This will permanently delete your account and all saved favorites. This action cannot be undone.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteAccount { success ->
                                if (success) {
                                    Toast.makeText(context, "Account Deleted", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Error deleting account", Toast.LENGTH_SHORT).show()
                                }
                            }
                            showDeleteDialog = false
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("DELETE PERMANENTLY")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("CANCEL")
                    }
                },
                shape = MaterialTheme.shapes.small,
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.onSurface,
                textContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        SettingsSection(title = "ACCOUNT") {
            Button(
                onClick = { viewModel.signOut() },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.small,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
            ) {
                Text(
                    text = "SIGN OUT",
                    style = MaterialTheme.typography.labelLarge.copy(
                        letterSpacing = 2.sp
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            TextButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error.copy(alpha = 0.7f))
            ) {
                Text(
                    text = "DELETE ACCOUNT",
                    style = MaterialTheme.typography.labelSmall.copy(
                        textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // ── Account Info ──
        val user by viewModel.currentUser.collectAsState()
        
        user?.let { firebaseUser ->
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "SIGNED IN AS",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = firebaseUser.displayName ?: firebaseUser.email ?: "Anonymous User",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                if (firebaseUser.email != null && firebaseUser.displayName != null) {
                    Text(
                        text = firebaseUser.email!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun CategoryItem(
    name: String,
    isSelected: Boolean,
    isEnabled: Boolean = true,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface)
            .border(
                width = 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                shape = MaterialTheme.shapes.small
            )
            .clickable(enabled = isEnabled) { onToggle() }
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Checkbox(
            checked = isSelected,
            onCheckedChange = { onToggle() },
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary,
                uncheckedColor = MaterialTheme.colorScheme.outline,
                checkmarkColor = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier.size(24.dp)
        )
    }
}



@Composable
fun SettingsSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Surface(
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                    shape = MaterialTheme.shapes.small
                )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}
@Composable
fun SettingsLinkItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SettingsToggleItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isChecked) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.weight(1f))
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
    }
}
