import day01.withPrefixSum
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("src", "$name.txt")
    .readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')


fun List<String>.groupByBlankLine() = withPrefixSum { it == "" }
    .filter { it.second.isNotBlank() }
    .groupBy({ it.first }) { it.second }


fun <T> List<T>.getWrapped(idx: Int): T = this[idx.mod(this.size)]

fun <U, V> combinations(l1: List<U>, l2: List<V>): Sequence<Pair<U, V>> = combinations(l1.asSequence(), l2.asSequence())
fun <U, V> combinations(l1: Sequence<U>, l2: Sequence<V>): Sequence<Pair<U, V>> = l1.flatMap { v1 -> l2.map { v2 -> v1 to v2 } }
