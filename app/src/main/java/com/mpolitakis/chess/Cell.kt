package com.mpolitakis.chess


data class Cell(
    val x: Int,
    val y: Int,
    val step: Int = 0,
    val pathLetter: String = "",
    var isStarting: Boolean = false,
    var isEnding: Boolean = false,
    var index: Int = 0,
) {
    fun copy(
        step: Int = this.step,
        pathLetter: String = this.pathLetter,
        isStarting: Boolean = this.isStarting,
        isEnding: Boolean = this.isEnding,
        index: Int = this.index
    ): Cell {
        return Cell(x, y, step, pathLetter, isStarting, isEnding, index)
    }

}






