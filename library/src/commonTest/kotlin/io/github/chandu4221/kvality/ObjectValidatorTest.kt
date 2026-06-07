package io.github.chandu4221.kvality

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ObjectValidatorTest {

    @Test
    fun `nested object passes when all fields valid`() {
        val schema = kvality {
            field("address") {
                objectSchema {
                    field("city") { string().required() }
                    field("zip") { string().length(6).required() }
                }
            }
        }

        val result = schema.validate(
            mapOf(
                "address" to mapOf(
                    "city" to "Hyderabad",
                    "zip" to "500001"
                )
            )
        )

        assertTrue(result is ValidationResult.Success)
    }

    @Test
    fun `nested object fails with correct path`() {
        val schema = kvality {
            field("address") {
                objectSchema {
                    field("city") { string().required() }
                    field("zip") { string().length(6).required() }
                }
            }
        }

        val result = schema.validate(
            mapOf(
                "address" to mapOf(
                    "city" to "",
                    "zip" to "123"
                )
            )
        )

        assertTrue(result is ValidationResult.Failure)
        val errors = (result as ValidationResult.Failure).errors
        assertTrue(errors.any { it.path == "address.city" })
        assertTrue(errors.any { it.path == "address.zip" })
    }

    @Test
    fun `nested object fails when null and not nullable`() {
        val schema = kvality {
            field("address") {
                objectSchema {
                    field("city") { string().required() }
                }
            }
        }

        val result = schema.validate(mapOf("address" to null))
        assertTrue(result is ValidationResult.Failure)
        assertEquals("object.required", (result as ValidationResult.Failure).errors.first().code)
    }

    @Test
    fun `nullable nested object passes when null`() {
        val schema = kvality {
            field("address") {
                objectSchema {
                    field("city") { string().required() }
                }.nullable()
            }
        }

        val result = schema.validate(mapOf("address" to null))
        assertTrue(result is ValidationResult.Success)
    }

    @Test
    fun `partial schema makes all fields optional`() {
        val userSchema = kvality {
            field("name") { string().required() }
            field("email") { string().email().required() }
        }

        val result = userSchema.partial().validate(emptyMap())
        assertTrue(result is ValidationResult.Success)
    }

    @Test
    fun `nullable string passes on null`() {
        val result = Kvality.string().nullable().validate(null)
        assertTrue(result is ValidationResult.Success)
    }

    @Test
    fun `optional string passes on null`() {
        val result = Kvality.string().optional().validate(null)
        assertTrue(result is ValidationResult.Success)
    }

    @Test
    fun `nullable number passes on null`() {
        val result = Kvality.number().nullable().validate(null)
        assertTrue(result is ValidationResult.Success)
    }

    @Test
    fun `optional boolean passes on null`() {
        val result = Kvality.boolean().optional().validate(null)
        assertTrue(result is ValidationResult.Success)
    }
}