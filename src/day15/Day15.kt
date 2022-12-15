package day15

import Coordinate
import flatten
import readInput
import java.lang.Integer.max
import java.lang.Integer.min
import kotlin.math.absoluteValue

const val day = "15"


fun main() {

    fun calculatePart1Score(input: List<String>, y: Int): Int {
        val sensorsAndBeacons = input.parseInputs()

        val blockedPositions = sensorsAndBeacons.flatMap { (sensor, beacon) ->
            val beaconDistance = sensor.manhattanDistanceTo(beacon)
            val yDistance = (sensor.y - y).absoluteValue
            val remainingDistance = beaconDistance - yDistance
            (sensor.x - remainingDistance..sensor.x + remainingDistance)
        }
            .distinct()
            .sorted()
            .subtract(sensorsAndBeacons.flatten().filter { it.y == y }.map { it.x }.toSet())

        return blockedPositions.size
    }

    fun calculatePart2Score(input: List<String>, searchArea: Int): Long {
        val sensorsAndBeacons = input.parseInputs()

        (0..searchArea).map { x ->
            sensorsAndBeacons.asSequence().map { (sensor, beacon) ->
                val maxDistance = sensor.manhattanDistanceTo(beacon)
                val xDistance = (sensor.x - x).absoluteValue
                val remainingYDistance = maxDistance - xDistance

                max(0, sensor.y - remainingYDistance)..min(searchArea, sensor.y + remainingYDistance)
            }
                .filter { !it.isEmpty() }
                .sortedBy { it.first }
                .fold(0) { y, range ->
                    if (y < range.first) {
                        return x * 4000000L + y
                    }
                    max(y, range.last + 1)
                }
        }

        return -1
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("/day$day/Day${day}_test")
    val input = readInput("/day$day/Day${day}")

    val part1TestPoints = calculatePart1Score(testInput, 10)
    val part1points = calculatePart1Score(input, 2000000)

    println("Part1 test points: $part1TestPoints")
    println("Part1 points: $part1points")
    check(part1TestPoints == 26)


    val part2TestPoints = calculatePart2Score(testInput, 20)

    val part2points = calculatePart2Score(input, 4000000)
    println("Part2 test points: \n$part2TestPoints")
    println("Part2 points: \n$part2points")
    check(part2TestPoints == 56000011L)

}

fun List<String>.parseInputs(): List<Pair<Coordinate, Coordinate>> = map { it.parseInput() }

fun String.parseInput(): Pair<Coordinate, Coordinate> {
    val (sensorText, beaconText) = split(":")

    val sensorX = sensorText.substringAfter("x=").substringBefore(", y=")
    val sensorY = sensorText.substringAfter("y=")

    val beaconX = beaconText.substringAfter("x=").substringBefore(", y=")
    val beaconY = beaconText.substringAfter("y=")

    return Coordinate(sensorX, sensorY) to Coordinate(beaconX, beaconY)
}
