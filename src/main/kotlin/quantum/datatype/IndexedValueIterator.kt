package quantum.datatype

import java.util.NoSuchElementException

interface IndexedValueIterator<V> {

    val size: Int
    val zeroValue: V

    fun hasNext(): Boolean
    fun next(consumer: (index: Int, value: V) -> Unit)
}

class ArrayIndexedValueIterator<V>(override val zeroValue: V,
                                   private vararg val values: V) : IndexedValueIterator<V> {

    private var index = 0
    override val size = values.size

    override fun hasNext() = index < size

    override fun next(consumer: (Int, V) -> Unit) {
        if (index >= size) {
            outOfBounds(index, size)
        }
        consumer(index, values[index])
        index++
    }
}

private fun outOfBounds(index: Int, size: Int): Nothing =
        throw NoSuchElementException("IndexedValueIterator index $index out of bounds $size")
