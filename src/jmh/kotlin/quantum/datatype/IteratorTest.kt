package quantum.datatype

import org.openjdk.jmh.annotations.*
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.TimeUnit
import kotlin.random.Random

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@State(Scope.Benchmark)
open class IteratorTest {

    @Param("100", "200", "300")
    var value = ""

    private val zero = Pair(0.0, 0.0)
    private var vector: Array<Pair<Double, Double>> = arrayOf()
    private var iterator = ArrayIterator<Pair<Double, Double>>()
    private var indexedValueIterator = ArrayIndexedValueIterator(zero)


    @Setup(Level.Invocation)
    fun setUp() {
        val size = value.toInt()
        val rnd = ThreadLocalRandom.current()
        vector = Array(size) { Pair(rnd.nextDouble(), rnd.nextDouble()) }
        iterator = ArrayIterator(*vector)
        indexedValueIterator = ArrayIndexedValueIterator(zero, *vector)
    }

    fun getValue(pair: Pair<Double, Double>): Double = pair.first + pair.second

    @Benchmark
    fun testRangeLoop(): Double {
        var sum = 0.0

        for (i in 0 until vector.size) {
            sum += getValue(vector[i])
        }
        return sum
    }

    @Benchmark
    fun testIterator(): Double {
        var sum = 0.0

        for (value in iterator) {
            sum += getValue(value)
        }

        return sum
    }

    @Benchmark
    fun testIndexedValueIterator(): Double {
        var sum = 0.0

        while (indexedValueIterator.hasNext()) {
            indexedValueIterator.next { _, value ->
                sum += getValue(value)
            }
        }

        return sum
    }
}
