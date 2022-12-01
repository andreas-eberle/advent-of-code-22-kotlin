fun main() {

    fun topCaloriesElf(input: List<String>): ElfWithCalories {
        return getElfsWithSumCalories(input)
            .maxBy { (_, sumCalories) -> sumCalories }
    }

    fun topThreeCalories(input: List<String>): Int {
        return getElfsWithSumCalories(input)
            .toList()
            .sortedByDescending { (_, sumCalories) -> sumCalories }
            .take(3)
            .sumOf { it.sumCalories }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    val part1Test = topCaloriesElf(testInput)
    check(part1Test.elfId == 4)
    check(part1Test.sumCalories == 24000)

    val topThreeCaloriesTest = topThreeCalories(testInput)
    println("topThreeCaloriesTest: $topThreeCaloriesTest")
    check(topThreeCaloriesTest == 45000)


    val input = readInput("Day01")
    println(topCaloriesElf(input))
    println(topThreeCalories(input))
}

fun getElfsWithSumCalories(input: List<String>): List<ElfWithCalories> =
    groupCaloriesByElf(input).map { it.first to it.second.sum() }
        .map { ElfWithCalories(it.first + 1, it.second) }

fun groupCaloriesByElf(input: List<String>): List<Pair<Int, List<Int>>> = input.withPrefixSum { it == "" }
    .filter { it.second.isNotBlank() }
    .groupBy({ it.first }) { it.second.toInt() }
    .map { (elfId, calories) -> elfId to calories }

data class ElfWithCalories(val elfId: Int, val sumCalories: Int)

fun <T> List<T>.withPrefixSum(predicate: (T) -> Boolean): List<Pair<Int, T>> = prefixSum(predicate).zip(this)

fun <T> List<T>.prefixSum(predicate: (T) -> Boolean): List<Int> = runningFold(0) { count, element ->
    count + if (predicate(element)) 1 else 0
}