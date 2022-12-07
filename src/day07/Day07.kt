package day07

import readInput
import java.util.*

const val day = "07"

fun main() {

    fun calculateDirectories(input: List<String>): MutableMap<String, Int> {
        val currentDirectory = Stack<String>()

        val directories = mutableMapOf<String, Int>()

        input.forEach {
            when {
                it.startsWith("$ cd ..") -> currentDirectory.pop()
                it.startsWith("$ cd") -> {
                    val currentDirectoryName = it.substringAfterLast("$ cd ")
                    currentDirectory.push(currentDirectoryName)
                }

                it.startsWith("$ ls") -> {}
                it.startsWith("dir ") -> {}
                else -> {
                    val splitLine = it.split(" ")
                    val size = splitLine[0].toInt()

                    repeat(currentDirectory.size) { depth ->
                        val directory = currentDirectory.subList(0, depth + 1).toDirectoryPath()
                        directories[directory] = directories.getOrDefault(directory, 0) + size
                    }
                }
            }
        }
        return directories
    }

    fun calculatePart1Score(input: List<String>): Int {
        val directories = calculateDirectories(input)
        return directories.filterValues { it <= 100000 }.values.sum()
    }


    fun calculatePart2Score(input: List<String>): Int {
        val directories = calculateDirectories(input)

        val totalSpace = 70000000
        val requiredForUpdate = 30000000
        val totalUsed = directories.getValue("/")

        val required = requiredForUpdate - (totalSpace - totalUsed)

        val smallestFitting = directories.entries.filter { it.value >= required}.minBy { it.value }

        return smallestFitting.value
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("/day$day/Day${day}_test")
    val input = readInput("/day$day/Day${day}")

    val part1TestPoints = calculatePart1Score(testInput)
    val part1Points = calculatePart1Score(input)

    println("Part1 test points: $part1TestPoints")
    println("Part1 points: $part1Points")
    check(part1TestPoints == 95437)


    val part2TestPoints = calculatePart2Score(testInput)
    val part2Points = calculatePart2Score(input)

    println("Part2 test points: $part2TestPoints")
    println("Part2 points: $part2Points")
    check(part2TestPoints == 24933642)

}

fun Collection<String>.toDirectoryPath() = joinToString("/")