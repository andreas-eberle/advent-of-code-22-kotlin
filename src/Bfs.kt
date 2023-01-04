import java.util.*

inline fun <T> bfs(startState: T, crossinline isEnd: T.() -> Boolean, crossinline neighbors: T.() -> List<T>): T? {
    val queue: Queue<T> = LinkedList()
    val open = mutableSetOf<T>()
    queue.add(startState)

    while (queue.isNotEmpty()) {
        val state = queue.poll()
        open.remove(state)

        if (state.isEnd()) {
            return state
        }
        val elements = state.neighbors().filter { !open.contains(it) }
        queue.addAll(elements)
        open.addAll(elements)
    }

    return null
}