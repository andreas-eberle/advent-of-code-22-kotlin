package day10

import readInput
import kotlin.math.absoluteValue

const val day = "10"

fun main() {

    fun calculatePart1Score(input: List<String>): Int {
        val allX = input.executeProgram()

        return allX.mapIndexed { index, x -> (1 + index) * x }.slice(19..allX.size step 40).sum()
    }

    fun calculatePart2Score(input: List<String>): String {
        val allX = input.executeProgram()

        val screen = (0 until 6).map { yPos ->
            (0 until 40).map { xPos ->
                val cycle = xPos + yPos * 40
                val x = allX[cycle]

                if ((xPos - x).absoluteValue <= 1) {
                    "#"
                } else {
                    "."
                }
            }.joinToString("")
        }.joinToString("\n")

        return screen
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("/day$day/Day${day}_test")
    val input = readInput("/day$day/Day${day}")

    val part1TestPoints = calculatePart1Score(testInput)
    val part1Points = calculatePart1Score(input)

    println("Part1 test points: $part1TestPoints")
    println("Part1 points: $part1Points")
    check(part1TestPoints == 13140)


    val part2TestPoints = calculatePart2Score(testInput)
    val part2Points = calculatePart2Score(input)

    println("Part2 test points: \n$part2TestPoints")
    println("Part2 points: \n$part2Points")
    check(
        part2TestPoints == """
        ##..##..##..##..##..##..##..##..##..##..
        ###...###...###...###...###...###...###.
        ####....####....####....####....####....
        #####.....#####.....#####.....#####.....
        ######......######......######......####
        #######.......#######.......#######.....
    """.trimIndent()
    )

}

fun List<String>.executeProgram(): List<Int> {
    return flatMap {
        when {
            it.startsWith("addx") -> listOf("noop", it)
            else -> listOf(it)
        }
    }.runningFold(1) { x, command ->
        when {
            command.startsWith("addx ") -> x + command.substringAfter("addx ").toInt()
            else -> x
        }
    }
}