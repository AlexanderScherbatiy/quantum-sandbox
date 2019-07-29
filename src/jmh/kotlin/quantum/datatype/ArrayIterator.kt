package quantum.datatype

class ArrayIterator<T>(vararg val values: T) : Iterator<T> {

    var index = 0

    override fun hasNext() = index < values.size
    override fun next() = values[index++]
}
