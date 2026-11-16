package com.example.todoapp.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EventRepeat
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.todoapp.data.local.entity.OccurrenceStatus
import com.example.todoapp.data.local.model.TaskWithOccurrence
import com.example.todoapp.domain.model.ScheduleRuleParser
import com.example.todoapp.domain.scheduler.RecurrenceCalculator

fun parseColor(colorHex: String?): Color? {
    if (colorHex.isNullOrBlank()) return null
    return try {
        val clean = colorHex.split(":").first().removePrefix("#")
        val colorInt = clean.toLong(16)
        if (clean.length == 6) {
            Color(colorInt or 0x00000000FF000000)
        } else {
            Color(colorInt)
        }
    } catch (_: Exception) {
        null
    }
}

fun parseIconKey(colorHex: String?): String {
    if (colorHex.isNullOrBlank()) return "checklist"
    val parts = colorHex.split(":")
    return if (parts.size > 1) parts[1] else "checklist"
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TaskOccurrenceItem(
    item: TaskWithOccurrence,
    onToggle: (Boolean) -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isCompleted = item.occurrence.status == OccurrenceStatus.COMPLETED
    val task = item.task
    val tagColor = parseColor(task.colorHex) ?: MaterialTheme.colorScheme.primary
    val iconKey = parseIconKey(task.colorHex)

    val scheduleSummary = remember(task.scheduleRuleJson) {
        try {
            val rule = ScheduleRuleParser.fromJson(task.scheduleType, task.scheduleRuleJson)
            RecurrenceCalculator.formatScheduleSummary(rule)
        } catch (_: Exception) {
            task.scheduleType.name
        }
    }

    val checkboxContainerColor by animateColorAsState(
        targetValue = if (isCompleted) {
            MaterialTheme.colorScheme.tertiaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceContainerHighest
        },
        label = "cbContainerColor"
    )

    val checkmarkScale by animateFloatAsState(
        targetValue = if (isCompleted) 1f else 0f,
        label = "cbCheckScale"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) {
                MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.75f)
            } else {
                MaterialTheme.colorScheme.surfaceContainer
            }
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = if (isCompleted) 0.45f else 0.85f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isCompleted) 0.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Interactive Checkbox
            Box(
                modifier = Modifier
                    .padding(top = 2.dp)
                    .size(26.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(checkboxContainerColor)
                    .border(
                        BorderStroke(
                            width = 1.dp,
                            color = if (isCompleted) Color.Transparent else MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { onToggle(!isCompleted) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = if (isCompleted) "Completed" else "Mark Complete",
                    tint = if (isCompleted) {
                        MaterialTheme.colorScheme.onTertiaryContainer
                    } else {
                        Color.Transparent
                    },
                    modifier = Modifier
                        .size(18.dp)
                        .scale(checkmarkScale)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 1.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isCompleted) {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        textDecoration = if (isCompleted) TextDecoration.LineThrough else null,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    // Tag dot
                    Box(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(tagColor)
                    )
                }

                if (!task.notes.isNullOrBlank()) {
                    Text(
                        text = task.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Recurrence Rule Pill
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(9999.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.EventRepeat,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = scheduleSummary,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Completed timestamp or category badge
                    if (isCompleted && item.occurrence.completedAt != null) {
                        Text(
                            text = "Selesai",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier
                                .clip(RoundedCornerShape(9999.dp))
                                .background(MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }
    }
}
