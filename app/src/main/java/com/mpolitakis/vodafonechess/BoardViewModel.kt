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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class Move(val x: Int, val y: Int)
@HiltViewModel
class BoardViewModel @Inject constructor() : ViewModel() {

    private val _startingChoice: MutableStateFlow<Cell?> = MutableStateFlow(null)
    val startingChoice: StateFlow<Cell?> get() = _startingChoice

    private val _endingChoice: MutableStateFlow<Cell?> = MutableStateFlow(null)
    val endingChoice: StateFlow<Cell?> get() = _endingChoice

    private val visitedCells = mutableSetOf<Cell>()
    private val _successfulPaths = MutableStateFlow<List<Cell>>(emptyList())
    val successfulPaths: StateFlow<List<Cell>> get() = _successfulPaths

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _board = MutableStateFlow<List<List<Cell>>>(emptyList())
    val board: StateFlow<List<List<Cell>>> get() = _board

    private val _boardSize = MutableStateFlow(8)
    val boardSize: StateFlow<Int> get() = _boardSize

    private var pathFound = false
    private var stepCount = 0
    private var pathLetterIndex = 0
    private var searchJob: Job? = null

    init {
        resetBoard(_boardSize.value)
    }

    fun setBoardSize(size: Int) {
        _boardSize.value = size
        resetBoard(size)
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

    fun findKnightTour(start: Cell, end: Cell, size: Int) {
        _isLoading.value = true

        searchJob?.cancel()
        searchJob = viewModelScope.launch(Dispatchers.Default) {
            try {
                pathFound = false
                visitedCells.clear()
                _successfulPaths.value = emptyList()
                val paths = mutableListOf<Cell>()
                knightTourPath(start, end, visitedCells, paths)
                if (pathFound) {
                    withContext(Dispatchers.Main) {
                        Log.d("BoardViewModel", "Paths Found: ${paths.size}")
                        _startingChoice.value = start
                        _endingChoice.value = end
                        _successfulPaths.value = paths
                        resetBoard(size)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Log.d("BoardViewModel", "Paths not found")
                    }
                }
            } finally {
                withContext(Dispatchers.Main) {
                    _isLoading.value = false
                }
            }
        }
    }

    init {
        resetBoard(_boardSize.value)
    }

    private fun knightTourPath(
        currentCell: Cell,
        endCell: Cell,
        visited: MutableSet<Cell>,
        paths: MutableList<Cell>
    ) {
        visited.add(currentCell)
        stepCount++

        if (currentCell.x == endCell.x && currentCell.y == endCell.y) {
            pathFound = true
            visited.remove(currentCell)
            return
        }

        val moves = getValidMoves(currentCell)
        for (move in moves) {
            val nextCell = getCell(move.x, move.y)
            if (nextCell != null && nextCell !in visited) {
                knightTourPath(nextCell, endCell, visited, paths)
                if (pathFound) {
                    val newPath = paths.toMutableList()
                    newPath.add(nextCell)
                    newPath.forEach() {
                        paths.add(it)
                    }
                }
            }
        }

        visited.remove(currentCell)
        stepCount--
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
        val successfulCells = _successfulPaths.value.toSet()

        val newBoard = generateBoard(size).map { rowList ->
            rowList.map { cell ->
                val color = when {
                    cell == startCell -> Color.Yellow
                    cell == endCell -> Color.Blue
                    cell in successfulCells -> Color.Green
                    else -> getColorForCell(cell.x, cell.y)
                }
                val step = if (cell in successfulCells) (_successfulPaths.value.indexOfFirst { it == cell } + 1) else 0
                cell.copy(color = color, step = step)
            }
        }

        _board.value = newBoard
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
        val currentStarting = _startingChoice.value
        _board.value = _board.value.map { rowList ->
            rowList.map { it ->
                if (it == cell) {
                    it.copy(color = Color.Yellow)
                } else if (it == currentStarting) {
                    it.copy(color = getColorForCell(it.x, it.y))
                } else {
                    it
                }
            }
        }
        _startingChoice.value = cell
    }

    fun setEndingChoice(cell: Cell) {
        val currentEnding = _endingChoice.value
        _board.value = _board.value.map { rowList ->
            rowList.map { it ->
                if (it == cell) {
                    it.copy(color = Color.Blue)
                } else if (it == currentEnding) {
                    it.copy(color = getColorForCell(it.x, it.y))
                } else {
                    it
                }
            }
        }
        _endingChoice.value = cell
    }

    fun clearPaths() {
        _successfulPaths.value = emptyList()
        _isLoading.value = false
    }
}

