package com.mpolitakis.vodafonechess

import android.os.Build.VERSION_CODES.N
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.Random
import java.util.concurrent.ThreadLocalRandom


class BoardViewModel : ViewModel() {

    private val _board : MutableList<Cell> = mutableStateListOf()
    val board : List<Cell> = _board
    var startingChoice : Cell? = null
    var endChoice : Cell? = null
    var boardSize : Int = 6

    fun knight(start: Cell, end: Cell, boardSize: Int,  moves: Int) {


        val prevVisited = mutableListOf<Cell>()

        val setX = listOf(2, 2, -2, -2, 1, 1, -1, -1)
        val setY = listOf(1, -1, 1, -1, 2, -2, 2, -2)

        fun isValid(x: Int, y: Int): Boolean {
            return (x in 0..boardSize && y in 0..boardSize)
        }

        fun rec(x: Int, y: Int, endPosition: Cell, moves: Int, prevVisited: MutableList<Cell>): Boolean {
            if (x == endPosition.x && y == endPosition.y) {
                return true
            }
            if (moves == 0) {
                return false
            }
            for (i in 0 until boardSize) {
                val nextX = x + setX[i]
                val nextY = y + setY[i]
                if (isValid(nextX, nextY) && !prevVisited.contains(Cell(nextX, nextY))) {
                    prevVisited.add(Cell(nextX, nextY))
                    if (rec(nextX, nextY, endPosition, moves - 1, prevVisited)) {
                        return true
                    }
                    prevVisited.removeAt(prevVisited.lastIndex)
                }
            }
            return false
        }

        if (rec(start.x, start.y, end, moves, prevVisited)) {
            updateBoard(prevVisited)
        } else {
            println("NO")
        }
    }

fun updateBoard(prevVisited: List<Cell>) {

    _board.forEachIndexed(){ index , cell ->
        for (cellP in prevVisited) {
            if (cell == cellP) {
                cell.color.green
                _board[index] = cell

            }
        }
    }
}
    fun createBoard(cell : Cell){
        _board.add(cell)
    }
}