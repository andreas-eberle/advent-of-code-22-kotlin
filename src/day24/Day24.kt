package day24

import Coordinate
import readInput
import java.util.*

const val day = "24"

data class Board(val board: List<List<List<Char>>>) {
    val height = board.size
    val width = board.first().size

    fun print(location: Coordinate) {
        board.forEachIndexed { y, line ->
            line.forEachIndexed { x, chars ->
                when {
                    x == location.x && y == location.y -> print("E")
                    chars.isEmpty() -> print(".")
                    chars.size == 1 -> print(chars.first())
                    else -> print(chars.size)
                }
            }
            println()
        }
        println()
    }

    fun Int.wrappedX(): Int {
        return when (this) {
            0 -> width - 2
            width - 1 -> 1
            else -> this
        }
    }

    fun Int.wrappedY(): Int {
        return when (this) {
            0 -> height - 2
            height - 1 -> 1
            else -> this
        }
    }

    fun isFree(coordinate: Coordinate): Boolean = coordinate.isIn(width, height) && board[coordinate.y][coordinate.x].isEmpty()


}

class BoardCache(initial: Board) {
    private val boardCache = mutableMapOf(0 to initial)

    fun getBoard(step: Int): Board {
        return boardCache.getOrPut(step) {
            getBoard(step - 1).run {
                val newBoard = List(height) { List(width) { mutableListOf<Char>() } }

                board.forEachIndexed { y, line ->
                    line.forEachIndexed { x, chars ->
                        chars.forEach { c ->
                            when (c) {
                                '^' -> newBoard[(y - 1).wrappedY()][x].add('^')
                                '>' -> newBoard[y][(x + 1).wrappedX()].add('>')
                                'v' -> newBoard[(y + 1).wrappedY()][x].add('v')
                                '<' -> newBoard[y][(x - 1).wrappedX()].add('<')
                                '#' -> newBoard[y][x].add('#')
                            }
                        }
                    }
                }

                Board(newBoard)
            }
        }
    }
}


val positionOffsets = listOf(
    Coordinate(0, 0),
    Coordinate(1, 0),
    Coordinate(-1, 0),
    Coordinate(0, 1),
    Coordinate(0, -1),
)

data class State(val position: Coordinate, val stepCounter: Int) {

    fun nextPossibleStates(boardCache: BoardCache): List<State> {
        val nextBoard = boardCache.getBoard(stepCounter + 1)

        return positionOffsets
            .map { position + it }
            .filter { nextBoard.isFree(it) }
            .map { State(it, stepCounter + 1) }
    }

    fun print(boardCache: BoardCache) {
        boardCache.getBoard(stepCounter + 1).print(position)
    }
}

fun main() {

    fun findPath(boardCache: BoardCache, startState: State, targetLocation: Coordinate): State? {
        val queue: Queue<State> = LinkedList()
        val open = mutableSetOf<State>()
        queue.add(startState)

        while (queue.isNotEmpty()) {
            val state = queue.poll()
            open.remove(state)

            if (state.position == targetLocation) {
                return state
            }
            val elements = state.nextPossibleStates(boardCache).filter { !open.contains(it) }
            queue.addAll(elements)
            open.addAll(elements)
        }

        return null
    }

    fun List<String>.parseBoard(): Triple<Board, Coordinate, Coordinate> {
        val board = map { it.toCharArray().map { char -> listOf(char).filter { c -> c != '.' } } }

        val start = Coordinate(board.first().indexOfFirst { it.isEmpty() }, 0)
        val end = Coordinate(board.last().indexOfFirst { it.isEmpty() }, board.size - 1)

        return Triple(Board(board), start, end)
    }


    fun calculatePart1Score(input: List<String>): Int {
        val (initialBoard, start, end) = input.parseBoard()

        val boardCache = BoardCache(initialBoard)
        val startState = State(start, 0)

        return findPath(boardCache, startState, end)?.stepCounter ?: 0
    }


    fun calculatePart2Score(input: List<String>): Int {
        val (initialBoard, start, end) = input.parseBoard()

        val boardCache = BoardCache(initialBoard)

        val path1 = findPath(boardCache, State(start, 0), end) ?: error("path 1 failed")
        val path2 = findPath(boardCache, path1, start) ?: error("path 2 failed")
        val path3 = findPath(boardCache, path2, end) ?: error("path 3 failed")

        return path3.stepCounter
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("/day$day/Day${day}_test")
    val input = readInput("/day$day/Day${day}")


    val part1TestPoints = calculatePart1Score(testInput)
    println("Part1 test points: $part1TestPoints")
    check(part1TestPoints == 18)

    val part1points = calculatePart1Score(input)
    println("Part1 points: $part1points")


    val part2TestPoints = calculatePart2Score(testInput)
    println("Part2 test points: $part2TestPoints")
    check(part2TestPoints == 54)

    val part2points = calculatePart2Score(input)
    println("Part2 points: $part2points")

}
