package quantum.datatype.benchmark

import org.openjdk.jmh.annotations.*
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.TimeUnit

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 8, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@State(Scope.Benchmark)
open class IndexedValueIteratorBenchmark {

    @Param("500")
    var value = "500"

    private var vector: Array<Double> = arrayOf()
    private var kotlinIterator = KotlinArrayIterator<Double>()
    private var javaIterator = JavaArrayIterator<Double>()
    private var indexedValueIterator = ArrayIndexedValueIterator(0.0)
    private var inlineIndexedValueIterator = InlineArrayIndexedValueIterator(0.0)
    private var baseInlineIndexedValueIterator: IndexedValueIterator<Double> = ArrayIndexedValueIterator(0.0)


    @Setup(Level.Invocation)
    fun setUp() {
        val size = value.toInt()
        val rnd = ThreadLocalRandom.current()
        vector = Array(size) { rnd.nextDouble() }
        kotlinIterator = KotlinArrayIterator(*vector)
        javaIterator = JavaArrayIterator(*vector)
        indexedValueIterator = ArrayIndexedValueIterator(0.0, *vector)
        inlineIndexedValueIterator = InlineArrayIndexedValueIterator(0.0, *vector)
        baseInlineIndexedValueIterator = InlineArrayIndexedValueIterator(0.0, *vector)
    }

    @Benchmark
    fun testRangeLoop(): Double {
        var sum = 0.0

        for (index in 0 until vector.size) {
            if (index % 3 == 0) {
                sum += vector[index]
            }
        }
        return sum
    }

    @Benchmark
    fun testKotlinIterator(): Double {
        var sum = 0.0

        for ((index, value) in kotlinIterator) {
            if (index % 3 == 0) {
                sum += value
            }
        }

        return sum
    }

    @Benchmark
    fun testJavaIterator(): Double {
        var sum = 0.0

        while (javaIterator.hasNext()) {
            val (index, value) = javaIterator.next()

            if (index % 3 == 0) {
                sum += value
            }
        }

        return sum
    }

    @Benchmark
    fun testIndexedValueIterator(): Double {
        var sum = 0.0

        while (indexedValueIterator.hasNext()) {
            indexedValueIterator.next { index, value ->
                if (index % 3 == 0) {
                    sum += value
                }
            }
        }

        return sum
    }

    @Benchmark
    fun testInlineIndexedValueIterator(): Double {
        var sum = 0.0

        while (inlineIndexedValueIterator.hasNext()) {
            inlineIndexedValueIterator.next { index, value ->
                if (index % 3 == 0) {
                    sum += value
                }
            }
        }

        return sum
    }

    @Benchmark
    fun testBaseInlineIndexedValueIterator(): Double {
        var sum = 0.0

        while (baseInlineIndexedValueIterator.hasNext()) {
            baseInlineIndexedValueIterator.next { index, value ->
                if (index % 3 == 0) {
                    sum += value
                }
            }
        }

        return sum
    }

    @Benchmark
    fun testIteratorExtensionFunction(): Double {
        var sum = 0.0

        val iter = vector.iterator()

        iter.iterate { index, value ->
            if (index % 3 == 0) {
                sum += value
            }
        }

        return sum
    }

    @Benchmark
    fun testInlineIteratorExtensionFunction(): Double {
        var sum = 0.0

        val iter = vector.iterator()

        iter.inlineIterate { index, value ->
            if (index % 3 == 0) {
                sum += value
            }
        }

        return sum
    }
}

data class IndexedValue<T>(val index: Int, val value: T)

class KotlinArrayIterator<T>(vararg val values: T) : Iterator<IndexedValue<T>> {

    var index = 0

    override fun hasNext() = index < values.size
    override fun next() = IndexedValue(index, values[index++])
}

class JavaArrayIterator<T>(vararg val values: T) : java.util.Iterator<IndexedValue<T>> {

    var index = 0

    override fun hasNext() = index < values.size
    override fun next() = IndexedValue(index, values[index++])
}

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
        consumer(index, values[index++])
    }
}

class InlineArrayIndexedValueIterator<V>(override val zeroValue: V,
                                         private vararg val values: V) : IndexedValueIterator<V> {

    private var index = 0
    override val size = values.size

    override fun hasNext() = index < size

    @Suppress("NON_PUBLIC_CALL_FROM_PUBLIC_INLINE")
    inline override fun next(consumer: (Int, V) -> Unit) {
        consumer(index, values[index++])
    }
}

fun <T> Iterator<T>.iterate(consumer: (Int, T) -> Unit) {
    var index = 0
    for (value in iterator()) {
        consumer(index++, value)
    }
}

inline fun <T> Iterator<T>.inlineIterate(consumer: (Int, T) -> Unit) {
    var index = 0
    for (value in iterator()) {
        consumer(index++, value)
    }
}
