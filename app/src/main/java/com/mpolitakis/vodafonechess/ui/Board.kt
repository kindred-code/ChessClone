package com.mpolitakis.vodafonechess.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mpolitakis.vodafonechess.BoardViewModel
import com.mpolitakis.vodafonechess.Cell

@Composable
fun Board() {
    val boardViewModel: BoardViewModel = viewModel()
    val boardSize by boardViewModel.boardSize.collectAsState()
    val startingChoiceState by boardViewModel.startingChoice.collectAsState()
    val endingChoiceState by boardViewModel.endingChoice.collectAsState()
    val successfulPaths by boardViewModel.successfulPaths.collectAsState(initial = emptyList())

    val board by boardViewModel.board.collectAsState()
    val updatedBoardSize by rememberUpdatedState(boardSize)

    Column {
        for (row  in 0 until updatedBoardSize ) {
            Row {
                for (col in 0 until updatedBoardSize ) {
                    val cell = board[row][col]
                    Box(
                        Modifier
                            .sizeIn(10.dp, 20.dp)
                            .background(cell.color)
                            .clickable {
                                if (cell.isStarting) {
                                    boardViewModel.setStartingChoice(cell)
                                } else if (cell.isEnding) {
                                    boardViewModel.setEndingChoice(cell)
                                } else {
                                    boardViewModel.findKnightTour(
                                        boardViewModel.startingChoice.value ?: return@clickable,
                                        cell,
                                        boardSize
                                    )
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        val pathCount = successfulPaths.size
                        val labelText = when {
                            cell.isStarting -> "S"
                            cell.isEnding -> "E"
                            pathCount > 0 -> pathCount.toString()
                            else -> ""
                        }

                        Text(
                            text = labelText,
                            fontSize = 26.sp,
                            color = Color.Black
                        )

                        if (pathCount > 0) {
                            val successfulPath = successfulPaths.find { it == cell }
                            val pathIndex = successfulPaths.indexOf(successfulPath)
                            if (successfulPath != null) {
                                boardViewModel.findKnightTour(cell, successfulPath, pathIndex)
                            }
                        }
                    }
                }
            }
        }
        LaunchedEffect(updatedBoardSize) {
            boardViewModel.setBoardSize(updatedBoardSize)
        }

        Spacer(modifier = Modifier.size(16.dp))

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            FloatingActionButton(
                modifier = Modifier.padding(bottom = 8.dp),
                onClick = {
                    boardViewModel.startingChoice.value?.let { startingChoice ->
                        boardViewModel.endingChoice.value?.let { endingChoice ->
                            boardViewModel.findKnightTour(
                                startingChoice,
                                endingChoice,
                                boardViewModel.boardSize.value
                            )
                        }
                    }
                }
            ) {
                Text(text = "Generate Path")
            }

            FloatingActionButton(
                modifier = Modifier.padding(bottom = 8.dp),
                onClick = { boardViewModel.clearPaths() }
            ) {
                Text(text = "Clear")
            }
        }
    }
}

