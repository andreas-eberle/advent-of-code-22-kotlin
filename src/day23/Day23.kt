package day23

import Coordinate
import maxEach
import minEach
import readInput

const val day = "23"

val directions = listOf(
    Triple(Coordinate(-1, -1), Coordinate(0, -1), Coordinate(1, -1)),   // N
    Triple(Coordinate(-1, 1), Coordinate(0, 1), Coordinate(1, 1)),      // S
    Triple(Coordinate(-1, -1), Coordinate(-1, 0), Coordinate(-1, 1)),   // W
    Triple(Coordinate(1, -1), Coordinate(1, 0), Coordinate(1, 1))       // E
)

val allNeighbors = listOf(
    Coordinate(-1, -1),
    Coordinate(0, -1),
    Coordinate(1, -1),
    Coordinate(1, 0),
    Coordinate(1, 1),
    Coordinate(0, 1),
    Coordinate(-1, 1),
    Coordinate(-1, 0),
)

fun main() {


    fun calculatePart1Score(input: List<String>, rounds: Int = 10): Int {
        val elvStartCoordinates = input.flatMapIndexed { y, line -> line.withIndex().filter { it.value == '#' }.map { (x, _) -> Coordinate(x, y) } }.toSet()

        val allStates = (0 until rounds).runningFold(elvStartCoordinates) { elvLocations, directionIdx ->
            playRound(elvLocations, directionIdx)
        }

//        allStates.printElvBoards()

        val min = allStates.flatten().minEach()
        val max = allStates.flatten().maxEach()

        val (width, height) = max - min

        return width * height - elvStartCoordinates.size
    }


    fun calculatePart2Score(input: List<String>): Int {
        val elvStartCoordinates = input.flatMapIndexed { y, line -> line.withIndex().filter { it.value == '#' }.map { (x, _) -> Coordinate(x, y) } }.toSet()

        var elvLocations = elvStartCoordinates
        var directionIdx = 0

        while (true) {
            val nextLocations = playRound(elvLocations, directionIdx)

            if (elvLocations == nextLocations) {
                return directionIdx + 1
            }

            elvLocations = nextLocations
            directionIdx++
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("/day$day/Day${day}_test")
    val input = readInput("/day$day/Day${day}")


    val part1TestPoints = calculatePart1Score(testInput)
    println("Part1 test points: $part1TestPoints")
    check(part1TestPoints == 88)

    val part1points = calculatePart1Score(input)
    println("Part1 points: $part1points")


    val part2TestPoints = calculatePart2Score(testInput)
    println("Part2 test points: $part2TestPoints")
    check(part2TestPoints == 20)

    val part2points = calculatePart2Score(input)
    println("Part2 points: $part2points")

}

fun Set<Coordinate>.printElvesBoard(mins: Coordinate = minEach(), maxs: Coordinate = maxEach()) {
    (mins.y..maxs.y).forEach { y ->
        (mins.x..maxs.x).forEach { x ->
            if (contains(Coordinate(x, y))) {
                print("#")
            } else {
                print(".")
            }
        }
        println()
    }

    println()
}

fun List<Set<Coordinate>>.printElvBoards() {
    val absoluteMin = flatten().minEach()
    val absoluteMax = flatten().maxEach() + 1 + Coordinate(1, 0)

    forEachIndexed { idx, board ->
        println("round $idx:  offset: $absoluteMin")
        board.printElvesBoard(absoluteMin, absoluteMax)
    }
}

fun playRound(elvLocations: Set<Coordinate>, directionIdx: Int): Set<Coordinate> {
    val (noNeighborsElves, needToMoveElves) = elvLocations.partition { location -> allNeighbors.asSequence().map { it + location }.none { elvLocations.contains(it) } }

    val nextElvLocations = needToMoveElves.map { location ->
        directions.indices
            .asSequence()
            .map { directionOffset ->
                val (neighborDirection1, direction, neighborDirection2) = directions[(directionIdx + directionOffset) % directions.size]

                val nextLocation = location + direction
                val neighbor1 = location + neighborDirection1
                val neighbor2 = location + neighborDirection2

                val validLocation = !elvLocations.contains(neighbor1) && !elvLocations.contains(nextLocation) && !elvLocations.contains(neighbor2)
                Triple(location, nextLocation, validLocation)
            }
            .filter { it.third }
            .firstOrNull()
            ?: Triple(location, location, true)
    }.map { it.first to it.second }


    val validNextLocations = nextElvLocations
        .groupBy { it.second }
        .filter { (_, value) -> value.size == 1 }
        .keys

    val nextLocations = nextElvLocations.map { (location, nextLocation) ->
        if (validNextLocations.contains(nextLocation)) {
            nextLocation
        } else {
            location
        }
    }.toSet()

    return nextLocations + noNeighborsElves
}