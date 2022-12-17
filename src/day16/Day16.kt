package day16

import readInput

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

    fun findPathOne(currentValve: Valve, pressureRelease: Int, remainingTime: Int, remainingValves: Set<Valve>): Int {
        val neighborPressures = currentValve.neighbors
            .asSequence()
            .filter { remainingValves.contains(it.second) }
            .mapNotNull { (step, neighbor) ->
                val neighborRemainingTime = remainingTime - step - 1

                if (neighborRemainingTime < 0) {
                    null
                } else {
                    findPathOne(neighbor, pressureRelease + neighborRemainingTime * neighbor.rate, neighborRemainingTime, remainingValves.minus(neighbor))
                }
            } + listOf(pressureRelease)

        return neighborPressures.max()
    }

    fun calculatePart1Score(input: List<String>): Int {
        val contractedGraph = input.parseGraph()
        return findPathOne(contractedGraph.getValue("AA"), 0, 30, contractedGraph.values.toSet())
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