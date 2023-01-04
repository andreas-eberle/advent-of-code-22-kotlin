import java.lang.Integer.max
import java.lang.Integer.min
import kotlin.math.absoluteValue
import kotlin.math.sign

data class Coordinate(val x: Int, val y: Int) {
    constructor(x: String, y: String) : this(x.toInt(), y.toInt())

    fun manhattanDistanceTo(coordinate: Coordinate): Int = manhattanDistanceTo(coordinate.x, coordinate.y)

    fun manhattanDistanceTo(x: Int, y: Int): Int = (this.x - x).absoluteValue + (this.y - y).absoluteValue

    operator fun minus(other: Coordinate) = Coordinate(x - other.x, y - other.y)
    operator fun plus(value: Int) = Coordinate(x + value, y + value)
    operator fun plus(other: Coordinate) = Coordinate(x + other.x, y + other.y)

    fun wrap(width: Int, height: Int): Coordinate = Coordinate(x.mod(width), y.mod(height))

    operator fun rangeTo(o: Coordinate): List<Coordinate> = (this.y..o.y).flatMap { newY -> (this.x..o.x).map { newX -> Coordinate(newX, newY) } }

    infix fun lineTo(o: Coordinate): List<Coordinate> {
        val xSign = (o.x - x).sign
        val ySign = (o.y - y).sign

        val xSequence = if (xSign >= 0) x..o.x else x downTo o.x
        val ySequence = if (ySign >= 0) y..o.y else y downTo o.y

        return when {
            xSign == 0 && ySign == 0 -> listOf(this)
            xSign != 0 && ySign != 0 -> xSequence.zip(ySequence).map { (x, y) -> Coordinate(x, y) }
            ySign == 0 -> xSequence.map { Coordinate(it, y) }
            else -> ySequence.map { Coordinate(x, it) }
        }
    }

    fun isIn(width: Int, height: Int) = x in 0 until width && y in 0 until height
}


fun Collection<Coordinate>.maxEach(): Coordinate = reduce { acc, coordinate -> Coordinate(max(acc.x, coordinate.x), max(acc.y, coordinate.y)) }
fun Collection<Coordinate>.minEach(): Coordinate = reduce { acc, coordinate -> Coordinate(min(acc.x, coordinate.x), min(acc.y, coordinate.y)) }

fun List<Pair<Coordinate, Coordinate>>.flatten(): List<Coordinate> = flatMap { listOf(it.first, it.second) }
