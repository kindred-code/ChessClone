package com.mpolitakis.vodafonechess

import androidx.compose.runtime.Composable
import com.mpolitakis.vodafonechess.ui.Board
import javax.inject.Inject




class DefaultBoardFactoryImpl @Inject constructor() : DefaultBoardFactory {

    override fun create(boardSize: Int): @Composable () -> Unit {
        return {
            Board()
        }
    }
}