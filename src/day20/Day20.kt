package day20

import getWrapped
import readInput

const val day = "20"

fun <T> MutableList<T>.swap(idx1: Int, idx2: Int) {
    val temp = this[idx1]
    this[idx1] = this[idx2]
    this[idx2] = temp
}


fun main() {


    fun calculatePart1Score(input: List<String>): Int {
        val numbers = input.map { it.toInt() }.withIndex()

        val result = numbers.toMutableList()

        numbers.forEach { indexedNumber ->
            val currentIndex = result.indexOf(indexedNumber)
            result.removeAt(currentIndex)
            var newIndex = (currentIndex + indexedNumber.value).mod(result.size)

            if (newIndex == 0) {
                newIndex = result.size
            }

            result.add(newIndex, indexedNumber)
//            println(result.map { it.value })
        }

        val resultNumbers = result.map { it.value }
        val firstZeroIdx = resultNumbers.indexOf(0)

        return resultNumbers.getWrapped(firstZeroIdx + 1000) +
                resultNumbers.getWrapped(firstZeroIdx + 2000) +
                resultNumbers.getWrapped(firstZeroIdx + 3000)
    }


    fun calculatePart2Score(input: List<String>): Long {
        val numbers = input.map { it.toInt() * 811589153L }.withIndex()
        val result = numbers.toMutableList()

        repeat(10) {
            numbers.forEach { indexedNumber ->
                val currentIndex = result.indexOf(indexedNumber)
                result.removeAt(currentIndex)
                var newIndex = (currentIndex + indexedNumber.value).mod(result.size)

                if (newIndex == 0) {
                    newIndex = result.size
                }

                result.add(newIndex, indexedNumber)
//            println(result.map { it.value })
            }
        }

        val resultNumbers = result.map { it.value }
        val firstZeroIdx = resultNumbers.indexOf(0)

        return resultNumbers.getWrapped(firstZeroIdx + 1000) +
                resultNumbers.getWrapped(firstZeroIdx + 2000) +
                resultNumbers.getWrapped(firstZeroIdx + 3000)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("/day$day/Day${day}_test")
    val input = readInput("/day$day/Day${day}")


    val part1TestPoints = calculatePart1Score(testInput)
    println("Part1 test points: $part1TestPoints")
    check(part1TestPoints == 3)

    val part1points = calculatePart1Score(input)
    println("Part1 points: $part1points")


    val part2TestPoints = calculatePart2Score(testInput)
    println("Part2 test points: $part2TestPoints")
    check(part2TestPoints == 1623178306L)

    val part2points = calculatePart2Score(input)
    println("Part2 points: $part2points")

}
