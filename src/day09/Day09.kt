package day09

import readInput
import kotlin.math.absoluteValue
import kotlin.math.sign

const val day = "09"

fun main() {

    fun calculatePart1Score(input: List<String>): Int {
        val moves = input.map { it.split(" ") }
            .map { it[0] to it[1].toInt() }

        var head = Coordinate(0, 0)
        var tail = Coordinate(0, 0)

        val visitedLocations = mutableSetOf(Coordinate(0, 0))

        moves.forEach { move ->
            repeat(move.second) {
                head = head.applyMove(move.first)
                tail = tail.follow(head)
                visitedLocations.add(tail)
            }
        }

        return visitedLocations.size
    }


    fun calculatePart2Score(input: List<String>): Int {
        val moves = input.map { it.split(" ") }
            .map { it[0] to it[1].toInt() }

        val coordinates = MutableList(10) { Coordinate(0, 0) }
        val visitedLocations = mutableSetOf(Coordinate(0, 0))

        moves.forEach { move ->
            repeat(move.second) {
                coordinates[0] = coordinates[0].applyMove(move.first)

                for (index in 1 until coordinates.size) {
                    coordinates[index] = coordinates[index].follow(coordinates[index - 1])
                }

                visitedLocations.add(coordinates.last())
            }
        }

        return visitedLocations.size
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