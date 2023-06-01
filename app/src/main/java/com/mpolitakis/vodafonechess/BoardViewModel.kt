package com.mpolitakis.vodafonechess

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class Move(val x: Int, val y: Int)
@HiltViewModel

class BoardViewModel @Inject constructor() : ViewModel() {

    private val _boardSize = MutableStateFlow(DEFAULT_BOARD_SIZE)
    val boardSize: StateFlow<Int> = _boardSize

    private val _startingChoice = MutableStateFlow<Cell?>(null)
    val startingChoice: StateFlow<Cell?> = _startingChoice

    private val _endingChoice = MutableStateFlow<Cell?>(null)
    val endingChoice: StateFlow<Cell?> = _endingChoice

    private val _successfulPaths = MutableStateFlow<List<List<Cell>>>(emptyList())
    val successfulPaths: StateFlow<List<List<Cell>>> = _successfulPaths

    private val _markedCell = MutableStateFlow<Cell?>(null)
    val markedCell: StateFlow<Cell?> = _markedCell

    private val _board = MutableStateFlow(
        createEmptyBoard(
            boardSize.value,
            startingChoice.value,
            endingChoice.value
        )
    )
    val board: StateFlow<List<Cell>> = _board.asStateFlow()

    companion object {
        private const val DEFAULT_BOARD_SIZE = 8
    }

    fun setBoardSize(size: Int) {
        _boardSize.tryEmit(size)
        _board.tryEmit(createEmptyBoard(size, startingChoice.value, endingChoice.value))
        _startingChoice.tryEmit(null)
        _endingChoice.tryEmit(null)
        _successfulPaths.tryEmit(emptyList())
        _markedCell.tryEmit(null)
    }

    private fun generateBoard(size: Int, startingChoice: Cell?, endingChoice: Cell?): List<Cell> {
        return List(size * size) { index ->
            val row = index / size
            val col = index % size
            val cell = Cell(row, col, step = 0)

            if (cell == startingChoice) {
                cell.isStarting = true
            }

            if (cell == endingChoice) {
                cell.isEnding = true
            }

            cell
        }
    }



    suspend fun setStartingChoice(cell: Cell) {
        _startingChoice.tryEmit(cell)
        _endingChoice.tryEmit(null)
        _successfulPaths.tryEmit(emptyList())
        _markedCell.tryEmit(null)

        val boardSize = _boardSize.value
        val updatedBoard = generateBoard(boardSize, cell, _endingChoice.value)
        _board.emit(updatedBoard)
    }

    suspend fun setEndingChoice(cell: Cell) {
        val startingChoice = _startingChoice.value

        if (cell != startingChoice) {
            _endingChoice.tryEmit(cell)
            _successfulPaths.tryEmit(emptyList())
            _markedCell.tryEmit(null)

            val boardSize = _boardSize.value
            val updatedBoard = generateBoard(boardSize, _startingChoice.value, cell)
            _board.emit(updatedBoard)
        } else {
            // If the chosen ending cell is the same as the starting cell, clear the ending choice
            _endingChoice.tryEmit(null)
            val boardSize = _boardSize.value
            val updatedBoard = generateBoard(boardSize, _startingChoice.value, null)
            _board.emit(updatedBoard)
        }
    }

    fun getColorForCell(row: Int, col: Int): Color {
        return if ((row + col) % 2 == 0) {
            Color.White
        } else {
            Color.LightGray
        }
    }


    suspend fun findKnightTour(startingChoice: Cell, endingChoice: Cell): Boolean {
        _successfulPaths.tryEmit(emptyList())
        _markedCell.tryEmit(null)

        val boardSize = _boardSize.value
        val board = generateBoard(boardSize, startingChoice, endingChoice)

        val knightTour = KnightTour(boardSize)
        val paths = knightTour.findPaths(
            startingChoice.x,
            startingChoice.y,
            endingChoice.x,
            endingChoice.y
        )
        _successfulPaths.emit(paths)

        val pathsFound = paths.isNotEmpty()
        if (pathsFound) {
            val path = paths.first()
            val markedCell = board[path.last().x * boardSize + path.last().y]
            _markedCell.tryEmit(markedCell)
        } else {
            _markedCell.tryEmit(null)
        }

        return pathsFound
    }


    fun clearBoard() {
        viewModelScope.launch {
            _startingChoice.tryEmit(null)
            _endingChoice.tryEmit(null)
            _successfulPaths.tryEmit(emptyList())
            _markedCell.tryEmit(null)

            val boardSize = _boardSize.value
            _board.emit(generateBoard(boardSize, null, null))
        }
    }

    private fun createEmptyBoard(
        size: Int,
        startingChoice: Cell?,
        endingChoice: Cell?
    ): List<Cell> {
        return List(size * size) { index ->
            val row = index / size
            val col = index % size
            val color = getColorForCell(row, col) // Get the color for the cell

            val cell = Cell(row, col, step = 0) // Set the initial step value

            if (cell == startingChoice) {
                cell.isStarting = true
            }

            if (cell == endingChoice) {
                cell.isEnding = true
            }

            cell
        }
    }
    fun setMarkedCell(cell: Cell) {
        _markedCell.value = cell
    }




}


