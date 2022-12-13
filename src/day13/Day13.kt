package day13

import readInput

const val day = "13"


fun main() {

    fun calculatePart1Score(input: List<String>): Int {
        val packetGroups = input
            .map { it.parsePackets() }
            .windowed(2, step = 3)

        val valid = packetGroups
            .map { (left, right) -> left.isValid(right) }


        return valid.withIndex().filter { it.value == ThreeState.VALID }.sumOf { it.index + 1 }
    }

    fun calculatePart2Score(input: List<String>): Int {
        val divider1 = "[[2]]".parsePackets()
        val divider2 = "[[6]]".parsePackets()

        val allPackets = listOf(divider1, divider2) + input
            .filter { it.isNotBlank() }
            .map { it.parsePackets() }

        val sorted = allPackets.sortedWith(Comparator { o1, o2 ->
            val valid = o1.isValid(o2)
            when (valid) {
                ThreeState.INVALID -> 1
                ThreeState.UNDECIDED -> 0
                ThreeState.VALID -> -1
            }
        })

        val (divIdx1, divIdx2) = sorted.withIndex().filter { it.value == divider1 || it.value == divider2 }

        return (divIdx1.index + 1) * (divIdx2.index + 1)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("/day$day/Day${day}_test")
    val input = readInput("/day$day/Day${day}")

    val part1TestPoints = calculatePart1Score(testInput)
    val part1points = calculatePart1Score(input)

    println("Part1 test points: $part1TestPoints")
    println("Part1 points: $part1points")
    check(part1TestPoints == 13)


    val part2TestPoints = calculatePart2Score(testInput)
    val part2points = calculatePart2Score(input)

    println("Part2 test points: \n$part2TestPoints")
    println("Part2 points: \n$part2points")
    check(part2TestPoints == 140)

}


fun String.parsePackets(): ListPacket {
    val topLevel = ListPacket(mutableListOf(), null)
    var currentList = topLevel
    var stringBuffer = ""

    this.toCharArray().drop(1).dropLast(1).forEach { char ->
        when (char) {
            '[' -> {
                val newList = ListPacket(mutableListOf(), currentList)
                currentList.list.add(newList)
                currentList = newList
            }

            ']' -> {
                if (stringBuffer.isNotEmpty()) {
                    currentList.list.add(IntPacket(stringBuffer.toInt(), currentList))
                    stringBuffer = ""
                }

                currentList = currentList.parent ?: error("parent null")
            }

            ',' -> {
                if (stringBuffer.isNotEmpty()) {
                    currentList.list.add(IntPacket(stringBuffer.toInt(), currentList))
                    stringBuffer = ""
                }
            }

            else -> stringBuffer += char
        }
    }

    if (stringBuffer.isNotEmpty()) {
        currentList.list.add(IntPacket(stringBuffer.toInt(), currentList))
        stringBuffer = ""
    }


    return topLevel
}


sealed interface Packet {
    val parent: ListPacket?

    fun isValid(right: Packet): ThreeState
}

data class ListPacket(val list: MutableList<Packet>, override val parent: ListPacket?) : Packet {
    override fun toString(): String {
        return "ListPacket(list=$list)"
    }

    override fun isValid(right: Packet): ThreeState {
        return when (right) {
            is ListPacket -> this.list.withIndex().map { (idx, leftChild) ->
                if (idx > right.list.lastIndex) {
                    return@map ThreeState.INVALID
                }

                return@map leftChild.isValid(right.list[idx])
            }.firstOrNull { it != ThreeState.UNDECIDED }
                ?: if (this.list.size < right.list.size) ThreeState.VALID else ThreeState.UNDECIDED

            is IntPacket -> isValid(ListPacket(mutableListOf(right), right.parent))
        }
    }
}

data class IntPacket(val value: Int, override val parent: ListPacket?) : Packet {
    override fun toString(): String {
        return "IntPacket(value=$value)"
    }

    override fun isValid(right: Packet): ThreeState {
        return when (right) {
            is ListPacket -> ListPacket(mutableListOf(this), this.parent).isValid(right)
            is IntPacket ->
                when {
                    this.value < right.value -> ThreeState.VALID
                    this.value > right.value -> ThreeState.INVALID
                    else -> ThreeState.UNDECIDED
                }
        }
    }
}

enum class ThreeState {
    VALID,
    INVALID,
    UNDECIDED
}