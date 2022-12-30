package day25

import readInput
import kotlin.math.pow
import kotlin.math.roundToLong

const val day = "25"

fun Int.pow(p: Int): Long = this.toDouble().pow(p.toDouble()).roundToLong()

fun String.snafuToLong(): Long {
    return reversed()
        .mapIndexed { index, c ->
            val digit = when (c) {
                '=' -> -2
                '-' -> -1
                else -> c.digitToInt()
            }
            digit * 5.pow(index)
        }.sum()
}

fun Long.toSnafu(): String {
    var value = this

    return buildString {
        while (value > 0) {
            val remainder = (value % 5).toInt()

            when (remainder) {
                3 -> append('=')
                4 -> append('-')
                else -> append(remainder)
            }

            value = value / 5 + if (remainder >= 3) 1 else 0
        }
    }.reversed()
}

fun main() {


    fun calculatePart1Score(input: List<String>): String {
        val numbers = input.map { it.snafuToLong() }
        val snafuNumbers = numbers.map { it.toSnafu() }
        return numbers.sum().toSnafu()
    }


    fun calculatePart2Score(input: List<String>): Int {
        return 0
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("/day$day/Day${day}_test")
    val input = readInput("/day$day/Day${day}")


    val part1TestPoints = calculatePart1Score(testInput)
    println("Part1 test points: $part1TestPoints")
    check(part1TestPoints == "2=-1=0")

    val part1points = calculatePart1Score(input)
    println("Part1 points: $part1points")


    val part2TestPoints = calculatePart2Score(testInput)
    println("Part2 test points: $part2TestPoints")
    check(part2TestPoints == 1707)

    val part2points = calculatePart2Score(input)
    println("Part2 points: $part2points")

}
