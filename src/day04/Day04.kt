package day04

import readInput

const val day = "04"

fun main() {

    fun calculatePart1Score(input: List<String>): Int {
        return input.asSequence()
            .map { row -> row.split(",", "-").map { num -> num.toInt() } }
            .map { (it[0]..it[1]).toSet() to (it[2]..it[3]).toSet() }
            .filter { it.first.containsAll(it.second) || it.second.containsAll(it.first) }
            .count()
    }

    fun calculatePart2Score(input: List<String>): Int {
        return input.asSequence()
            .map { row -> row.split(",", "-").map { num -> num.toInt() } }
            .map { (it[0]..it[1]).toSet() to (it[2]..it[3]).toSet() }
            .filter { it.first.intersect(it.second).isNotEmpty() }
            .count()
    }


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("/day$day/Day${day}_test")
    val input = readInput("/day$day/Day${day}")

    val part1TestPoints = calculatePart1Score(testInput)
    val part1Points = calculatePart1Score(input)

    println("Part1 test points: $part1TestPoints")
    println("Part1 points: $part1Points")
    check(part1TestPoints == 2)


    val part2TestPoints = calculatePart2Score(testInput)
    val part2Points = calculatePart2Score(input)

    println("Part2 test points: $part2TestPoints")
    println("Part2 points: $part2Points")
    check(part2TestPoints == 4)

}


fun Char.toPriority(): Int = when (this) {
    in 'a'..'z' -> this - 'a' + 1
    in 'A'..'Z' -> this - 'A' + 27
    else -> error("Invalid char: $this")
}