package com.mpolitakis.vodafonechess

import androidx.compose.ui.graphics.Color
import com.mpolitakis.vodafonechess.ui.theme.darkSquare
import com.mpolitakis.vodafonechess.ui.theme.lightSquare

data class Cell(var x: Int, var y: Int , var color: Color = Color.White)
