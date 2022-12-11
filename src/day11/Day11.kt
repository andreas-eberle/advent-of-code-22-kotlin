package day11

import groupByBlankLine
import readInput

const val day = "11"

fun main() {

    fun calculatePart1Score(input: List<String>): Long {
        val monkeys = input.groupByBlankLine().values.map { it.toMonkey(3) }
        val gameRounds = monkeys.runGame(20)
        return gameRounds.calculateMonkeyBusiness()
    }

    fun calculatePart2Score(input: List<String>): Long {
        val monkeys = input.groupByBlankLine().values.map { it.toMonkey(1) }
        val worryNormalization = monkeys.map { it.testDivider }.reduce { acc, l -> acc * l }
        val gameRounds = monkeys.runGame(10000, worryNormalization)
        return gameRounds.calculateMonkeyBusiness()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("/day$day/Day${day}_test")
    val input = readInput("/day$day/Day${day}")

    val part1TestPoints = calculatePart1Score(testInput)
    val part1points = calculatePart1Score(input)

    println("Part1 test points: $part1TestPoints")
    println("Part1 points: $part1points")
    check(part1TestPoints == 10605L)


    val part2TestPoints = calculatePart2Score(testInput)
    val part2points = calculatePart2Score(input)

    println("Part2 test points: \n$part2TestPoints")
    println("Part2 points: \n$part2points")
    check(part2TestPoints == 2713310158L)

}

fun List<Monkey>.runGame(rounds: Int, worryNormalization: Long = Long.MAX_VALUE): List<List<Monkey>> {
    return (1..rounds).runningFold(this) { oldMonkeys, _ ->
        val newMonkeys = oldMonkeys.toMutableList()

        repeat(oldMonkeys.size) { monkeyIdx ->
            val monkey = newMonkeys[monkeyIdx]

            monkey.itemsWorryLevel.map { monkey.calculateNewWorryLevel(it, worryNormalization) }.forEach { newWorryLevel ->
                val nextMonkeyIdx = monkey.selectNextMonkey(newWorryLevel)
                val nextMonkey = newMonkeys[nextMonkeyIdx]
                newMonkeys[nextMonkeyIdx] = nextMonkey.giveItem(newWorryLevel)
            }

            newMonkeys[monkeyIdx] = monkey.copy(itemsWorryLevel = emptyList(), inspectionCounter = monkey.inspectionCounter + monkey.itemsWorryLevel.size)
        }

        newMonkeys
    }
}

fun List<List<Monkey>>.calculateMonkeyBusiness(): Long {
    val (monkey1, monkey2) = this.last().sortedByDescending { it.inspectionCounter }.take(2)
    return monkey1.inspectionCounter * monkey2.inspectionCounter
}

data class Monkey(
    val itemsWorryLevel: List<Long>,
    val operationSymbol: String,
    val operationAmountString: String,
    val endOfInspectionWorryDivider: Long,
    val testDivider: Long,
    val testTrueMonkey: Int,
    val testFalseMonkey: Int,
    val inspectionCounter: Long = 0
) {
    fun giveItem(worryLevel: Long): Monkey {
        return copy(itemsWorryLevel = itemsWorryLevel + listOf(worryLevel))
    }

    fun calculateNewWorryLevel(oldWorryLevel: Long, worryNormalization: Long): Long {
        val operationAmount = when (operationAmountString) {
            "old" -> oldWorryLevel
            else -> operationAmountString.toLong()
        }

        return (when (operationSymbol) {
            "*" -> oldWorryLevel * operationAmount
            "+" -> oldWorryLevel + operationAmount
            "-" -> oldWorryLevel - operationAmount
            "/" -> oldWorryLevel / operationAmount
            else -> error("Unknown operation $operationSymbol")
        } / endOfInspectionWorryDivider) % worryNormalization
    }

    fun selectNextMonkey(worry: Long): Int {
        return if (worry % testDivider == 0L) testTrueMonkey else testFalseMonkey
    }
}


fun List<String>.toMonkey(worryDivider: Long): Monkey {
    val startWorryLevel = this[1].substringAfter("  Starting items: ").split(", ").map { it.toLong() }

    val (operationSymbol, operationAmountString) = this[2].substringAfter("  Operation: new = old ").split(" ")

    val testDivider = this[3].substringAfter("  Test: divisible by ").toLong()
    val testTrueMonkey = this[4].substringAfter("    If true: throw to monkey ").toInt()
    val testFalseMonkey = this[5].substringAfter("    If false: throw to monkey ").toInt()

    return Monkey(startWorryLevel, operationSymbol, operationAmountString, worryDivider, testDivider, testTrueMonkey, testFalseMonkey)
}
