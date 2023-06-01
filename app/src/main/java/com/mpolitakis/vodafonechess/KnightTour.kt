package com.mpolitakis.vodafonechess


class KnightTour(private val boardSize: Int) {

    private val moves = listOf(
        Move(-2, -1),
        Move(-2, 1),
        Move(-1, -2),
        Move(-1, 2),
        Move(1, -2),
        Move(1, 2),
        Move(2, -1),
        Move(2, 1)
    )

    fun findPaths(startX: Int, startY: Int, endX: Int, endY: Int): List<List<Cell>> {
        val visited = Array(boardSize) { BooleanArray(boardSize) }
        val paths = mutableListOf<List<Cell>>()

        val startCell = Cell(startX, startY, 0)
        val endCell = Cell(endX, endY)

        visited[startX][startY] = true

        val initialPath = mutableListOf(startCell)
        findPathsRecursive(startX, startY, endX, endY, visited, 1, initialPath, paths)

        return paths
    }

    private tailrec fun findPathsRecursive(
        currX: Int,
        currY: Int,
        endX: Int,
        endY: Int,
        visited: Array<BooleanArray>,
        step: Int,
        path: MutableList<Cell>,
        paths: MutableList<List<Cell>>
    ) {
        if (currX == endX && currY == endY) {
            paths.add(ArrayList(path))
            return
        }

        val nextMoves = moves.filter { move ->
            val nextX = currX + move.x
            val nextY = currY + move.y
            isSafeMove(nextX, nextY, visited)
        }

        if (nextMoves.isEmpty()) {
            return
        }

        val nextMove = nextMoves.first()
        val nextX = currX + nextMove.x
        val nextY = currY + nextMove.y

        visited[nextX][nextY] = true
        val nextCell = Cell(nextX, nextY, step)
        path.add(nextCell)

        findPathsRecursive(nextX, nextY, endX, endY, visited, step + 1, path, paths)
    }

    private fun isSafeMove(x: Int, y: Int, visited: Array<BooleanArray>): Boolean {
        return x in 0 until boardSize && y >= 0 && y < boardSize && !visited[x][y]
    }
}


