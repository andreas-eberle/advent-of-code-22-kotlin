package day09

import readInput
import kotlin.math.absoluteValue
import kotlin.math.sign

const val day = "09"

fun main() {

    fun calculatePart1Score(input: List<String>): Int {
        val states = input.executeMoves(2)
        return states.countLastKnotPositions()
    }

    fun calculatePart2Score(input: List<String>): Int {
        val states = input.executeMoves(10)
        return states.countLastKnotPositions()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("/day$day/Day${day}_test")
    val testInput2 = readInput("/day$day/Day${day}_test2")
    val input = readInput("/day$day/Day${day}")

    val part1TestPoints = calculatePart1Score(testInput)
    val part1Points = calculatePart1Score(input)

    println("Part1 test points: $part1TestPoints")
    println("Part1 points: $part1Points")
    check(part1TestPoints == 13)


    val part2TestPoints = calculatePart2Score(testInput2)
    val part2Points = calculatePart2Score(input)

    println("Part2 test points: $part2TestPoints")
    println("Part2 points: $part2Points")
    check(part2TestPoints == 36)

}

fun List<String>.executeMoves(numKnots: Int): List<List<Coordinate>> {
    val moves = map { it.split(" ") }
        .flatMap { (move, amount) -> List(amount.toInt()) { move } }

    val states = moves.runningFold(List(numKnots) { Coordinate(0, 0) }) { coordinates, move ->
        coordinates.drop(1)
            .runningFold(coordinates.first().applyMove(move)) { previous, current -> current.follow(previous) }
    }

    return states
}

fun List<List<Coordinate>>.countLastKnotPositions(): Int = map { it.last() }.toSet().size


data class Coordinate(val x: Int, val y: Int) {
    fun applyMove(direction: String): Coordinate {
        return when (direction) {
            "R" -> copy(x = x + 1)
            "L" -> copy(x = x - 1)
            "U" -> copy(y = y + 1)
            "D" -> copy(y = y - 1)
            else -> error("Unknown direction $direction")
        }
    }

    fun follow(head: Coordinate): Coordinate {
        val deltaX = head.x - x
        val deltaY = head.y - y

        if (deltaX.absoluteValue <= 1 && deltaY.absoluteValue <= 1) {
            return this // don't move, no need
        }

        return copy(x = x + deltaX.sign, y = y + deltaY.sign)
    }
}