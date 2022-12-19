package day19

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import readInput

const val day = "19"


fun main() = runBlocking {


    suspend fun calculatePart1Score(input: List<String>, steps: Int): Int {
        val blueprints = input.parseBlueprints()

        val blueprintOutcomes = blueprints.map { blueprint ->
            async {
                blueprint to blueprint.runSimulation(Material(1, 0, 0, 0), Material(0, 0, 0, 0), Material(0, 0, 0, 0), true, steps)
            }
        }.awaitAll()

        println("blueprintOutcomes: $blueprintOutcomes")


        return blueprintOutcomes.sumOf { it.first.id * it.second.geode }
    }


    suspend fun calculatePart2Score(input: List<String>, steps: Int): Int {
        val blueprints = input.take(3).parseBlueprints()

        val blueprintOutcomes = blueprints.map { blueprint ->
            async {
                blueprint to blueprint.runSimulation(Material(1, 0, 0, 0), Material(0, 0, 0, 0), Material(0, 0, 0, 0), true, steps)
            }
        }.awaitAll()

        println("blueprintOutcomes: $blueprintOutcomes")


        return blueprintOutcomes.fold(1) { acc, pair -> acc * pair.second.geode }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("/day$day/Day${day}_test")
    val input = readInput("/day$day/Day${day}")


//    val part1TestPoints = calculatePart1Score(testInput, 24)
//    println("Part1 test points: $part1TestPoints")
//    check(part1TestPoints == 33)
//
//    val part1points = calculatePart1Score(input, 24)
//    println("Part1 points: $part1points")


    val part2TestPoints = calculatePart2Score(testInput, 32)
    println("Part2 test points: $part2TestPoints")
    check(part2TestPoints == 62 * 56)

    val part2points = calculatePart2Score(input, 32)
    println("Part2 points: $part2points")

}

data class Material(val ore: Int, val clay: Int, val obsidian: Int, val geode: Int) {
    operator fun plus(o: Material) = Material(ore + o.ore, clay + o.clay, obsidian + o.obsidian, geode + o.geode)

    operator fun minus(b: BotBlueprint) = Material(ore - b.ore, clay - b.clay, obsidian - b.obsidian, geode)

    fun canProduce(b: BotBlueprint) = ore >= b.ore && clay >= b.clay && obsidian >= b.obsidian
}

data class BotBlueprint(val ore: Int, val clay: Int, val obsidian: Int)

data class Blueprint(val id: Int, val botBlueprints: List<Pair<BotBlueprint, Material>>)

fun List<String>.parseBlueprints(): List<Blueprint> = map { it.parseBlueprint() }

val regex =
    """Blueprint (\d+): Each ore robot costs (\d+) ore. Each clay robot costs (\d+) ore. Each obsidian robot costs (\d+) ore and (\d+) clay. Each geode robot costs (\d+) ore and (\d+) obsidian.""".toRegex()

fun String.parseBlueprint(): Blueprint {
    val matchedValues = regex.find(this)?.groupValues?.drop(1)?.map { it.toInt() }
        ?: error("pattern not found in $this")

    return Blueprint(
        matchedValues[0],
        listOf(
            BotBlueprint(matchedValues[1], 0, 0) to Material(1, 0, 0, 0),
            BotBlueprint(matchedValues[2], 0, 0) to Material(0, 1, 0, 0),
            BotBlueprint(matchedValues[3], matchedValues[4], 0) to Material(0, 0, 1, 0),
            BotBlueprint(matchedValues[5], 0, matchedValues[6]) to Material(0, 0, 0, 1),
        )
    )
}


fun Blueprint.runSimulation(currentProduction: Material, currentMaterials: Material, lastMaterials: Material, relax: Boolean, steps: Int): Material {
    val newMaterials = currentMaterials + currentProduction

    if (steps <= 1) {
        return newMaterials
    }

    val productionWithNewBots = botBlueprints
        .filter { (botBlueprint, _) -> currentMaterials.canProduce(botBlueprint) && (!lastMaterials.canProduce(botBlueprint) || relax) }
        .map { (botBlueprint, botProduction) -> runSimulation(currentProduction + botProduction, newMaterials - botBlueprint, currentMaterials, true, steps - 1) }

    val canNotAffordAll = botBlueprints.any { (botBlueprint, _) -> !currentMaterials.canProduce(botBlueprint) }

    val allProductions = productionWithNewBots + if (canNotAffordAll) {
        listOf(runSimulation(currentProduction, newMaterials, currentMaterials, false, steps - 1))
    } else {
        emptyList()
    }

    return allProductions.maxBy { it.geode }
}