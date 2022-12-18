package day17

import readInput

const val day = "17"


val rockPatterns = listOf(
    arrayOf(
        booleanArrayOf(true, true, true, true)
    ),
    arrayOf(
        booleanArrayOf(false, true, false),
        booleanArrayOf(true, true, true),
        booleanArrayOf(false, true, false)
    ),
    arrayOf(
        booleanArrayOf(true, true, true),
        booleanArrayOf(false, false, true),
        booleanArrayOf(false, false, true)
    ),
    arrayOf(
        booleanArrayOf(true),
        booleanArrayOf(true),
        booleanArrayOf(true),
        booleanArrayOf(true)
    ),
    arrayOf(
        booleanArrayOf(true, true),
        booleanArrayOf(true, true),
    ),
)

val wall = booleanArrayOf(true, false, false, false, false, false, false, false, true)
val floor = booleanArrayOf(true, true, true, true, true, true, true, true, true)

data class GameState(val board: List<BooleanArray>, val rockX: Int, val rockY: Int, val rockPatternIdx: Int, val rockCounter: Int) {
    private val rockPattern: Array<BooleanArray>
        get() = rockPatterns[rockPatternIdx % rockPatterns.size]

    fun moveLeft(): GameState {
        val newX = rockX - 1

        return if (isValidPosition(newX, rockY)) {
            GameState(board, newX, rockY, rockPatternIdx, rockCounter)
        } else {
            this
        }
    }

    fun moveRight(): GameState {
        val newX = rockX + 1

        return if (isValidPosition(newX, rockY)) {
            GameState(board, newX, rockY, rockPatternIdx, rockCounter)
        } else {
            this
        }
    }

    fun moveDown(): GameState {
        val newY = rockY - 1

        return if (isValidPosition(rockX, newY)) {
            GameState(board, rockX, newY, rockPatternIdx, rockCounter)
        } else {
            newStateAfterInvalidDownMove()
        }
    }

    private fun isValidPosition(newX: Int, newY: Int): Boolean {
        val allPos = rockPattern.flatMapIndexed { inRockY, rockLine ->
            val y = newY + inRockY

            rockLine.mapIndexed { inRockX, blocked ->
                val x = newX + inRockX

                board[y][x] && blocked
            }
        }

        return allPos.none { it }
    }

    private fun newStateAfterInvalidDownMove(): GameState {
//        println("NEW ROCK")
        val newBoard = placeStone()

        val maxBlockedY = newBoard.maxBlockedY()
        val nextPatternIdx = (rockPatternIdx + 1) % rockPatterns.size
        val nextPattern = rockPatterns[nextPatternIdx]
        val nextPatternHeight = nextPattern.size

        while (newBoard.size < maxBlockedY + 3 + 1 + nextPatternHeight) {
            newBoard.add(wall)
        }

        return GameState(newBoard, 2 + 1, maxBlockedY + 3 + 1, nextPatternIdx, rockCounter + 1)
    }


    private fun placeStone(): MutableList<BooleanArray> {
        val newBoard = board.toMutableList()

        rockPattern.forEachIndexed { inRockY, rockLine ->
            val y = rockY + inRockY
            newBoard[y] = newBoard[y].clone()

            rockLine.forEachIndexed { x, blocked ->
                newBoard[y][x + rockX] = board[y][x + rockX] || blocked
            }
        }

        return newBoard
    }

    override fun toString(): String {
        return buildString {
            appendLine()

            placeStone().reversed().forEach { line ->
                line.forEach {
                    if (it) append('#') else append('.')
                }
                appendLine()
            }
            appendLine()
        }
    }
}

fun List<BooleanArray>.maxBlockedY() = indexOfLast { it.drop(1).dropLast(1).any { blocked -> blocked } }

fun main() {
    fun calculatePart1Score(input: List<String>, numFallenRocks: Int, print: Boolean = false): Int {
        val commands = input.first().toCharArray()

        val initialGame = GameState(listOf(floor, wall, wall, wall, wall), rockX = 2 + 1, rockY = 3 + 1, rockPatternIdx = 0, rockCounter = 1)
        if (print) println("initial game: $initialGame")

        val unlimitedCommands = generateSequence { commands.asSequence() }.flatten()

        unlimitedCommands.foldIndexed(initialGame) { idx, gameStateBefore: GameState, command: Char ->
            val newGameState = when (command) {
                '<' -> gameStateBefore.moveLeft()
                '>' -> gameStateBefore.moveRight()
                else -> error("unknown command $command")
            }

            if (print) println("Game $idx: command: $command $newGameState")

            val afterDownState = newGameState.moveDown()
            if (print) println("Game $idx Down: $afterDownState")

            if (afterDownState.rockCounter > numFallenRocks) {
                return afterDownState.board.maxBlockedY()
            }

            afterDownState
        }

        return 0
    }


    fun calculatePart2Score(input: List<String>): Int {
        return 0
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("/day$day/Day${day}_test")
    val input = readInput("/day$day/Day${day}")


    val part1TestPoints = calculatePart1Score(testInput, 2022)
    println("Part1 test points: $part1TestPoints")
    check(part1TestPoints == 3068)

    val part1points = calculatePart1Score(input, 2022)
    println("Part1 points: $part1points")


    val part2TestPoints = calculatePart2Score(testInput)
    println("Part2 test points: $part2TestPoints")
    check(part2TestPoints == 1707)

    val part2points = calculatePart2Score(input)
    println("Part2 points: $part2points")

}
