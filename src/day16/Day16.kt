package day16

import readInput
import java.util.*
import kotlin.math.ceil

const val day = "16"


data class Valve(val name: String, val rate: Int, var neighbors: List<Pair<Int, Valve>> = emptyList()) {
    override fun toString(): String {
        return "Valve(name='$name', rate=$rate, neighbors=${neighbors.map { neighbor -> "${neighbor.first}|${neighbor.second.name}" }})"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Valve

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}

data class Step(val pressureRelease: Int, val remainingTime: Int, val valve: Valve, val remainingValves: SortedSet<Valve>, val openIt: Boolean = false) {
    val remainingMaxPossible = remainingValves.maxRemaining(remainingTime)
    val maxPossiblePressure = pressureRelease + remainingMaxPossible
}

fun SortedSet<Valve>.maxRemaining(remainingTime: Int) =
    take(ceil(remainingTime / 2.0).toInt())
        .withIndex()
        .sumOf { (idx, valve) -> (remainingTime - 2 * idx) * valve.rate }


fun main() {

    fun calculatePart1Score(input: List<String>): Int {
        val maxTimeSteps = 30

        val originalGraph = input.parseValves()
        val contractedGraph = originalGraph.contractGraph()

        val valvesByRateComparator = compareByDescending<Valve> { it.rate }
        val sortedValves = contractedGraph.values.toSortedSet(valvesByRateComparator)
        val absoluteMax = sortedValves.maxRemaining(maxTimeSteps)

        val queue = PriorityQueue<Step>(compareBy { absoluteMax - it.maxPossiblePressure })
        queue.add(Step(0, maxTimeSteps, contractedGraph.getValue("AA"), remainingValves = sortedValves))


        while (queue.isNotEmpty()) {
            val currStep = queue.poll()
            if (currStep.remainingTime <= 0) {
                return currStep.pressureRelease
            }

            if (!currStep.openIt && currStep.remainingValves.contains(currStep.valve)) {
                val nextRemainingTime = currStep.remainingTime - 1
                val nextPressureRelease = currStep.pressureRelease + currStep.valve.rate * nextRemainingTime
                val newRemaining = currStep.remainingValves.minus(currStep.valve).toSortedSet(valvesByRateComparator)
                queue.add(currStep.copy(pressureRelease = nextPressureRelease, remainingTime = nextRemainingTime, openIt = true, remainingValves = newRemaining))
            }

            currStep.valve.neighbors
                .map { Step(currStep.pressureRelease, currStep.remainingTime - it.first, it.second, currStep.remainingValves, false) }
                .forEach { newStep ->
                    queue.removeIf { it.valve == newStep.valve && it.remainingTime <= newStep.remainingTime && it.pressureRelease <= newStep.pressureRelease }
                    queue.add(newStep)
                }
        }

        return 0
    }

    fun calculatePart2Score(input: List<String>): Int {


        return 0
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("/day$day/Day${day}_test")
    val input = readInput("/day$day/Day${day}")


    val part1TestPoints = calculatePart1Score(testInput)
    println("Part1 test points: $part1TestPoints")
    check(part1TestPoints == 1651)

    val part1points = calculatePart1Score(input)
    println("Part1 points: $part1points")


    val part2TestPoints = calculatePart2Score(testInput)
    println("Part2 test points: \n$part2TestPoints")
    check(part2TestPoints == 1707)

    val part2points = calculatePart2Score(input)
    println("Part2 points: \n$part2points")

}


fun List<String>.parseValves(): Map<String, Valve> {
    val valvesStringMap = associate {
        val name = it.substringAfter("Valve ").substringBefore(" has flow")
        val rate = it.substringAfter("rate=").substringBefore("; tunnel").toInt()
        val neighbors = it.substringAfter("to valve").removePrefix("s ").trim().split(", ")
        name to Triple(name, rate, neighbors)
    }

    val valves = valvesStringMap.values.map { Valve(it.first, it.second) }.associateBy { it.name }
    valvesStringMap.forEach { (name, value) -> valves.getValue(name).neighbors = value.third.map { 1 to valves.getValue(it) } }

    return valves
}

fun Map<String, Valve>.contractGraph(): Map<String, Valve> {
    val contracted = mapValues { (_, value) ->
        val queue = mutableListOf(0 to value)
        val visited = mutableSetOf<Valve>()
        val allNeighbors = mutableListOf<Pair<Int, Valve>>()

        while (queue.isNotEmpty()) {
            val (currentSteps, currentValve) = queue.removeFirst()

            currentValve.neighbors.forEach { (steps, nextValve) ->
                if (!visited.contains(nextValve)) {
                    visited.add(nextValve)
                    queue.add((currentSteps + steps) to nextValve)
                    allNeighbors.add((currentSteps + steps) to nextValve)
                }
            }
        }

        value.copy(neighbors = allNeighbors.filter { it.second.rate > 0 })
    }.filter { it.value.rate > 0 || it.key == "AA" }

    return contracted
}