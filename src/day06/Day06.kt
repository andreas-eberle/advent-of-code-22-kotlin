package day06

import readInput

const val day = "06"

fun main() {

    fun calculatePart1Score(input: List<String>): List<Int> {
        return input.map { signal -> signal.findEndOfMarkerIndex(4) }
    }

    fun calculatePart2Score(input: List<String>): List<Int> {
        return input.map { signal -> signal.findEndOfMarkerIndex(14) }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("/day$day/Day${day}_test")
    val input = readInput("/day$day/Day${day}")

    val part1TestPoints = calculatePart1Score(testInput)
    val part1Points = calculatePart1Score(input)

    println("Part1 test points: $part1TestPoints")
    println("Part1 points: $part1Points")
    check(part1TestPoints == listOf(7, 5, 6, 10, 11))


    val part2TestPoints = calculatePart2Score(testInput)
    val part2Points = calculatePart2Score(input)

    println("Part2 test points: $part2TestPoints")
    println("Part2 points: $part2Points")
    check(part2TestPoints == listOf(19, 23, 23, 29, 26))

}

fun String.findEndOfMarkerIndex(numCharacters: Int): Int = windowedSequence(numCharacters)
    .indexOfFirst { it.toSet().size == numCharacters } + numCharacters