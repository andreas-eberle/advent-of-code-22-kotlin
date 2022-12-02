package day02

import readInput

fun main() {

    fun calculatePart1Score(input: List<String>): Int {
        return input.toPart1Moves().sumOf { it.points }
    }

    fun calculatePart2Score(input: List<String>): Int {
        val toPart2Moves = input.toPart2Moves()
        return toPart2Moves.sumOf { it.points }
    }


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("/day02/Day02_test")
    val part1TestPoints = calculatePart1Score(testInput)
    val part2TestPoints = calculatePart2Score(testInput)

    println("Part1 test points: $part1TestPoints")
    println("Part2 test points: $part2TestPoints")
    check(part1TestPoints == 15)
    check(part2TestPoints == 12)


    val input = readInput("/day02/Day02")
    val part1Points = calculatePart1Score(input)
    val part2Points = calculatePart2Score(input)

    println("Part1 points: $part1Points")
    println("Part2 points: $part2Points")

}

fun List<String>.toPart1Moves(): List<MovePart1> = map { it.toPart1Move() }
fun String.toPart1Move(): MovePart1 = split(" ").let { MovePart1(it[0].toHand(), it[1].toHand()) }

fun List<String>.toPart2Moves(): List<MovePart2> = map { it.toPart2Move() }
fun String.toPart2Move(): MovePart2 = split(" ").let { MovePart2(it[0].toHand(), it[1].toOutcome()) }

data class MovePart1(private val opponent: Hand, override val yourHand: Hand) : GameMove {
    private val win: Boolean
        get() = when {
            opponent == Hand.Rock && yourHand == Hand.Paper ||
                    opponent == Hand.Paper && yourHand == Hand.Scissors ||
                    opponent == Hand.Scissors && yourHand == Hand.Rock -> true

            else -> false
        }

    private val draw: Boolean
        get() = opponent == yourHand

    override val outcome: Outcome
        get() = when {
            win -> Outcome.Win
            draw -> Outcome.Draw
            else -> Outcome.Loss
        }
}


data class MovePart2(private val opponent: Hand, override val outcome: Outcome) : GameMove {
    override val yourHand: Hand
        get() = when (outcome) {
            Outcome.Win -> when (opponent) {
                Hand.Rock -> Hand.Paper
                Hand.Paper -> Hand.Scissors
                Hand.Scissors -> Hand.Rock
            }

            Outcome.Draw -> when (opponent) {
                Hand.Rock -> Hand.Rock
                Hand.Paper -> Hand.Paper
                Hand.Scissors -> Hand.Scissors
            }

            Outcome.Loss -> when (opponent) {
                Hand.Rock -> Hand.Scissors
                Hand.Paper -> Hand.Rock
                Hand.Scissors -> Hand.Paper
            }
        }
}

interface GameMove {
    val outcome: Outcome
    val yourHand: Hand

    val points: Int
        get() = outcome.score + yourHand.score
}


enum class Hand(val score: Int) {
    Rock(1), Paper(2), Scissors(3);
}

enum class Outcome(val score: Int) {
    Win(6), Draw(3), Loss(0)
}

fun String.toHand(): Hand = when (this) {
    "A", "X" -> Hand.Rock
    "B", "Y" -> Hand.Paper
    "C", "Z" -> Hand.Scissors
    else -> error("don't know hand $this")
}

fun String.toOutcome(): Outcome = when (this) {
    "X" -> Outcome.Loss
    "Y" -> Outcome.Draw
    "Z" -> Outcome.Win
    else -> error("don't know outcome $this")
}