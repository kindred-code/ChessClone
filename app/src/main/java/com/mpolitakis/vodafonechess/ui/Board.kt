package com.mpolitakis.vodafonechess.ui

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.mpolitakis.vodafonechess.BoardViewModel
import com.mpolitakis.vodafonechess.Cell
import com.mpolitakis.vodafonechess.ui.theme.darkSquare
import com.mpolitakis.vodafonechess.ui.theme.lightSquare

val boardViewModel =BoardViewModel()
val board = boardViewModel.board
var start :Cell? = null
var end : Cell? = null
@Composable
fun Board(boardSize : Int) {
    val context = LocalContext.current

    Column {

        for (i in 0 until boardSize) {
            Row {
                for (j in 0 until boardSize) {
                    val cell = Cell(i, j)
                    boardViewModel.createBoard(cell)


                    val isLightSquare = i % 2 == j % 2
                    var squareColor: Color

                    var selected1 by remember { mutableStateOf(false) }
                    var selected2 by remember { mutableStateOf(false) }

                    squareColor = if (selected1) {
                        start = Cell(i , j)
                        Color.Blue
                    } else if (selected2) {
                        end = Cell(i, j)
                        Color.Red
                    } else if (board[j].color != Color.White) {
                        board[j].color
                    } else {
                        if (i % 2 != j % 2) lightSquare else darkSquare
                    }


                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)

                            .clickable {
                                if (boardViewModel.startingChoice == null) {
                                    boardViewModel.startingChoice = Cell(i, j)
                                    selected1 = true

                                } else if (boardViewModel.endChoice == null) {
                                    boardViewModel.endChoice = Cell(i, j)
                                    selected2 = true
                                }


                            }
                            .background(squareColor)
                    )

                }
            }
        }
        Row() {
            ExtendedFloatingActionButton(
                onClick = {
                    Toast.makeText(context, "Selected size: ${start}", Toast.LENGTH_SHORT)
                        .show()
                    if (start != null && end != null)
                        boardViewModel.knight(
                            start!!,
                            end!!, boardSize, moves = 3 )

                },
                icon = {
                    Icon(
                        Icons.Filled.Build,
                        contentDescription = "Find Path"
                    )
                },
                text = { Text("Find Path") }
            )
            ExtendedFloatingActionButton(
                onClick = {
                    Log.e("e", "Hello")

                },
                icon = {
                    Icon(
                        Icons.Filled.ArrowBack,
                        contentDescription = "Clear Board"
                    )
                },
                text = { Text("Clear Board") }
            )
        }
    }

}
