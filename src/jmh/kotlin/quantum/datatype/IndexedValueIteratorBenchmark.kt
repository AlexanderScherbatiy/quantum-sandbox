package quantum.datatype

import org.openjdk.jmh.annotations.*
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.TimeUnit

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@State(Scope.Benchmark)
open class IndexedValueIteratorBenchmark {

    @Param("100", "200", "300")
    var value = ""

    private var vector: Array<Double> = arrayOf()
    private var iterator = ArrayIterator<Double>()
    private var indexedValueIterator = ArrayIndexedValueIterator(0.0)


    @Setup(Level.Invocation)
    fun setUp() {
        val size = value.toInt()
        val rnd = ThreadLocalRandom.current()
        vector = Array(size) { rnd.nextDouble() }
        iterator = ArrayIterator(*vector)
        indexedValueIterator = ArrayIndexedValueIterator(0.0, *vector)
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
    fun testIterator(): Double {
        var sum = 0.0

        for ((index, value) in iterator) {
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
}

data class IndexedValue<T>(val index: Int, val value: T)

class ArrayIterator<T>(vararg val values: T) : Iterator<IndexedValue<T>> {

    var index = 0

    override fun hasNext() = index < values.size
    override fun next() = IndexedValue(index, values[index++])
}
