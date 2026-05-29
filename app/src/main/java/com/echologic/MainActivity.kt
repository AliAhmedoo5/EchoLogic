package com.echologic

import android.os.Bundle
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.navigation.compose.rememberNavController
import com.echologic.ui.screens.AuthScreen
import com.echologic.ui.screens.LibraryScreen
import com.echologic.ui.screens.MainScreen
import com.echologic.ui.screens.SettingsScreen
import com.echologic.ui.theme.EchoLogicTheme
import dagger.hilt.android.AndroidEntryPoint

import androidx.compose.runtime.collectAsState
import com.echologic.data.repository.SettingsRepository
import com.echologic.data.repository.AuthRepository
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    @Inject
    lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkMode by settingsRepository.isDarkMode.collectAsState()
            val accentColor by settingsRepository.accentColor.collectAsState()
            val authState by authRepository.authState.collectAsState()
            
            LaunchedEffect(Unit) {
                Log.d("MainActivity", "First composition successful")
            }

            EchoLogicTheme(
                isDarkMode = isDarkMode,
                accentColor = Color(accentColor)
            ) {
                when (authState) {
                    is com.echologic.data.repository.AuthState.Loading -> {
                        // Show nothing (or a subtle splash) while Firebase checks cached auth
                        androidx.compose.foundation.layout.Box(
                            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
                        )
                    }
                    is com.echologic.data.repository.AuthState.Unauthenticated -> {
                        AuthScreen(authRepository = authRepository)
                    }
                    is com.echologic.data.repository.AuthState.Authenticated -> {
                        EchoLogicNavigation()
                    }
                }
            }
        }
    }
}

data class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val screens = listOf(
    Screen("home", "Feed", Icons.Filled.Home, Icons.Outlined.Home),
    Screen("library", "Library", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder),
    Screen("settings", "Settings", Icons.Filled.Settings, Icons.Outlined.Settings),
)

@Composable
fun EchoLogicNavigation() {
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                tonalElevation = 0.dp
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                screens.forEach { screen ->
                    val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true

                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (selected) screen.selectedIcon else screen.unselectedIcon,
                                contentDescription = screen.title
                            )
                        },
                        label = {
                            Text(
                                text = screen.title.uppercase(),
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                            unselectedTextColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            enterTransition = {
                fadeIn(animationSpec = tween(300, easing = FastOutSlowInEasing)) +
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(300, easing = FastOutSlowInEasing)) +
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                )
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(300, easing = FastOutSlowInEasing)) +
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                )
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(300, easing = FastOutSlowInEasing)) +
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                )
            }
        ) {
            composable("home") { MainScreen() }
            composable("library") { LibraryScreen() }
            composable("settings") { SettingsScreen() }
        }
    }
}
