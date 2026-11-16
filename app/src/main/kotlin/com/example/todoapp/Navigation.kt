package com.example.todoapp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.todoapp.ui.alltasks.AllTasksScreen
import com.example.todoapp.ui.navigation.Screen
import com.example.todoapp.ui.settings.SettingsScreen
import com.example.todoapp.ui.taskdetail.TaskDetailScreen
import com.example.todoapp.ui.taskform.TaskFormScreen
import com.example.todoapp.ui.today.TodayScreen
import com.example.todoapp.ui.upcoming.UpcomingScreen

@Composable
fun MainNavigation() {
    val backStack = rememberNavBackStack(Screen.Today)
    val currentScreen = backStack.lastOrNull() ?: Screen.Today

    val isTopLevel = currentScreen is Screen.Today ||
            currentScreen is Screen.Upcoming ||
            currentScreen is Screen.AllTasks ||
            currentScreen is Screen.Settings

    val navItemColors = NavigationBarItemDefaults.colors(
        selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
        selectedTextColor = MaterialTheme.colorScheme.onSurface,
        indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (isTopLevel) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    NavigationBarItem(
                        selected = currentScreen is Screen.Today,
                        onClick = {
                            if (currentScreen !is Screen.Today) {
                                backStack.clear()
                                backStack.add(Screen.Today)
                            }
                        },
                        icon = { Icon(Icons.Default.EventAvailable, contentDescription = null) },
                        label = { Text(stringResource(R.string.nav_today), style = MaterialTheme.typography.labelMedium) },
                        colors = navItemColors
                    )
                    NavigationBarItem(
                        selected = currentScreen is Screen.Upcoming,
                        onClick = {
                            if (currentScreen !is Screen.Upcoming) {
                                backStack.clear()
                                backStack.add(Screen.Upcoming)
                            }
                        },
                        icon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
                        label = { Text(stringResource(R.string.nav_upcoming), style = MaterialTheme.typography.labelMedium) },
                        colors = navItemColors
                    )
                    NavigationBarItem(
                        selected = currentScreen is Screen.AllTasks,
                        onClick = {
                            if (currentScreen !is Screen.AllTasks) {
                                backStack.clear()
                                backStack.add(Screen.AllTasks)
                            }
                        },
                        icon = { Icon(Icons.Default.Checklist, contentDescription = null) },
                        label = { Text(stringResource(R.string.nav_all_tasks), style = MaterialTheme.typography.labelMedium) },
                        colors = navItemColors
                    )
                    NavigationBarItem(
                        selected = currentScreen is Screen.Settings,
                        onClick = {
                            if (currentScreen !is Screen.Settings) {
                                backStack.clear()
                                backStack.add(Screen.Settings)
                            }
                        },
                        icon = { Icon(Icons.Default.Widgets, contentDescription = null) },
                        label = { Text(stringResource(R.string.nav_widget_info), style = MaterialTheme.typography.labelMedium) },
                        colors = navItemColors
                    )
                }
            }
        }
    ) { paddingValues ->
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            modifier = Modifier.padding(paddingValues),
            entryProvider = entryProvider {
                entry<Screen.Today> {
                    TodayScreen(
                        onAddTask = { backStack.add(Screen.TaskForm()) },
                        onOpenTask = { taskId -> backStack.add(Screen.TaskDetail(taskId)) }
                    )
                }
                entry<Screen.Upcoming> {
                    UpcomingScreen(
                        onOpenTask = { taskId: Long -> backStack.add(Screen.TaskDetail(taskId)) }
                    )
                }
                entry<Screen.AllTasks> {
                    AllTasksScreen(
                        onOpenTask = { taskId -> backStack.add(Screen.TaskDetail(taskId)) }
                    )
                }
                entry<Screen.Settings> {
                    SettingsScreen()
                }
                entry<Screen.TaskForm> { key ->
                    TaskFormScreen(
                        taskId = key.taskId,
                        onNavigateBack = { backStack.removeLastOrNull() }
                    )
                }
                entry<Screen.TaskDetail> { key ->
                    TaskDetailScreen(
                        taskId = key.taskId,
                        onNavigateBack = { backStack.removeLastOrNull() },
                        onEditTask = { id -> backStack.add(Screen.TaskForm(id)) }
                    )
                }
            }
        )
    }
}
