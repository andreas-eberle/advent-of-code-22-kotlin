package day21

import readInput

const val day = "21"


fun calculate(monkey: String, monkeyOperationsMap: Map<String, String>): Long {
    val operation = monkeyOperationsMap.getValue(monkey)

    val operationInt = operation.toLongOrNull()
    if (operationInt != null) {
        return operationInt
    }

    val (operand1, operand, operand2) = operation.split(" ")

    return when (operand) {
        "+" -> calculate(operand1, monkeyOperationsMap) + calculate(operand2, monkeyOperationsMap)
        "-" -> calculate(operand1, monkeyOperationsMap) - calculate(operand2, monkeyOperationsMap)
        "*" -> calculate(operand1, monkeyOperationsMap) * calculate(operand2, monkeyOperationsMap)
        "/" -> calculate(operand1, monkeyOperationsMap) / calculate(operand2, monkeyOperationsMap)
        else -> error("unknown operation $operation")
    }
}

fun main() {


    fun calculatePart1Score(input: List<String>): Long {
        val monkeyOperationsMap = input.associate {
            val split = it.split(": ")
            split[0] to split[1]
        }

        return calculate("root", monkeyOperationsMap)
    }


    fun calculatePart2Score(input: List<String>): Int {
        return 0
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("/day$day/Day${day}_test")
    val input = readInput("/day$day/Day${day}")


    val part1TestPoints = calculatePart1Score(testInput)
    println("Part1 test points: $part1TestPoints")
    check(part1TestPoints == 152L)

    val part1points = calculatePart1Score(input)
    println("Part1 points: $part1points")


    val part2TestPoints = calculatePart2Score(testInput)
    println("Part2 test points: $part2TestPoints")
    check(part2TestPoints == 1707)

    val part2points = calculatePart2Score(input)
    println("Part2 points: $part2points")

}
