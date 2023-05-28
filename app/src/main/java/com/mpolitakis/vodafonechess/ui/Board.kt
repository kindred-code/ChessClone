package com.mpolitakis.vodafonechess.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.mpolitakis.vodafonechess.Cell
import com.mpolitakis.vodafonechess.ViewModel
import com.mpolitakis.vodafonechess.ui.theme.darkSquare
import com.mpolitakis.vodafonechess.ui.theme.lightSquare
import java.util.Random

val viewModel = ViewModel()
@Composable
fun Board(boardSize : Int) {
    Column {
        for (i in 0 until boardSize) {
            Row {
                for (j in 0 until boardSize) {
                    val cell = Cell(i, j)
                    viewModel.board.add(cell)


                    val isLightSquare = i % 2 == j % 2
                    var squareColor: Color
                    var selected1 by remember { mutableStateOf(false) }
                    var selected2 by remember { mutableStateOf(false) }
                    squareColor = if (selected1) {
                        Color.Blue
                    } else if (selected2) {
                        Color.Red
                    }
                    else if (viewModel.board[j].color != Color.White) {
                        viewModel.board[j].color
                    }
                    else{
                        if (i % 2 != j % 2) lightSquare else darkSquare}

                    if (selected1 && selected2){
                        Log.e("e", "Hello")
                        print(viewModel.findClosedTour())
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)

                            .clickable {
                                if (viewModel.startingChoice == null) {
                                    viewModel.startingChoice = Cell(i, j)
                                    selected1 = true

                                } else if (viewModel.endChoice == null) {
                                    viewModel.endChoice = Cell(i, j)
                                    selected2 = true
                                }


                            }
                            .background(squareColor)
                    )

                }
            }
        }
    }
}
