package com.mpolitakis.chess.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mpolitakis.chess.BoardViewModel
import kotlinx.coroutines.launch

@Composable
fun Board() {
    val boardViewModel: BoardViewModel = viewModel()
    val boardSize by boardViewModel.boardSize.collectAsState()
    val startingChoiceState by boardViewModel.startingChoice.collectAsState()
    val endingChoiceState by boardViewModel.endingChoice.collectAsState()
    val successfulPaths by boardViewModel.successfulPaths.collectAsState(initial = emptyList())
    val markedCell by boardViewModel.markedCell.collectAsState()

    val board by boardViewModel.board.collectAsState()
    val updatedBoardSize by rememberUpdatedState(boardSize)
    val coroutineScope = rememberCoroutineScope()
    val showDialog = remember { mutableStateOf(false) }
    var isMarked by remember { mutableStateOf(false) }

            LazyColumn(modifier = Modifier.fillMaxSize().padding(bottom = 20.dp)) {
                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        board.chunked(updatedBoardSize).forEach { row ->
                            Row {
                                row.forEach { cell ->
                                    val color =
                                        if ((cell.x + cell.y) % 2 == 0) {
                                            Color.White
                                        } else {
                                            Color.LightGray
                                        }

                                    Box(
                                        modifier = Modifier
                                            .size(60.dp)
                                            .background(color)
                                            .clickable {
                                                if (!cell.isStarting && !cell.isEnding && !isMarked) {
                                                    coroutineScope.launch {
                                                        if (startingChoiceState == null) {
                                                            boardViewModel.setStartingChoice(cell)
                                                        } else {
                                                            boardViewModel.setEndingChoice(cell)
                                                            isMarked =
                                                                true // Mark the cell as ending
                                                        }
                                                    }
                                                } else if (cell.isEnding) {
                                                    coroutineScope.launch {
                                                        startingChoiceState?.let { startingChoice ->
                                                            endingChoiceState?.let { endingChoice ->
                                                                boardViewModel.findKnightTour(
                                                                    startingChoice,
                                                                    endingChoice
                                                                )
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    boardViewModel.setMarkedCell(cell) // Set the cell as marked
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        val pathCount = successfulPaths.size
                                        val labelText = when {
                                            cell.isStarting -> "S"
                                            cell.isEnding -> "E"
                                            pathCount > 0 -> {
                                                var foundLabel: String? = null
                                                successfulPaths.forEachIndexed { index, path ->
                                                    val pathStep =
                                                        path.indexOfFirst { it.x == cell.x && it.y == cell.y } + 1
                                                    if (pathStep > 0) {
                                                        val pathLetter = ('A' + index).toString()
                                                        foundLabel = "$pathLetter\n($pathStep)"
                                                        return@forEachIndexed
                                                    }
                                                }
                                                foundLabel ?: ""
                                            }

                                            else -> ""
                                        }

                                        Text(
                                            text = labelText,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }


        Box(modifier = Modifier.padding(bottom = 20.dp)) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ExtendedFloatingActionButton(
                    onClick = {
                        coroutineScope.launch {
                            boardViewModel.clearBoard()
                            isMarked = false
                        }
                    },
                    content = { Text(text = "Clear Board") },
                    modifier = Modifier.padding(top = 8.dp)
                )

                ExtendedFloatingActionButton(
                    onClick = {
                        coroutineScope.launch {
                            startingChoiceState?.let { startingChoice ->
                                endingChoiceState?.let { endingChoice ->
                                    val pathsFound = boardViewModel.findKnightTour(
                                        startingChoice,
                                        endingChoice
                                    )
                                    if (!pathsFound) {
                                        showDialog.value = true
                                    }
                                }
                            }
                        }
                    },
                    content = { Text(text = "Find Tour") },
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }


    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("No Paths Found") },
            text = { Text("No successful paths were found.") },
            confirmButton = {
                Button(onClick = { showDialog.value = false }) {
                    Text("OK")
                }
            }
        )
    }
}

