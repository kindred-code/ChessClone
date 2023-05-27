package com.mpolitakis.vodafonechess.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mpolitakis.vodafonechess.ui.theme.darkSquare
import com.mpolitakis.vodafonechess.ui.theme.lightSquare


@Composable
fun Board(boardSize : Int) {
    Column {
        for (i in 0 until boardSize) {
            Row {
                for (j in 0 until boardSize) {
                    val isLightSquare = i % 2 == j % 2
                    val squareColor = if (isLightSquare) lightSquare else darkSquare
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .background(squareColor)
                    ) {
                        Text(text = "${i + j}")
                    }
                }
            }
        }
    }
}

