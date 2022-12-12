package day12

import readInput
import java.util.*

const val day = "12"


fun main() {


    fun findDijkstraPath(start: Pos, end: Pos, heightMap: List<List<Int>>): MutableList<Pos>? {
        val height = heightMap.size
        val width = heightMap[0].size

        val backPointers = List(height) { MutableList<Pos?>(width) { null } }

        val posQueue: Queue<Pos> = LinkedList()
        posQueue.add(start)
        backPointers[start.y][start.x] = start

        while (posQueue.isNotEmpty()) {
            val currPos = posQueue.poll()

            if (currPos == end) {
                break
            }

            val currentHeight = heightMap.of(currPos)

            currPos.neighbors.filter { it.inBounds(width, height) }
                .filter { neighbor -> heightMap.of(neighbor) <= currentHeight + 1 }
                .filter { backPointers.of(it) == null }
                .forEach { neighbor ->
                    posQueue.add(neighbor)
                    backPointers[neighbor.y][neighbor.x] = currPos
                }
        }

        if(backPointers.of(end) == null) {
            return null
        }

        val path = mutableListOf<Pos>()
        var currentPos: Pos = end

        while (currentPos != start) {
            path.add(currentPos)
            currentPos = backPointers.of(currentPos)
                ?: error("null")
        }
        return path
    }

    fun calculatePart1Score(input: List<String>): Int {
        val charMap = input.map { it.toCharArray().toList() }
        val heightMap = charMap.map { it.map { char -> char.mapToHeight() } }


        val start = charMap.findValuePos('S')
        val end = charMap.findValuePos('E')

        val path = findDijkstraPath(start, end, heightMap)

        return path?.size ?: 0
    }

    fun calculatePart2Score(input: List<String>): Int {
        val charMap = input.map { it.toCharArray().toList() }
        val heightMap = charMap.map { it.map { char -> char.mapToHeight() } }

        val starts = heightMap.findValuePositions(0)
        val end = charMap.findValuePos('E')

        return starts.mapNotNull { start -> findDijkstraPath(start, end, heightMap) }.minOf { it.size }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("/day$day/Day${day}_test")
    val input = readInput("/day$day/Day${day}")

    val part1TestPoints = calculatePart1Score(testInput)
    val part1points = calculatePart1Score(input)

    println("Part1 test points: $part1TestPoints")
    println("Part1 points: $part1points")
    check(part1TestPoints == 31)


    val part2TestPoints = calculatePart2Score(testInput)
    val part2points = calculatePart2Score(input)

    println("Part2 test points: \n$part2TestPoints")
    println("Part2 points: \n$part2points")
    check(part2TestPoints == 29)

}

fun <T> List<List<T>>.of(pos: Pos): T {
    return this[pos.y][pos.x]
}


fun <T> List<List<T>>.findValuePos(value: T): Pos {
    val y = indexOf(find { it.contains(value) })
    val x = this[y].indexOf(value)
    return Pos(x, y)
}

fun <T> List<List<T>>.findValuePositions(value: T): List<Pos> {
    val indexedLocations = this.map { it.withIndex().filter { pos -> pos.value == value } }.withIndex().filter { it.value.isNotEmpty() }
    return indexedLocations.flatMap { outer -> outer.value.map { Pos(it.index, outer.index) } }
}

fun Char.mapToHeight(): Int = when (this) {
    in 'a'..'z' -> this - 'a'
    'S' -> 0
    'E' -> 'z' - 'a'
    else -> error("Unknown char $this")
}


data class Pos(val x: Int, val y: Int) {
    val neighbors: List<Pos>
        get() = listOf(Pos(x - 1, y), Pos(x + 1, y), Pos(x, y + 1), Pos(x, y - 1))

    fun inBounds(width: Int, height: Int): Boolean {
        return x in 0 until width && y in 0 until height
    }
}