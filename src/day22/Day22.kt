package day22

import Coordinate
import readInput

const val day = "22"

sealed interface Command
data class StepCommand(val steps: Int) : Command
data class TurnCommand(val turn: Char) : Command


fun main() {


    fun calculatePart1Score(input: List<String>): Int {
        val (commands, board) = input.parseBoardAndCommands()
        val (position, direction) = board.walkCommands(commands) { direction, position ->
            var nextPos = position
            while (this[nextPos] == ' ') {
                nextPos = nextPos.goInDirection(direction).wrap(width, height)
            }
            nextPos
        }

        return 1000 * (position.y + 1) + 4 * (position.x + 1) + direction
    }


    fun calculatePart2Score(input: List<String>): Int {
        return 0
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("/day$day/Day${day}_test")
    val input = readInput("/day$day/Day${day}")


    val part1TestPoints = calculatePart1Score(testInput)
    println("Part1 test points: $part1TestPoints")
    check(part1TestPoints == 6032)

    val part1points = calculatePart1Score(input)
    println("Part1 points: $part1points")


    val part2TestPoints = calculatePart2Score(testInput)
    println("Part2 test points: $part2TestPoints")
    check(part2TestPoints == 1707)

    val part2points = calculatePart2Score(input)
    println("Part2 points: $part2points")

}

data class Board(val board: List<CharArray>) {
    val height: Int = board.size
    val width: Int = board[0].size

    operator fun get(coordinate: Coordinate): Char = board[coordinate.y][coordinate.x]
}

fun List<CharArray>.print() = println(joinToString("\n") { it.joinToString("") })

fun Coordinate.goInDirection(direction: Int): Coordinate = when (direction) {
    0 -> copy(x = x + 1)
    1 -> copy(y = y + 1)
    2 -> copy(x = x - 1)
    3 -> copy(y = y - 1)
    else -> error("hä? Wie das? direction: $direction")
}


fun List<String>.parseBoardAndCommands(): Pair<List<Command>, Board> {
    val commandsString = last()

    val mapLines = dropLast(2).map { it.toCharArray() }
    val longestMapLineLength = mapLines.maxOf { it.size }
    val board = mapLines.map { it + CharArray(longestMapLineLength - it.size) { ' ' } }

//    board.print()

    val commands = commandsString.toCharArray().fold(listOf<Command>(StepCommand(0))) { acc, c ->
        val lastCommand = acc.last()
        when {
            c == 'R' || c == 'L' -> acc + listOf(TurnCommand(c))
            lastCommand is StepCommand -> acc.dropLast(1) + listOf(StepCommand(lastCommand.steps * 10 + c.digitToInt()))
            lastCommand is TurnCommand -> acc + listOf(StepCommand(c.digitToInt()))
            else -> error("unknown $c")
        }
    }

    return commands to Board(board)
}

fun Board.walkCommands(commands: List<Command>, wrapAround: Board.(direction: Int, pos: Coordinate) -> Coordinate): Pair<Coordinate, Int> {
    val startY = 0
    val startX = board[startY].indexOfFirst { it == '.' }

    // play game

    var pos = Coordinate(startX, startY)
    var direction = 0

    commands.forEach { command ->
        when (command) {
            is StepCommand -> {
                for (step in 0 until command.steps) {
                    val nextPos = pos.goInDirection(direction).wrap(width, height)
                    val wrappedNextPos = wrapAround(direction, nextPos)


                    if (this[wrappedNextPos] == '#') {
                        break
                    }

                    pos = wrappedNextPos
                }
            }

            is TurnCommand -> {
                when (command.turn) {
                    'R' -> direction = (direction + 1).mod(4)
                    'L' -> direction = (direction - 1).mod(4)
                }
            }
        }
    }

    return pos to direction
}