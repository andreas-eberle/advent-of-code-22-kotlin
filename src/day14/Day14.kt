package day14

import readInput

const val day = "14"


fun main() {

    fun calculatePart1Score(input: List<String>): Int {
        val allLines = input.map { line ->
            line.split(" -> ")
                .map { it.split(",") }
                .map { (x, y) -> Coordinate(x.toInt(), y.toInt()) }
                .map { Coordinate(it.x, it.y) }
                .windowed(2)
                .map { (p1, p2) -> p1 to p2 }
        }
            .flatten()

        val allCoordinates = allLines.flatMap { listOf(it.first, it.second) }
        val minX = allCoordinates.minOf { it.x }
        val maxX = allCoordinates.maxOf { it.x }
        val maxY = allCoordinates.maxOf { it.y }

        val offsetLines = allLines.map { (p1, p2) -> Coordinate(p1.x - minX, p1.y) to Coordinate(p2.x - minX, p2.y) }

        val height = maxY + 1
        val width = maxX - minX + 1

        val map = List(height) { MutableList(width) { false } }

        offsetLines.forEach { (p1, p2) ->
            Line(p1, p2).points.forEach { map[it] = true }
        }

        val sandSpawn = Coordinate(500 - minX, 0)

        var sandCounter = 0

        try {
            while (true) {
                var sandLocation = sandSpawn.copy()

                while (true) {
                    when {
                        !map[sandLocation.down] -> sandLocation = sandLocation.down
                        !map[sandLocation.leftDiagonal] -> sandLocation = sandLocation.leftDiagonal
                        !map[sandLocation.rightDiagonal] -> sandLocation = sandLocation.rightDiagonal
                        else -> {
                            map[sandLocation] = true
                            break
                        }
                    }

                }

                sandCounter++
            }

        } catch (e: IndexOutOfBoundsException) {
        }

        return sandCounter
    }

    fun calculatePart2Score(input: List<String>): Int {
        val initialLines = input.map { line ->
            line.split(" -> ")
                .map { it.split(",") }
                .map { (x, y) -> Coordinate(x.toInt(), y.toInt()) }
                .map { Coordinate(it.x, it.y) }
                .windowed(2)
                .map { (p1, p2) -> p1 to p2 }
        }
            .flatten()

        val allInitialPoints = initialLines.flatMap { listOf(it.first, it.second) }
        val initialMaxY = allInitialPoints.maxOf { it.y }
        val initialMinX = allInitialPoints.minOf { it.x }
        val initialMaxX = allInitialPoints.maxOf { it.x }
        val allLines = initialLines + listOf(Coordinate(initialMinX - 300, initialMaxY + 2) to Coordinate(initialMaxX + 300, initialMaxY + 2))


        val allCoordinates = allLines.flatMap { listOf(it.first, it.second) }
        val minX = allCoordinates.minOf { it.x }
        val maxX = allCoordinates.maxOf { it.x }
        val maxY = allCoordinates.maxOf { it.y }

        val offsetLines = allLines.map { (p1, p2) -> Coordinate(p1.x - minX, p1.y) to Coordinate(p2.x - minX, p2.y) }

        val height = maxY + 1
        val width = maxX - minX + 1

        val map = List(height) { MutableList(width) { false } }

        offsetLines.forEach { (p1, p2) ->
            Line(p1, p2).points.forEach { map[it] = true }
        }

        val sandSpawn = Coordinate(500 - minX, 0)

        var sandCounter = 0

        while (true) {
            var sandLocation = sandSpawn.copy()

            while (true) {
                when {
                    !map[sandLocation.down] -> sandLocation = sandLocation.down
                    !map[sandLocation.leftDiagonal] -> sandLocation = sandLocation.leftDiagonal
                    !map[sandLocation.rightDiagonal] -> sandLocation = sandLocation.rightDiagonal
                    else -> {
                        map[sandLocation] = true

                        if (sandLocation == sandSpawn) {
                            return sandCounter + 1
                        }

                        break
                    }
                }

            }

            sandCounter++
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("/day$day/Day${day}_test")
    val input = readInput("/day$day/Day${day}")

    val part1TestPoints = calculatePart1Score(testInput)
    val part1points = calculatePart1Score(input)

    println("Part1 test points: $part1TestPoints")
    println("Part1 points: $part1points")
    check(part1TestPoints == 24)


    val part2TestPoints = calculatePart2Score(testInput)
    val part2points = calculatePart2Score(input)

    println("Part2 test points: \n$part2TestPoints")
    println("Part2 points: \n$part2points")
    check(part2TestPoints == 93)

}


operator fun <T> List<List<T>>.get(coordinate: Coordinate): T {
    return this[coordinate.y][coordinate.x]
}

operator fun <T> List<MutableList<T>>.set(coordinate: Coordinate, value: T) {
    this[coordinate.y][coordinate.x] = value
}


data class Line(val p1: Coordinate, val p2: Coordinate) {
    val points: List<Coordinate>
        get() = ((p1.y..p2.y) + (p2.y..p1.y)).distinct().flatMap { y ->
            ((p1.x..p2.x) + (p2.x..p1.x)).distinct().map { x ->
                Coordinate(x, y)
            }
        }
}

data class Coordinate(val x: Int, val y: Int) {
    val down: Coordinate
        get() = Coordinate(x, y + 1)

    val leftDiagonal: Coordinate
        get() = Coordinate(x - 1, y + 1)

    val rightDiagonal: Coordinate
        get() = Coordinate(x + 1, y + 1)
}