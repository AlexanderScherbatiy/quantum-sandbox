package quantum.datatype

import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit
import kotlin.random.Random

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 15, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@State(Scope.Benchmark)
open class IteratorTest {

    @Param("1", "2", "3")
    var value = ""

    private var vector: Array<Double> = arrayOf()
    private var iterator = ArrayIterator<Double>()
    private var indexedValueIterator = ArrayIndexedValueIterator(0.0)

    @Setup
    fun setUp() {
        val size = value.toInt()
        vector = Array(size) { Random.nextDouble() }
        iterator = ArrayIterator(*vector)
        indexedValueIterator = ArrayIndexedValueIterator(0.0, *vector)
    }

    @Benchmark
    fun testRangeLoop(): Double {
        var sum = 0.0;

        for (i in 0 until vector.size) {
            sum += vector[i]
        }
        return sum
    }

    @Benchmark
    fun testIterator(): Double {
        var sum = 0.0;

        for (value in iterator) {
            sum += value
        }

        return sum
    }

    @Benchmark
    fun testIndexedValueIterator(): Double {
        var sum = 0.0;

        while (indexedValueIterator.hasNext()) {
            indexedValueIterator.next { _, d ->
                sum += d
            }
        }

        return sum
    }
}
