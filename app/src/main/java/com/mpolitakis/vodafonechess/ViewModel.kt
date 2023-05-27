package com.mpolitakis.vodafonechess

class ViewModel {

    val board = mutableListOf<Pair<Int, Int>>()
    var startingChoice : Pair<Int, Int>? = null
    var endChoice : Pair<Int, Int>? = null
    var paths = mutableListOf<Pair<Int, Int>>()
    fun calculatePaths(startingChoice: Pair<Int, Int>, endChoice : Pair<Int, Int>){

    }

    fun findPossibleMoves(paths : List<Pair<Int, Int>>, startingChoice: Pair<Int, Int>, endChoice : Pair<Int, Int>): Int {
        // All possible moves of a knight
        val X = intArrayOf(2, 1, -1, -2, -2, -1, 1, 2)
        val Y = intArrayOf(1, 2, 2, 1, -1, -2, -2, -1)
        var count = 0

        // Check if each possible move is valid or not
        for (i in 0..7) {

            // Position of knight after move
            val x = startingChoice.first + X[i]
            val y = startingChoice.second + Y[i]

            // count valid moves
            if (x >= 0 && y >= 0) {
                count++
            }
        }


        // Return number of possible moves
        return count
    }
    fun inBoard(x: Int, y: Int, board : List<Pair<Int, Int>>){
        if ()
    }

}