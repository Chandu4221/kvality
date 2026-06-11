package io.github.chandu4221.kvality

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ValidationResultHelpersTest {

    private val schema = kvality {
        field("name") { string().min(3).required() }
        field("email") { string().email().required() }
        field("address") {
            objectSchema {
                field("city") { string().required() }
                field("zip") { string().length(6).required() }
            }
        }
    }

    private val result = schema.validate(mapOf(
        "name" to "Jo",
        "email" to "not-an-email",
        "address" to mapOf(
            "city" to "",
            "zip" to "123"
        )
    ))

    @Test
    fun `errorsFor returns errors for exact field`() {
        val errors = result.errorsFor("email")
        assertEquals(1, errors.size)
        assertEquals("string.email", errors.first().code)
    }

    @Test
    fun `errorsFor returns empty for unknown field`() {
        assertTrue(result.errorsFor("unknown").isEmpty())
    }

    @Test
    fun `errorsUnder returns all errors under path`() {
        val errors = result.errorsUnder("address")
        assertEquals(2, errors.size)
        assertTrue(errors.any { it.path == "address.city" })
        assertTrue(errors.any { it.path == "address.zip" })
    }

    @Test
    fun `errorsUnder returns empty for unknown path`() {
        assertTrue(result.errorsUnder("unknown").isEmpty())
    }

    @Test
    fun `hasErrors returns true for field with errors`() {
        assertTrue(result.hasErrors("email"))
    }

    @Test
    fun `hasErrors returns false for field without errors`() {
        assertFalse(result.hasErrors("unknown"))
    }

    @Test
    fun `firstErrorFor returns first error message`() {
        assertEquals("must be a valid email address", result.firstErrorFor("email"))
    }

    @Test
    fun `firstErrorFor returns null for unknown field`() {
        assertNull(result.firstErrorFor("unknown"))
    }

    @Test
    fun `firstErrorUnder returns first error under path`() {
        val error = result.firstErrorUnder("address")
        assertTrue(error != null)
        assertTrue(error.path.startsWith("address."))
    }

    @Test
    fun `toMap returns field to messages map`() {
        val map = result.toMap()
        assertTrue(map.containsKey("email"))
        assertEquals("must be a valid email address", map["email"]?.first())
    }

    @Test
    fun `toPathMap returns path to messages map`() {
        val map = result.toPathMap()
        assertTrue(map.containsKey("address.city"))
        assertTrue(map.containsKey("address.zip"))
    }

    @Test
    fun `isValid returns true on success`() {
        val successResult = kvality {
            field("name") { string().required() }
        }.validate(mapOf("name" to "Chandu"))
        assertTrue(successResult.isValid)
    }

    @Test
    fun `isValid returns false on failure`() {
        assertFalse(result.isValid)
    }
}