package com.example.todoapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Cottage
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.vector.ImageVector

data class CategoryIconItem(
    val key: String,
    val label: String,
    val icon: ImageVector
)

object CategoryIconHelper {
    val ICONS = listOf(
        CategoryIconItem("fitness_center", "Kebugaran", Icons.Default.FitnessCenter),
        CategoryIconItem("work", "Pekerjaan", Icons.Default.Work),
        CategoryIconItem("cottage", "Rumah", Icons.Default.Cottage),
        CategoryIconItem("account_balance_wallet", "Keuangan", Icons.Default.AccountBalanceWallet),
        CategoryIconItem("ecg_heart", "Kesehatan", Icons.Default.Favorite),
        CategoryIconItem("menu_book", "Belajar", Icons.AutoMirrored.Filled.MenuBook),
        CategoryIconItem("checklist", "Tugas", Icons.Default.Checklist)
    )

    fun getIcon(key: String?): ImageVector {
        return ICONS.firstOrNull { it.key == key }?.icon ?: Icons.Default.Checklist
    }
}
