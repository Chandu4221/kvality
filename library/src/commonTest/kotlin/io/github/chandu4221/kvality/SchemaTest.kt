package io.github.chandu4221.kvality

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertEquals

class SchemaTest {

    @Test
    fun `schema passes when all fields valid`() {
        val schema = kvality {
            field("name") { string().min(3).required() }
            field("email") { string().email().required() }
            field("age") { number().min(18).required() }
        }

        val result = schema.validate(mapOf(
            "name" to "Chandu",
            "email" to "chandu@example.com",
            "age" to 25
        ))

        assertTrue(result is ValidationResult.Success)
    }

    @Test
    fun `schema collects all field errors`() {
        val schema = kvality {
            field("name") { string().min(5).required() }
            field("email") { string().email().required() }
            field("age") { number().min(18).required() }
        }

        val result = schema.validate(mapOf(
            "name" to "Jo",
            "email" to "not-an-email",
            "age" to 15
        ))

        assertTrue(result is ValidationResult.Failure)
        val errors = (result as ValidationResult.Failure).errors
        assertEquals(3, errors.size)
        assertTrue(errors.containsKey("name"))
        assertTrue(errors.containsKey("email"))
        assertTrue(errors.containsKey("age"))
    }

    @Test
    fun `schema handles missing optional field`() {
        val schema = kvality {
            field("name") { string().required() }
            field("website") { string().url() }
        }

        val result = schema.validate(mapOf(
            "name" to "Chandu"
        ))

        assertTrue(result is ValidationResult.Success)
    }

    @Test
    fun `schema returns correct error messages`() {
        val schema = kvality {
            field("age") { number().min(18) }
        }

        val result = schema.validate(mapOf("age" to 15))
        assertTrue(result is ValidationResult.Failure)
        val errors = (result as ValidationResult.Failure).errors
        assertEquals("must be at least 18", errors["age"]?.first())
    }
}