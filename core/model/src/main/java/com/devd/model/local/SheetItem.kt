package com.devd.model.local

import androidx.compose.ui.graphics.Color

data class SheetItem(
    val itemIcon: Int? = null,
    val itemColor: Color = Color.Black,
    val id: String,
    val text: String,
    val isSelected: Boolean = false
)