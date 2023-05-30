package com.mpolitakis.vodafonechess

import androidx.compose.ui.graphics.Color
import com.mpolitakis.vodafonechess.ui.theme.darkSquare
import com.mpolitakis.vodafonechess.ui.theme.lightSquare

data class Cell(
    val x: Int,
    val y: Int,
    val color: Color = Color.White,
    val step: Int = 0,
    val pathLetter: String = "",
    val isStarting: Boolean = false,
    val isEnding: Boolean = false
) {
    fun copy(
        color: Color = this.color,
        step: Int = this.step,
        pathLetter: String = this.pathLetter,
        isStarting: Boolean = this.isStarting,
        isEnding: Boolean = this.isEnding
    ): Cell {
        return Cell(x, y, color, step, pathLetter, isStarting, isEnding)
    }
}

