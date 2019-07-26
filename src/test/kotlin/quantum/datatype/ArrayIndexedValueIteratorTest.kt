package quantum.datatype

import org.junit.Test

class ArrayIndexedValueIteratorTest {

    @Test
    fun testArrayIteratorZeroElements() {

        val iter = ArrayIndexedValueIterator("")
        iter.checkEnd()
    }

    @Test
    fun testArrayIteratorZeroOneElement() {

        val iter = ArrayIndexedValueIterator("", "a")
        iter.checkValue(0, "a")
        iter.checkEnd()
    }

    @Test
    fun testArrayIteratorZeroTwoElement() {

        val iter = ArrayIndexedValueIterator("", "a", "b")
        iter.checkValue(0, "a")
        iter.checkValue(1, "b")
        iter.checkEnd()
    }
}
