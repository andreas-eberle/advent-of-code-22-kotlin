package day16

import combinations
import readInput
import java.lang.Integer.max

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


fun main() {

    fun calculatePart1Score(input: List<String>): Int {
        val contractedGraph = input.parseGraph()
        return findPathOne(contractedGraph.getValue("AA"), 30, 0, contractedGraph.keys)
    }

    fun calculatePart2Score(input: List<String>): Int {
        val contractedGraph = input.parseGraph()

        val startNode = contractedGraph.getValue("AA")

        return findPathTwo(startNode, 26, startNode, 26, 0, contractedGraph.keys)
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
    println("Part2 test points: $part2TestPoints")
    check(part2TestPoints == 1707)

    val part2points = calculatePart2Score(input)
    println("Part2 points: $part2points")

}


fun List<String>.parseGraph(): Map<String, Valve> {
    val valvesStringMap = associate {
        val name = it.substringAfter("Valve ").substringBefore(" has flow")
        val rate = it.substringAfter("rate=").substringBefore("; tunnel").toInt()
        val neighbors = it.substringAfter("to valve").removePrefix("s ").trim().split(", ")
        name to Pair(rate, neighbors)
    }

    val valves = valvesStringMap
        .mapValues { (name, valve) -> Valve(name, valve.first) }
    val contractedGraph = valvesStringMap
        .mapValues { (_, valve) -> valve.second }
        .contractGraph()

    val graph = contractedGraph.mapValues { (name, neighbors) ->
        val valve = valves.getValue(name)
        valve.neighbors = neighbors
            .map { (steps, neighbor) -> steps to valves.getValue(neighbor) }
            .filter { it.second.rate > 0 }
        valve
    }.filterValues { it.rate > 0 || it.name == "AA" }

    return graph
}

fun Map<String, List<String>>.contractGraph(): Map<String, List<Pair<Int, String>>> {
    val graph = this.mapValues { (_, neighbors) -> neighbors.map { 1 to it } }

    val contracted = graph.mapValues { (_, valve) ->
        val queue = mutableListOf(0 to valve)
        val visited = mutableSetOf<String>()
        val allNeighbors = mutableListOf<Pair<Int, String>>()

        while (queue.isNotEmpty()) {
            val (currentSteps, currentValve) = queue.removeFirst()

            currentValve.forEach { (steps, neighborName) ->
                if (!visited.contains(neighborName)) {
                    visited.add(neighborName)
                    val neighbor = graph.getValue(neighborName)
                    queue.add((currentSteps + steps) to neighbor)
                    allNeighbors.add((currentSteps + steps) to neighborName)
                }
            }
        }

        allNeighbors
    }

    return contracted
}


data class State(val valve: String, val pressureRelease: Int, val remainingTime: Int, val remainingValves: Set<String>)

fun findPathOne(valve: Valve, remainingTime: Int, pressureRelease: Int, remainingValves: Set<String>): Int {
    if(remainingValves.isEmpty()){
        return pressureRelease
    }

    val neighborValues = valve.neighbors
        .asSequence()
        .filter { remainingValves.contains(it.second.name) }
        .filter { (distance, _) -> distance < remainingTime }
        .map { (distance, valve) ->
            val newRemainingTime = remainingTime - distance - 1
            findPathOne(valve, newRemainingTime, pressureRelease + valve.rate * newRemainingTime, remainingValves.minus(valve.name))
        }

    return neighborValues.maxOrNull() ?: pressureRelease
}


fun findPathTwo(v1: Valve, remainingTime1: Int, v2: Valve, remainingTime2: Int, pressureRelease: Int, remainingValves: Set<String>): Int {
    val v1Neighbors = v1.neighbors.asSequence()
        .filter { remainingValves.contains(it.second.name) }
        .filter { (distance, _) -> distance < remainingTime1 }
    val v2Neighbors = v2.neighbors.asSequence()
        .filter { remainingValves.contains(it.second.name) }
        .filter { (distance, _) -> distance < remainingTime2 }

    val pairNeighborValues = combinations(v1Neighbors, v2Neighbors)
        .filter { it.first.second.name != it.second.second.name }
        .map { (step1, step2) ->
            val newRemainingTime1 = remainingTime1 - step1.first - 1
            val newRemainingTime2 = remainingTime2 - step2.first - 1
            val newPressureRelease = pressureRelease + step1.second.rate * newRemainingTime1 + step2.second.rate * newRemainingTime2

            findPathTwo(
                step1.second, newRemainingTime1,
                step2.second, newRemainingTime2,
                newPressureRelease,
                remainingValves.minus(step1.second.name).minus(step2.second.name)
            )
        }

    val pairMax = pairNeighborValues.maxOrNull()
    if (pairMax != null) {
        return pairMax
    }

    return max(
        findPathOne(v1, remainingTime1, pressureRelease, remainingValves),
        findPathOne(v2, remainingTime2, pressureRelease, remainingValves)
    )
}
