package com.example.todoapp.widget

import android.content.Context
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.example.todoapp.MainActivity
import com.example.todoapp.R
import com.example.todoapp.data.local.entity.OccurrenceStatus
import com.example.todoapp.data.preferences.AppThemeMode
import com.example.todoapp.data.preferences.UserPreferencesRepository
import com.example.todoapp.data.repository.TaskRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class TodayWidget : GlanceAppWidget() {

    companion object {
        suspend fun updateAllWidgets(context: Context) {
            try {
                TodayWidget().updateAll(context)
            } catch (e: Exception) {
                android.util.Log.e("TodayWidget", "Failed to update all widgets", e)
            }
        }
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repo = TaskRepository.getInstance(context)
        val prefs = UserPreferencesRepository(context)
        val today = LocalDate.now()

        // Sync occurrences in case a new day started or fresh install
        repo.seedDefaultTrainingScheduleIfNotExists(today)
        repo.reconcileAllActiveTasks(today)

        provideContent {
            val occurrences by repo.observeTodayOccurrences(today).collectAsState(initial = emptyList())
            val showCompleted by prefs.widgetShowCompleted.collectAsState(initial = true)
            val themePref by prefs.themeMode.collectAsState(initial = AppThemeMode.SYSTEM)

            val displayList = if (showCompleted) {
                occurrences
            } else {
                occurrences.filter { it.occurrence.status == OccurrenceStatus.PENDING }
            }

            val completedCount = occurrences.count { it.occurrence.status == OccurrenceStatus.COMPLETED }
            val totalCount = occurrences.size

            // High-contrast Pure Black (#000000) for Dark Mode & Pure White (#FFFFFF) for Light Mode
            val widgetBgColor = when (themePref) {
                AppThemeMode.DARK -> ColorProvider(day = Color(0xFF000000), night = Color(0xFF000000))
                AppThemeMode.LIGHT -> ColorProvider(day = Color(0xFFFFFFFF), night = Color(0xFFFFFFFF))
                AppThemeMode.SYSTEM -> ColorProvider(day = Color(0xFFFFFFFF), night = Color(0xFF000000))
            }

            val widgetOnSurface = when (themePref) {
                AppThemeMode.DARK -> ColorProvider(day = Color(0xFFFFFFFF), night = Color(0xFFFFFFFF))
                AppThemeMode.LIGHT -> ColorProvider(day = Color(0xFF0F172A), night = Color(0xFF0F172A))
                AppThemeMode.SYSTEM -> ColorProvider(day = Color(0xFF0F172A), night = Color(0xFFFFFFFF))
            }

            val widgetOnSurfaceVariant = when (themePref) {
                AppThemeMode.DARK -> ColorProvider(day = Color(0xFFA1A1AA), night = Color(0xFFA1A1AA))
                AppThemeMode.LIGHT -> ColorProvider(day = Color(0xFF64748B), night = Color(0xFF64748B))
                AppThemeMode.SYSTEM -> ColorProvider(day = Color(0xFF64748B), night = Color(0xFFA1A1AA))
            }

            val widgetPrimary = when (themePref) {
                AppThemeMode.DARK -> ColorProvider(day = Color(0xFFA5B4FC), night = Color(0xFFA5B4FC))
                AppThemeMode.LIGHT -> ColorProvider(day = Color(0xFF3525CD), night = Color(0xFF3525CD))
                AppThemeMode.SYSTEM -> ColorProvider(day = Color(0xFF3525CD), night = Color(0xFFA5B4FC))
            }

            val widgetOnPrimary = when (themePref) {
                AppThemeMode.DARK -> ColorProvider(day = Color(0xFF1E1B4B), night = Color(0xFF1E1B4B))
                AppThemeMode.LIGHT -> ColorProvider(day = Color(0xFFFFFFFF), night = Color(0xFFFFFFFF))
                AppThemeMode.SYSTEM -> ColorProvider(day = Color(0xFFFFFFFF), night = Color(0xFF1E1B4B))
            }

            val widgetCheckboxPendingBg = when (themePref) {
                AppThemeMode.DARK -> ColorProvider(day = Color(0xFF18181B), night = Color(0xFF18181B))
                AppThemeMode.LIGHT -> ColorProvider(day = Color(0xFFF1F5F9), night = Color(0xFFF1F5F9))
                AppThemeMode.SYSTEM -> ColorProvider(day = Color(0xFFF1F5F9), night = Color(0xFF18181B))
            }

            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(widgetBgColor)
                    .padding(12.dp)
            ) {
                // Header Row
                val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                val formattedDate = today.format(formatter)

                Row(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .clickable(actionStartActivity<MainActivity>()),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = GlanceModifier.defaultWeight()) {
                        Text(
                            text = context.getString(R.string.widget_title),
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = widgetOnSurface
                            )
                        )
                        Text(
                            text = formattedDate,
                            style = TextStyle(
                                fontSize = 12.sp,
                                color = widgetOnSurfaceVariant
                            )
                        )
                    }

                    if (totalCount > 0) {
                        Text(
                            text = "$completedCount / $totalCount",
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = widgetPrimary
                            )
                        )
                    }
                }

                Spacer(modifier = GlanceModifier.height(8.dp))

                if (displayList.isEmpty()) {
                    Box(
                        modifier = GlanceModifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (totalCount > 0) context.getString(R.string.widget_empty) else context.getString(R.string.today_empty),
                                style = TextStyle(
                                    fontSize = 13.sp,
                                    color = widgetOnSurfaceVariant
                                )
                            )
                            Spacer(modifier = GlanceModifier.height(6.dp))
                            Text(
                                text = context.getString(R.string.widget_open_app),
                                modifier = GlanceModifier.clickable(actionStartActivity<MainActivity>()),
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = widgetPrimary
                                )
                            )
                        }
                    }
                } else {
                    LazyColumn(modifier = GlanceModifier.fillMaxSize()) {
                        items(items = displayList, itemId = { it.occurrence.id }) { item ->
                            val isCompleted = item.occurrence.status == OccurrenceStatus.COMPLETED

                            Row(
                                modifier = GlanceModifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Custom checkbox button with dedicated 44.dp touch target in Glance
                                Box(
                                    modifier = GlanceModifier
                                        .size(44.dp)
                                        .clickable(
                                            actionRunCallback<ToggleTaskActionCallback>(
                                                actionParametersOf(
                                                    ToggleTaskActionCallback.KEY_OCCURRENCE_ID to item.occurrence.id,
                                                    ToggleTaskActionCallback.KEY_OCCURRENCE_ID_STR to item.occurrence.id.toString(),
                                                    ToggleTaskActionCallback.KEY_NEW_STATUS to !isCompleted
                                                )
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = GlanceModifier
                                            .size(22.dp)
                                            .background(
                                                if (isCompleted) widgetPrimary
                                                else widgetCheckboxPendingBg
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (isCompleted) "✓" else "",
                                            style = TextStyle(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = if (isCompleted) widgetOnPrimary else widgetOnSurfaceVariant
                                            )
                                        )
                                    }
                                }

                                Spacer(modifier = GlanceModifier.width(4.dp))

                                Text(
                                    text = item.task.title,
                                    modifier = GlanceModifier
                                        .defaultWeight()
                                        .clickable(actionStartActivity<MainActivity>()),
                                    style = TextStyle(
                                        fontSize = 13.sp,
                                        fontWeight = if (isCompleted) FontWeight.Normal else FontWeight.Medium,
                                        color = if (isCompleted) widgetOnSurfaceVariant else widgetOnSurface
                                    ),
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

class ToggleTaskActionCallback : ActionCallback {
    companion object {
        val KEY_OCCURRENCE_ID = ActionParameters.Key<Long>("occurrence_id")
        val KEY_OCCURRENCE_ID_STR = ActionParameters.Key<String>("occurrence_id_str")
        val KEY_NEW_STATUS = ActionParameters.Key<Boolean>("new_status")
    }

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val occurrenceId = parameters[KEY_OCCURRENCE_ID]
            ?: parameters[KEY_OCCURRENCE_ID_STR]?.toLongOrNull()
            ?: return
        val newStatus = parameters[KEY_NEW_STATUS] ?: return

        try {
            val repo = TaskRepository.getInstance(context)
            repo.toggleOccurrenceStatus(occurrenceId, newStatus)
            TodayWidget().update(context, glanceId)
            TodayWidget.updateAllWidgets(context)
        } catch (e: Exception) {
            android.util.Log.e("TodayWidget", "Failed to update occurrence status in widget", e)
        }
    }
}

class TodayWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = TodayWidget()
}
