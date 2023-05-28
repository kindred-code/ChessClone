package com.mpolitakis.vodafonechess

import android.os.Build.VERSION_CODES.N
import androidx.compose.ui.graphics.Color
import com.mpolitakis.vodafonechess.ui.viewModel
import java.util.Random
import java.util.concurrent.ThreadLocalRandom


class ViewModel {

    val board = mutableListOf<Cell>()
    var startingChoice : Cell? = null
    var endChoice : Cell? = null
    var boardSize : Int = 6
    val boardPaths = mutableListOf<Cell>()
    // Move pattern on basis of the change of
    // x coordinates and y coordinates respectively
    val cx = intArrayOf(1, 1, 2, 2, -1, -1, -2, -2)
    val cy = intArrayOf(2, -2, 1, -1, 2, -2, 1, -1)

    // function restricts the knight to remain within
    // the 8x8 chessboard
    fun limits(x: Int, y: Int): Boolean {
        return x >= 0 && y >= 0 && x < boardSize && y < boardSize
    }

    /* Checks whether a square is valid and
    empty or not */
    fun isempty(a: IntArray, x: Int, y: Int): Boolean {
        return limits(x, y) && a[y * boardSize + x] < 0
    }

    /* Returns the number of empty squares
    adjacent to (x, y) */
    fun getDegree(a: IntArray, x: Int, y: Int): Int {
        var count = 0
        for (i in 0 until boardSize) if (isempty(
                a, x + cx[i],
                y + cy[i]
            )
        ) count++
        return count
    }

    // Picks next point using Warnsdorff's heuristic.
    // Returns false if it is not possible to pick
    // next point.
    fun nextMove(a: IntArray, cell: Cell): Cell? {
        var min_deg_idx = -1
        var c = 0
        var min_deg = boardSize + 1
        var nx: Int
        var ny: Int

        // Try all N adjacent of (*x, *y) starting
        // from a random adjacent. Find the adjacent
        // with minimum degree.
        val start = ThreadLocalRandom.current().nextInt(1000) % N
        for (count in 0 until boardSize) {
            val i = (start + count) % boardSize
            nx = cell.x + cx[i]
            ny = cell.y + cy[i]
            if (isempty(a, nx, ny) &&
                getDegree(a, nx, ny).also { c = it } < min_deg
            ) {
                min_deg_idx = i
                min_deg = c
            }
        }

        // IF we could not find a next cell
        if (min_deg_idx == -1) return null

        // Store coordinates of next point
        nx = cell.x + cx[min_deg_idx]
        ny = cell.y + cy[min_deg_idx]

        // Mark next move
        a[ny * boardSize + nx] = a[cell.y * boardSize +
                cell.x] + 1

        // Update next point
        cell.x = nx
        cell.y = ny
        return cell
    }



    /* checks its neighbouring squares */ /* If the knight ends on a square that is one
    knight's move from the beginning square,
    then tour is closed */
    fun neighbour(x: Int, y: Int, xx: Int, yy: Int): Boolean {
        for (i in 0 until boardSize) if (x + cx[i] == xx && y + cy[i] == yy) return true
        return false
    }

    /* Generates the legal moves using warnsdorff's
    heuristics. Returns false if not possible */
    fun findClosedTour(): Boolean {
        // Filling up the chessboard matrix with -1's
        val a = IntArray(boardSize * boardSize)
        for (i in 0 until boardSize * boardSize) a[i] = -1

        // initial position
        val sx = 3
        val sy = 2

        // Current points are same as initial points
        val cell = Cell(sx, sy)
        a[cell.y * boardSize + cell.x] = 1 // Mark first move.

        // Keep picking next points using
        // Warnsdorff's heuristic
        var ret: Cell? = null
        for (i in 0 until boardSize * boardSize - 1) {
            ret = nextMove(a, cell)
            if (ret == null) return false
        }

        // Check if tour is closed (Can end
        // at starting point)
        if (!neighbour(ret!!.x, ret.y, sx, sy)) return false
        val rnd = Random()
        val color = Color(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
        ret.color = color
        boardPaths.add(ret)

        return true
    }


}