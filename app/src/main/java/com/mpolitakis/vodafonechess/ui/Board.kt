package com.mpolitakis.vodafonechess.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.util.toAndroidPair
import com.mpolitakis.vodafonechess.ViewModel
import com.mpolitakis.vodafonechess.ui.theme.darkSquare
import com.mpolitakis.vodafonechess.ui.theme.lightSquare

val viewModel = ViewModel()
@Composable
fun Board(boardSize : Int) {
    Column {
        for (i in 0 until boardSize) {
            Row {
                for (j in 0 until boardSize) {

                    viewModel.board.add(Pair(i, j))

                    val isLightSquare = i % 2 == j % 2
                    var squareColor = if (isLightSquare) lightSquare else darkSquare
                    var selected1 by remember { mutableStateOf(false) }
                    var selected2 by remember { mutableStateOf(false) }
                    squareColor = if (selected1) {
                        Color.Blue
                    } else if (selected2) {
                        Color.Red
                    } else
                        squareColor

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)

                            .clickable{
                                if (viewModel.startingChoice == null){
                                    viewModel.startingChoice = Pair(i, j)
                                    selected1 = true

                                }
                                else if ( viewModel.endChoice == null){
                                    viewModel.endChoice = Pair(i, j)
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

