package com.mpolitakis.vodafonechess

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class Move(val x: Int, val y: Int)
@HiltViewModel
class BoardViewModel @Inject constructor() : ViewModel() {

    private val _boardSize = MutableStateFlow(DEFAULT_BOARD_SIZE)
    val boardSize: MutableStateFlow<Int> = _boardSize

    private val _board = MutableStateFlow(createEmptyBoard(DEFAULT_BOARD_SIZE))
    val board: StateFlow<List<List<Cell>>> = _board.asStateFlow()

    private val _startingChoice = MutableStateFlow<Cell?>(null)
    val startingChoice: StateFlow<Cell?> = _startingChoice

    private val _endingChoice = MutableStateFlow<Cell?>(null)
    val endingChoice: StateFlow<Cell?> = _endingChoice

    private val _successfulPaths = MutableStateFlow<List<List<Cell>>>(emptyList())
    val successfulPaths: StateFlow<List<List<Cell>>> = _successfulPaths

    private val _markedCell = MutableStateFlow<Cell?>(null)
    val markedCell: StateFlow<Cell?> = _markedCell

    companion object {
        private const val DEFAULT_BOARD_SIZE = 8
    }

    fun setBoardSize(size: Int) {
        _boardSize.tryEmit(size)
        _board.tryEmit(createEmptyBoard(size))
        _startingChoice.tryEmit(null)
        _endingChoice.tryEmit(null)
        _successfulPaths.tryEmit(emptyList())
        _markedCell.tryEmit(null)
    }

    private fun generateBoard(size: Int): List<List<Cell>> {
        val board = mutableListOf<List<Cell>>()
        for (row in 0 until size) {
            val rowList = mutableListOf<Cell>()
            for (col in 0 until size) {
                val cell = Cell(row, col, getColorForCell(row, col))
                rowList.add(cell)
            }
            board.add(rowList)
        }
        return board
    }

    fun findKnightTour(startingChoice: Cell, endingChoice: Cell? = null) {
        val boardSize = _boardSize.value
        val board = createEmptyBoard(boardSize)
        board[startingChoice.x][startingChoice.y].isStarting = true
        endingChoice?.let {
            board[it.x][it.y].isEnding = true
        }
        _board.tryEmit(board)

        viewModelScope.launch {
            val paths = mutableListOf<List<Cell>>()
            knightTourPath(startingChoice, endingChoice, mutableSetOf(), paths)
            if (paths.isNotEmpty()) {
                _successfulPaths.tryEmit(paths)
                val path = paths[0]
                for (i in path.indices) {
                    val cell = path[i]
                    board[cell.x][cell.y].index = i + 1
                }
                _board.tryEmit(board)
                _markedCell.tryEmit(path.last())
            }
        }
    }

    fun clearBoard() {
        val boardSize = _boardSize.value
        _board.tryEmit(createEmptyBoard(boardSize))
        _startingChoice.tryEmit(null)
        _endingChoice.tryEmit(null)
        _successfulPaths.tryEmit(emptyList())
        _markedCell.tryEmit(null)
    }

    init {
        resetBoard(_boardSize.value)
    }

    private fun knightTourPath(
        currentCell: Cell,
        endCell: Cell?,
        visited: MutableSet<Cell>,
        paths: MutableList<List<Cell>>
    ) {
        visited.add(currentCell)

        if (currentCell == endCell) {
            paths.add(visited.toList())
            visited.remove(currentCell)
            return
        }

        val moves = getValidMoves(currentCell)
        for (move in moves) {
            val nextCell = getCell(move.x, move.y)
            if (nextCell != null && nextCell !in visited) {
                knightTourPath(nextCell, endCell, visited, paths)
            }
        }

        visited.remove(currentCell)
    }

    private fun getValidMoves(cell: Cell): List<Cell> {
        val moves = mutableListOf<Cell>()
        val possibleMoves = listOf(
            Move(2, 1), Move(1, 2), Move(-1, 2), Move(-2, 1),
            Move(-2, -1), Move(-1, -2), Move(1, -2), Move(2, -1)
        )

        for (move in possibleMoves) {
            val newRow = cell.x + move.x
            val newCol = cell.y + move.y
            val newCell = Cell(newRow, newCol)
            moves.add(newCell)
        }

        return moves
    }

    private fun resetBoard(size: Int) {
        Log.d("BoardViewModel", "Resetting board with size $size")
        val startCell = _startingChoice.value
        val endCell = _endingChoice.value
        val successfulCells = _successfulPaths.value.flatten().toSet()

        val newBoard = generateBoard(size).map { rowList ->
            rowList.map { cell ->
                val color = when {
                    cell == startCell -> Color.Yellow
                    cell == endCell -> Color.Blue
                    cell in successfulCells -> Color.Green
                    else -> getColorForCell(cell.x, cell.y)
                }
                val step = if (cell in successfulCells) {
                    _successfulPaths.value.indexOfFirst { path -> cell in path } + 1
                } else {
                    0
                }


                cell.copy(color = color, step = step)
            }
        }

        _board.tryEmit(newBoard)
        for (row in newBoard) {
            val rowStr = row.joinToString { it.toString() }
            Log.d("BoardViewModel", rowStr)
        }
    }

    private fun getColorForCell(x: Int, y: Int): Color {
        return if (x < 0 || x >= _boardSize.value || y < 0 || y >= _boardSize.value) {
            Color.Transparent // or any other default color
        } else if ((x + y) % 2 == 0) {
            if (x % 2 == 0) {
                Color.White
            } else {
                Color.LightGray
            }
        } else {
            if (x % 2 == 0) {
                Color.LightGray
            } else {
                Color.White
            }
        }
    }

    private fun getCell(x: Int, y: Int): Cell? {
        return _board.value.getOrNull(x)?.getOrNull(y)
    }

    fun setStartingChoice(cell: Cell) {
        _startingChoice.tryEmit(cell)
        _endingChoice.tryEmit(null)
        _successfulPaths.tryEmit(emptyList())
        _markedCell.tryEmit(null)
        resetBoard(_boardSize.value)
    }

    fun setEndingChoice(cell: Cell) {
        _startingChoice.tryEmit(null)
        _endingChoice.tryEmit(cell)
        _successfulPaths.tryEmit(emptyList())
        _markedCell.tryEmit(null)
        resetBoard(_boardSize.value)
    }

    fun setBoardCell(cell: Cell) {
        val currentCell = _markedCell.value
        if (currentCell == null) {
            setStartingChoice(cell)
        } else {
            if (cell == currentCell) {
                setStartingChoice(cell)
            } else {
                setEndingChoice(cell)
            }
        }
    }

    fun getSuccessfulPaths(): List<List<Cell>> = _successfulPaths.value

    private fun createEmptyBoard(size: Int): List<List<Cell>> {
        return List(size) { row ->
            List(size) { col ->
                Cell(row, col)
            }
        }
    }
}

