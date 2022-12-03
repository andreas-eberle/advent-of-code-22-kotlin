package day03

import readInput

fun main() {

    fun calculatePart1Score(input: List<String>): Int {
        return input.asSequence()
            .map { it.substring(0, it.length / 2) to it.substring(it.length / 2) }
            .map { it.first.toCharArray().toSet() to it.second.toCharArray().toSet() }
            .map { it.first.intersect(it.second) }
            .map { it.first() }
            .map { it.toPriority() }
            .sum()
    }

    fun calculatePart2Score(input: List<String>): Int {
        return input.asSequence()
            .map { it.toCharArray().toSet() }
            .withIndex()
            .groupBy({ it.index / 3 }) { it.value }
            .values.asSequence()
            .map { it[0] intersect it[1] intersect it[2] }
            .map { it.first() }
            .map { it.toPriority() }
            .sum()
    }


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("/day03/Day03_test")
    val input = readInput("/day03/Day03")

    val part1TestPoints = calculatePart1Score(testInput)
    val part1Points = calculatePart1Score(input)

    println("Part1 test points: $part1TestPoints")
    println("Part1 points: $part1Points")
    check(part1TestPoints == 157)


    val part2TestPoints = calculatePart2Score(testInput)
    val part2Points = calculatePart2Score(input)

    println("Part2 test points: $part2TestPoints")
    println("Part2 points: $part2Points")
    check(part2TestPoints == 70)

}


fun Char.toPriority(): Int = when (this) {
    in 'a'..'z' -> this - 'a' + 1
    in 'A'..'Z' -> this - 'A' + 27
    else -> error("Invalid char: $this")
}