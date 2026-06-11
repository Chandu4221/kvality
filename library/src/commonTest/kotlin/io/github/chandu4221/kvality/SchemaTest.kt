package io.github.chandu4221.kvality

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SchemaTest {

    @Test
    fun `schema passes when all fields valid`() {
        val schema = kvality {
            field("firstname") { string().min(3).required() }
            field("email") { string().email().required() }
            field("age") { number().min(18).required() }
        }

        val result = schema.validate(mapOf(
            "firstname" to "Chandu",
            "email" to "chandu@example.com",
            "age" to 25
        ))

        assertTrue(result is ValidationResult.Success)
    }

    @Test
    fun `schema collects all field errors`() {
        val schema = kvality {
            field("firstname") { string().min(5).required() }
            field("email") { string().email().required() }
            field("age") { number().min(18).required() }
        }

        val result = schema.validate(mapOf(
            "firstname" to "Jo",
            "email" to "not-an-email",
            "age" to 15
        ))

        assertTrue(result is ValidationResult.Failure)
        val errors = (result as ValidationResult.Failure).errors
        assertEquals(3, errors.size)
        assertTrue(errors.any { it.field == "firstname" })
        assertTrue(errors.any { it.field == "email" })
        assertTrue(errors.any { it.field == "age" })
    }

    @Test
    fun `schema returns correct error codes`() {
        val schema = kvality {
            field("email") { string().email().required() }
        }

        val result = schema.validate(mapOf("email" to "not-an-email"))
        assertTrue(result is ValidationResult.Failure)
        assertEquals("string.email", (result as ValidationResult.Failure).errors.first().code)
    }

    @Test
    fun `schema returns correct path`() {
        val schema = kvality {
            field("email") { string().email().required() }
        }

        val result = schema.validate(mapOf("email" to "not-an-email"))
        assertTrue(result is ValidationResult.Failure)
        assertEquals("email", (result as ValidationResult.Failure).errors.first().path)
    }

    @Test
    fun `schema handles missing optional field`() {
        val schema = kvality {
            field("name") { string().required() }
            field("website") { string().url() }
        }

        val result = schema.validate(mapOf("name" to "Chandu"))
        assertTrue(result is ValidationResult.Success)
    }

    @Test
    fun `toMap returns field to messages map`() {
        val schema = kvality {
            field("age") { number().min(18) }
        }

        val result = schema.validate(mapOf("age" to 15))
        assertTrue(result is ValidationResult.Failure)
        val map = result.toMap()
        assertEquals("must be at least 18", map["age"]?.first())
    }

    @Test
    fun `schema extend adds new fields`() {
        val base = kvality {
            field("name") { string().required() }
        }

        val extended = base.extend {
            field("email") { string().email().required() }
        }

        val result = extended.validate(mapOf(
            "name" to "Chandu",
            "email" to "not-an-email"
        ))

        assertTrue(result is ValidationResult.Failure)
        assertEquals("string.email", (result as ValidationResult.Failure).errors.first().code)
    }

    @Test
    fun `schema pick validates only picked fields`() {
        val schema = kvality {
            field("name") { string().required() }
            field("email") { string().email().required() }
        }

        val picked = schema.pick("name")
        val result = picked.validate(mapOf("name" to "Chandu"))
        assertTrue(result is ValidationResult.Success)
    }

    @Test
    fun `schema omit skips omitted fields`() {
        val schema = kvality {
            field("name") { string().required() }
            field("email") { string().email().required() }
        }

        val omitted = schema.omit("email")
        val result = omitted.validate(mapOf("name" to "Chandu"))
        assertTrue(result is ValidationResult.Success)
    }

    @Test
    fun `cross field validation works`() {
        val schema = kvality {
            field("password") { string().required() }
            field("confirmPassword") { string().required() }
            validate("passwords do not match") { input ->
                input["password"] == input["confirmPassword"]
            }
        }

        val result = schema.validate(mapOf(
            "password" to "secret123",
            "confirmPassword" to "different"
        ))

        assertTrue(result is ValidationResult.Failure)
        assertEquals("schema.crossField", (result as ValidationResult.Failure).errors.first().code)
    }

    @Test
    fun `list validator works in schema`() {
        val schema = kvality {
            field("tags") { list(string().min(2)).nonEmpty().required() }
        }

        val result = schema.validate(mapOf("tags" to listOf("a", "valid")))
        assertTrue(result is ValidationResult.Failure)
        assertTrue((result as ValidationResult.Failure).errors.any { it.code == "string.min" })
    }

    @Test
    fun `oneOf validator works`() {
        val result = Kvality.oneOf("ADMIN", "USER", "GUEST").validate("UNKNOWN")
        assertTrue(result is ValidationResult.Failure)
        assertEquals("enum.invalid", (result as ValidationResult.Failure).errors.first().code)
    }

    @Test
    fun `strict mode rejects unknown fields`() {
        val schema = kvality {
            field("name") { string().required() }
        }.strict()

        val result = schema.validate(mapOf(
            "name" to "Chandu",
            "hackerField" to "malicious"
        ))

        assertTrue(result is ValidationResult.Failure)
        assertEquals("schema.unknownField", (result as ValidationResult.Failure).errors.first().code)
    }

    @Test
    fun `strict mode passes when no unknown fields`() {
        val schema = kvality {
            field("name") { string().required() }
            field("email") { string().email().required() }
        }.strict()

        val result = schema.validate(mapOf(
            "name" to "Chandu",
            "email" to "chandu@example.com"
        ))

        assertTrue(result is ValidationResult.Success)
    }

    @Test
    fun `non strict mode ignores unknown fields`() {
        val schema = kvality {
            field("name") { string().required() }
        }

        val result = schema.validate(mapOf(
            "name" to "Chandu",
            "unknownField" to "value"
        ))

        assertTrue(result is ValidationResult.Success)
    }
}