package day08

import readInput

const val day = "08"

fun main() {


    fun calculatePart1Score(input: List<String>): Int {
        val heightsHorizontal = input.map { it.toCharArray().map { char -> char.digitToInt() } }
        val rows = heightsHorizontal.size
        val columns = heightsHorizontal[0].size
        val heightsVertical = heightsHorizontal.transpose()

        check(rows == columns)

        var visibleCount = 4 * (rows - 1)

        (1 until rows - 1).forEach { row ->
            (1 until columns - 1).forEach { column ->
                val treeHeight = heightsHorizontal[row][column]

                if (heightsHorizontal[row].subList(0, column).max() < treeHeight ||
                    heightsHorizontal[row].subList(column + 1, columns).max() < treeHeight ||
                    heightsVertical[column].subList(0, row).max() < treeHeight ||
                    heightsVertical[column].subList(row + 1, rows).max() < treeHeight
                ) {
                    visibleCount++
                }
            }
        }

        return visibleCount
    }


    fun calculatePart2Score(input: List<String>): Int {
        val heightsHorizontal = input.map { it.toCharArray().map { char -> char.digitToInt() } }
        val rows = heightsHorizontal.size
        val columns = heightsHorizontal[0].size
        val heightsVertical = heightsHorizontal.transpose()

        check(rows == columns)

        val scenicScores = Array(rows) { IntArray(columns) }

        (1 until rows - 1).forEach { row ->
            (1 until columns - 1).forEach { column ->
                val treeHeight = heightsHorizontal[row][column]

                val left = heightsHorizontal[row].subList(0, column)
                    .reversed()
                    .viewDistance(treeHeight)

                val right = heightsHorizontal[row].subList(column + 1, columns)
                    .viewDistance(treeHeight)

                val top = heightsVertical[column].subList(0, row)
                    .reversed()
                    .viewDistance(treeHeight)

                val bottom = heightsVertical[column].subList(row + 1, rows)
                    .viewDistance(treeHeight)

                scenicScores[row][column] = left * right * top * bottom
            }
        }

        return scenicScores.maxOf { it.max() }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("/day$day/Day${day}_test")
    val input = readInput("/day$day/Day${day}")

    val part1TestPoints = calculatePart1Score(testInput)
    val part1Points = calculatePart1Score(input)

    println("Part1 test points: $part1TestPoints")
    println("Part1 points: $part1Points")
    check(part1TestPoints == 21)


    val part2TestPoints = calculatePart2Score(testInput)
    val part2Points = calculatePart2Score(input)

    println("Part2 test points: $part2TestPoints")
    println("Part2 points: $part2Points")
    check(part2TestPoints == 8)

}

fun <T> List<List<T>>.transpose(): List<List<T>> = List(first().size) { i ->
    this.map { it[i] }
}

fun List<Int>.viewDistance(height: Int): Int {
    val first = withIndex()
        .firstOrNull { it.value >= height } ?: return this.size

    return first
        .index + 1
}