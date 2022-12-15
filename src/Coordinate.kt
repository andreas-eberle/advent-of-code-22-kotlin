import java.lang.Integer.max
import java.lang.Integer.min
import kotlin.math.absoluteValue

data class Coordinate(val x: Int, val y: Int) {
    constructor(x: String, y: String) : this(x.toInt(), y.toInt())

    fun manhattanDistanceTo(coordinate: Coordinate): Int = manhattanDistanceTo(coordinate.x, coordinate.y)

    fun manhattanDistanceTo(x: Int, y: Int): Int = (this.x - x).absoluteValue + (this.y - y).absoluteValue

    operator fun minus(other: Coordinate) = Coordinate(x - other.x, y - other.y)
    operator fun plus(value: Int) = Coordinate(x + value, y + value)
}


fun List<Coordinate>.maxEach(): Coordinate = reduce { acc, coordinate -> Coordinate(max(acc.x, coordinate.x), max(acc.y, coordinate.y)) }
fun List<Coordinate>.minEach(): Coordinate = reduce { acc, coordinate -> Coordinate(min(acc.x, coordinate.x), min(acc.y, coordinate.y)) }

fun List<Pair<Coordinate, Coordinate>>.flatten(): List<Coordinate> = flatMap { listOf(it.first, it.second) }
