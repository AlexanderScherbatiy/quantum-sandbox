package quantum.datatype

import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertEquals
import kotlin.test.fail

fun <V> IndexedValueIterator<V>.checkValue(index: Int, value: V) {

    assertTrue(this.hasNext())

    this.next { i, v ->
        assertEquals(index, i)
        assertEquals(value, v)
    }
}

fun <V> IndexedValueIterator<V>.checkEnd() {

    assertFalse(this.hasNext())
    try {
        this.next { _, _ -> fail("Iterator next() is called") }
        fail("Exception is not thrown")
    } catch (_: NoSuchElementException) {
    }
}
