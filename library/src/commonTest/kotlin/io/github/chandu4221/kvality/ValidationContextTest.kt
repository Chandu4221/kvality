package io.github.chandu4221.kvality

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ValidationContextTest {

    private val input = mapOf(
        "name" to "Chandu",
        "age" to 25,
        "score" to 9.5,
        "active" to true,
        "tags" to listOf("kotlin", "kmp"),
        "address" to mapOf("city" to "Hyderabad"),
        "missing" to null
    )

    @Test
    fun `getString returns correct value`() {
        assertEquals("Chandu", input.getString("name"))
    }

    @Test
    fun `getString returns null for missing key`() {
        assertNull(input.getString("unknown"))
    }

    @Test
    fun `getInt returns correct value`() {
        assertEquals(25, input.getInt("age"))
    }

    @Test
    fun `getDouble returns correct value`() {
        assertEquals(9.5, input.getDouble("score"))
    }

    @Test
    fun `getBoolean returns correct value`() {
        assertEquals(true, input.getBoolean("active"))
    }

    @Test
    fun `getList returns correct value`() {
        assertEquals(listOf("kotlin", "kmp"), input.getList("tags"))
    }

    @Test
    fun `getMap returns correct value`() {
        assertEquals(mapOf("city" to "Hyderabad"), input.getMap("address"))
    }

    @Test
    fun `typed helpers work in cross-field validation`() {
        val schema = kvality {
            field("password")        { string().required() }
            field("confirmPassword") { string().required() }
            validate("passwords do not match") { input ->
                input.getString("password") == input.getString("confirmPassword")
            }
        }

        val result = schema.validate(mapOf(
            "password"        to "secret123",
            "confirmPassword" to "secret123"
        ))

        assertTrue(result is ValidationResult.Success)
    }

    @Test
    fun `typed helpers catch mismatch in cross-field validation`() {
        val schema = kvality {
            field("password")        { string().required() }
            field("confirmPassword") { string().required() }
            validate("passwords do not match") { input ->
                input.getString("password") == input.getString("confirmPassword")
            }
        }

        val result = schema.validate(mapOf(
            "password"        to "secret123",
            "confirmPassword" to "different"
        ))

        assertTrue(result is ValidationResult.Failure)
    }
}