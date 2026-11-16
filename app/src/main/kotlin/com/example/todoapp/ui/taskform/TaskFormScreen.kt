package com.example.todoapp.ui.taskform

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CalendarViewWeek
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.EventRepeat
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todoapp.R
import com.example.todoapp.domain.model.ScheduleType
import com.example.todoapp.ui.components.CategoryIconHelper
import com.example.todoapp.ui.components.RutinaAppHeader
import com.example.todoapp.ui.components.parseColor
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val SWATCH_COLORS = listOf(
    "#3525CD", // Indigo
    "#006A7C", // Emerald/Teal
    "#FEA619", // Amber
    "#BA1A1A", // Rose
    "#00505F", // Cyan
    "#A5B4FC"  // Violet
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TaskFormScreen(
    taskId: Long?,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TaskFormViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(taskId) {
        viewModel.loadTask(taskId)
    }

    val selectedColor = parseColor(uiState.colorHex) ?: MaterialTheme.colorScheme.primary
    val selectedIconVector = CategoryIconHelper.getIcon(uiState.selectedIcon)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            RutinaAppHeader(
                title = stringResource(if (taskId != null && taskId > 0) R.string.edit_task else R.string.add_task),
                onBack = onNavigateBack,
                showBrand = false
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Button(
                    onClick = { viewModel.saveTask(onSuccess = onNavigateBack) },
                    enabled = !uiState.isSaving && uiState.title.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(9999.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = stringResource(R.string.save_routine),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Offline-first sync status badge
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudDone,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Tersimpan lokal • Sinkronisasi otomatis",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "Draf Siap",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Title & Selected Icon Preview Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.task_title),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = uiState.title,
                            onValueChange = { viewModel.onTitleChanged(it) },
                            placeholder = { Text(stringResource(R.string.task_title_hint)) },
                            isError = uiState.titleError != null,
                            supportingText = uiState.titleError?.let { { Text(it) } },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            )
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        // Selected Icon Preview badge
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(selectedColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = selectedIconVector,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = stringResource(R.string.task_notes),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = uiState.notes,
                        onValueChange = { viewModel.onNotesChanged(it) },
                        placeholder = { Text(stringResource(R.string.task_notes_hint)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        minLines = 2,
                        maxLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }
            }

            // Visual Identity: Icon & Color Selector
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = stringResource(R.string.icon_palette_title),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Icon selector row
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(CategoryIconHelper.ICONS, key = { it.key }) { item ->
                            val isSelected = uiState.selectedIcon == item.key
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.surfaceContainerHigh
                                    )
                                    .clickable { viewModel.onIconChanged(item.key) },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label,
                                    tint = if (isSelected) MaterialTheme.colorScheme.onPrimary
                                    else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    // Color swatches row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SWATCH_COLORS.forEach { hex ->
                            val isSelected = uiState.colorHex.equals(hex, ignoreCase = true)
                            val swatchColor = parseColor(hex) ?: Color.Gray

                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(swatchColor)
                                    .clickable { viewModel.onColorChanged(hex) },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Recurrence Type Selector
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.schedule_type),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Jadwal Dinamis",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ScheduleType.entries) { type ->
                        val label = when (type) {
                            ScheduleType.ONCE -> stringResource(R.string.schedule_once)
                            ScheduleType.DAILY -> stringResource(R.string.schedule_daily)
                            ScheduleType.WEEKLY -> stringResource(R.string.schedule_weekly)
                            ScheduleType.MONTHLY -> stringResource(R.string.schedule_monthly)
                            ScheduleType.INTERVAL -> stringResource(R.string.schedule_interval)
                            ScheduleType.SELECTED_DATES -> stringResource(R.string.schedule_selected_dates)
                        }
                        val isSelected = uiState.scheduleType == type

                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.onScheduleTypeChanged(type) },
                            label = { Text(label) },
                            shape = RoundedCornerShape(9999.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        )
                    }
                }
            }

            // Dynamic Schedule Configuration Box
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    when (uiState.scheduleType) {
                        ScheduleType.ONCE -> {
                            Text(
                                text = "Tanggal Pelaksanaan",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = uiState.onceDate.format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy")),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                Button(
                                    onClick = {
                                        viewModel.onOnceDateChanged(uiState.onceDate.plusDays(1))
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
                                ) {
                                    Text("+1 Hari", color = MaterialTheme.colorScheme.onSurface)
                                }
                            }
                        }

                        ScheduleType.DAILY -> {
                            Text(
                                text = "Setiap ${uiState.dailyInterval} Hari",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Slider(
                                value = uiState.dailyInterval.toFloat(),
                                onValueChange = { viewModel.onDailyIntervalChanged(it.toInt()) },
                                valueRange = 1f..14f,
                                steps = 12
                            )
                        }

                        ScheduleType.WEEKLY -> {
                            Text(
                                text = "Hari Pelaksanaan",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                val days = listOf(
                                    DayOfWeek.MONDAY to "S",
                                    DayOfWeek.TUESDAY to "S",
                                    DayOfWeek.WEDNESDAY to "R",
                                    DayOfWeek.THURSDAY to "K",
                                    DayOfWeek.FRIDAY to "J",
                                    DayOfWeek.SATURDAY to "S",
                                    DayOfWeek.SUNDAY to "M"
                                )
                                days.forEach { (day, label) ->
                                    val isSelected = uiState.weeklyDays.contains(day)
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isSelected) MaterialTheme.colorScheme.primary
                                                else MaterialTheme.colorScheme.surfaceContainerHigh
                                            )
                                            .clickable { viewModel.onWeeklyDayToggled(day) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                                            else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        ScheduleType.MONTHLY -> {
                            Text(
                                text = "Tanggal ${uiState.monthlyDay} Setiap Bulan",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Slider(
                                value = uiState.monthlyDay.toFloat(),
                                onValueChange = { viewModel.onMonthlyDayChanged(it.toInt()) },
                                valueRange = 1f..31f,
                                steps = 29
                            )
                        }

                        ScheduleType.INTERVAL -> {
                            Text(
                                text = "Setiap ${uiState.intervalDays} Hari",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Slider(
                                value = uiState.intervalDays.toFloat(),
                                onValueChange = { viewModel.onIntervalDaysChanged(it.toInt()) },
                                valueRange = 2f..30f,
                                steps = 27
                            )
                        }

                        ScheduleType.SELECTED_DATES -> {
                            Text(
                                text = "${uiState.selectedDates.size} Tanggal Terpilih",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Button(
                                onClick = {
                                    viewModel.onSelectedDateAdded(LocalDate.now().plusDays((uiState.selectedDates.size + 1).toLong()))
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
                            ) {
                                Text("+ Tambah Tanggal Contoh", color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }

                    // Optional End Date Switch
                    if (uiState.scheduleType != ScheduleType.ONCE) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.field_has_end_date),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Switch(
                                checked = uiState.hasEndDate,
                                onCheckedChange = { viewModel.onHasEndDateChanged(it) }
                            )
                        }
                    }
                }
            }

            // Live Occurrence Preview Dates
            if (uiState.previewDates.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.EventRepeat,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = stringResource(R.string.preview_next_occurrences),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val formatter = DateTimeFormatter.ofPattern("EEE, d MMM")
                            uiState.previewDates.forEach { date ->
                                Text(
                                    text = date.format(formatter),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSecondaryFixed,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(9999.dp))
                                        .background(MaterialTheme.colorScheme.secondaryContainer)
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(72.dp))
        }
    }
}
