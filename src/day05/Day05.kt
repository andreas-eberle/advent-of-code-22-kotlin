package day05

import readInput

const val day = "05"

fun main() {


    fun calculatePart1Score(input: List<String>): String {
        val (stacks, moves) = input.parseStacksAndMoves()

        val mutableStacks = stacks.map { it.toMutableList() }

        moves.forEach { (times, from, to) ->
            (0 until times).forEach { _ ->
                val removed = mutableStacks[from - 1].removeFirst()
                mutableStacks[to - 1].add(0, removed)
            }
        }

        return mutableStacks.map { it.first() }.joinToString("")
    }

    fun calculatePart2Score(input: List<String>): String {
        val (stacks, moves) = input.parseStacksAndMoves()

        val mutableStacks = stacks.map { it.toMutableList() }

        moves.forEach { (times, from, to) ->
            val removed = (0 until times).map { mutableStacks[from - 1].removeFirst() }
            mutableStacks[to - 1].addAll(0, removed)
        }

        return mutableStacks.map { it.first() }.joinToString("")
    }


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("/day$day/Day${day}_test")
    val input = readInput("/day$day/Day${day}")

    val part1TestPoints = calculatePart1Score(testInput)
    val part1Points = calculatePart1Score(input)

    println("Part1 test points: $part1TestPoints")
    println("Part1 points: $part1Points")
    check(part1TestPoints == "CMZ")


    val part2TestPoints = calculatePart2Score(testInput)
    val part2Points = calculatePart2Score(input)

    println("Part2 test points: $part2TestPoints")
    println("Part2 points: $part2Points")
    check(part2TestPoints == "MCD")

}

fun List<String>.parseStacksAndMoves(): Pair<List<List<Char>>, List<Triple<Int, Int, Int>>> {
    val (stackLines, moveLines) = this.partition { !it.startsWith("move") }
    val stackParts = stackLines.dropLast(2)
        .map { it.toCharArray().slice(1 until it.length step 4) }
    val stacks = stackParts
        .fold((0 until stackParts.last().size).map { emptyList<Char>() }) { acc, lineChars ->
            acc.mapIndexed { index, list -> list + (lineChars.getOrNull(index) ?: ' ') }
        }
        .map { stack -> stack.filter { it != ' ' } }


    val moves = moveLines.map {
        val moveParts = it.split("move ", " from ", " to ")
            .drop(1)
            .map { num -> num.toInt() }
        Triple(moveParts[0], moveParts[1], moveParts[2])
    }

    return stacks to moves
}