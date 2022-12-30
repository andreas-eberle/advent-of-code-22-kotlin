package day18

import readInput
import java.lang.Integer.max
import java.util.ArrayDeque

const val day = "18"

operator fun Triple<Int, Int, Int>.plus(o: Triple<Int, Int, Int>) = Triple(first + o.first, second + o.second, third + o.third)
operator fun Triple<Int, Int, Int>.plus(i: Int) = Triple(first + i, second + i, third + i)

fun Collection<Triple<Int, Int, Int>>.maxAll() = reduce { acc, triple -> Triple(max(acc.first, triple.first), max(acc.second, triple.second), max(acc.third, triple.third)) }

val sideOffsets = listOf(
    Triple(1, 0, 0),
    Triple(-1, 0, 0),
    Triple(0, 1, 0),
    Triple(0, -1, 0),
    Triple(0, 0, 1),
    Triple(0, 0, -1),
)


fun List<String>.parseCubes(): Set<Triple<Int, Int, Int>> = map { it.split(",") }.map { (x, y, z) -> Triple(x.toInt(), y.toInt(), z.toInt()) }.toSet()


fun main() {
    fun calculatePart1Score(input: List<String>): Int {
        val allCubes = input.parseCubes()

        return allCubes.asSequence()
            .flatMap { cube -> sideOffsets.map { cube + it } }
            .filter { !allCubes.contains(it) }
            .count()
    }

    fun calculatePart2Score(input: List<String>): Int {
        val allCubes = input.parseCubes()

        val maxAll = allCubes.maxAll() + 1

        val queue = ArrayDeque<Triple<Int, Int, Int>>()
        queue.add(Triple(0, 0, 0) )

        val outsideAir = mutableSetOf<Triple<Int, Int, Int>>()

        while (queue.isNotEmpty()) {
            val current = queue.poll()

            val validNeighbors = sideOffsets.asSequence()
                .map { current + it }
                .filter { (x, y, z) -> x in (-1..maxAll.first) && y in (-1..maxAll.second) && z in (-1..maxAll.third) }
                .filter { !allCubes.contains(it) }
                .filter { !outsideAir.contains(it) }
                .toList()

            outsideAir.addAll(validNeighbors)
            queue.addAll(validNeighbors)
        }

        return allCubes.asSequence()
            .flatMap { cube -> sideOffsets.map { cube + it } }
            .filter { !allCubes.contains(it) }
            .filter { outsideAir.contains(it) }
            .count()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("/day$day/Day${day}_test")
    val input = readInput("/day$day/Day${day}")

    val part1TestPoints = calculatePart1Score(testInput)
    println("Part1 test points: $part1TestPoints")
    check(part1TestPoints == 64)

    val part1points = calculatePart1Score(input)
    println("Part1 points: $part1points")


    val part2TestPoints = calculatePart2Score(testInput)
    println("Part2 test points: $part2TestPoints")
    check(part2TestPoints == 58)

    val part2points = calculatePart2Score(input)
    println("Part2 points: $part2points")

}

