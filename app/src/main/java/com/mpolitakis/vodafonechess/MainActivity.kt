package com.mpolitakis.vodafonechess


import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.mpolitakis.vodafonechess.ui.Board
import com.mpolitakis.vodafonechess.ui.theme.VodafoneChessTheme
import com.mpolitakis.vodafonechess.ui.theme.darkSquare
import com.mpolitakis.vodafonechess.ui.theme.lightSquare


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VodafoneChessTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var showBoardSizeDialog by remember { mutableStateOf(true) }
                    var boardSize  by remember { mutableStateOf(8) }
                    var boardSizeChosen by remember { mutableStateOf(false) }

                    if (showBoardSizeDialog) {
                        SizeDialog(
                            onDismiss = {
                                showBoardSizeDialog = !showBoardSizeDialog
                                Toast.makeText(baseContext, "You haven't selected a size for the board", Toast.LENGTH_SHORT)
                                    .show()
                            },
                            onNegativeClick = {
                                showBoardSizeDialog = !showBoardSizeDialog
                                boardSizeChosen = !boardSizeChosen
                                Toast.makeText(baseContext, "You haven't selected a size for the board", Toast.LENGTH_SHORT)
                                    .show()

                            },
                            onPositiveClick = {
                                showBoardSizeDialog = !showBoardSizeDialog
                                boardSizeChosen = !boardSizeChosen
                                boardSize = it
                                Toast.makeText(baseContext, "Selected size: $boardSize", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        )
                    }

                    if (boardSizeChosen){
                        Board(boardSize = boardSize)
                    }

                }
            }
        }
    }
}
@Composable
private fun SizeDialog(
    onDismiss: () -> Unit,
    onNegativeClick: () -> Unit,
    onPositiveClick: (Int) -> Unit
) {

    var boardSize by remember { mutableStateOf(6f) }


    Dialog(onDismissRequest = onDismiss) {

        Card(
            shape = RoundedCornerShape(12.dp)
        ) {

            Column(modifier = Modifier.padding(8.dp)) {

                Text(
                    text = "Select Board Size",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(8.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Color Selection
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {


                    Column {

                        Text(text = "Board Size ${boardSize.toInt()}")
                        Slider(
                            value = boardSize,
                            onValueChange = { boardSize = it },
                            valueRange = 6f..16f,
                            onValueChangeFinished = {}
                        )
                    }
                }

                // Buttons
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    TextButton(onClick = onNegativeClick) {
                        Text(text = "CANCEL")
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    TextButton(onClick = {
                        onPositiveClick(boardSize.toInt())
                    }) {
                        Text(text = "OK")
                    }
                }
            }
        }
    }
}



