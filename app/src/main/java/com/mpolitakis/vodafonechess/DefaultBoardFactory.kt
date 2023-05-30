package com.mpolitakis.vodafonechess

import androidx.compose.runtime.Composable

interface DefaultBoardFactory {
    fun create(boardSize: Int): @Composable () -> Unit
}