package io.github.chandu4221.kvality

import kotlin.test.Test
import kotlin.test.assertTrue

class EdgeCaseTest {

    // ── Nullable/Optional flag propagation ──────────────────

    @Test
    fun `nullable string preserves flag in schema`() {
        val schema = kvality {
            field("name") { string().nullable() }
        }
        assertTrue(schema.validate(mapOf("name" to null)) is ValidationResult.Success)
    }

    @Test
    fun `optional string preserves flag in schema`() {
        val schema = kvality {
            field("name") { string().optional() }
        }
        assertTrue(schema.validate(mapOf("name" to null)) is ValidationResult.Success)
    }

    @Test
    fun `nullable number preserves flag in schema`() {
        val schema = kvality {
            field("age") { number().nullable() }
        }
        assertTrue(schema.validate(mapOf("age" to null)) is ValidationResult.Success)
    }

    @Test
    fun `optional boolean preserves flag in schema`() {
        val schema = kvality {
            field("active") { boolean().optional() }
        }
        assertTrue(schema.validate(mapOf("active" to null)) is ValidationResult.Success)
    }

    @Test
    fun `nullable list preserves flag in schema`() {
        val schema = kvality {
            field("tags") { list(string()).nullable() }
        }
        assertTrue(schema.validate(mapOf("tags" to null)) is ValidationResult.Success)
    }

    @Test
    fun `nullable flag preserved after partial()`() {
        val schema = kvality {
            field("name") { string().min(3).required() }
        }
        assertTrue(schema.partial().validate(emptyMap()) is ValidationResult.Success)
    }

    // ── Numeric precision ───────────────────────────────────

    @Test
    fun `min works correctly with Long values`() {
        val result = Kvality.number().min(Long.MIN_VALUE).validate(Long.MAX_VALUE)
        assertTrue(result is ValidationResult.Success)
    }

    @Test
    fun `integer passes for Int`() {
        val result = Kvality.number().integer().validate(42)
        assertTrue(result is ValidationResult.Success)
    }

    @Test
    fun `integer passes for Long`() {
        val result = Kvality.number().integer().validate(100L)
        assertTrue(result is ValidationResult.Success)
    }

    @Test
    fun `integer fails for Double with decimal`() {
        val result = Kvality.number().integer().validate(3.14)
        assertTrue(result is ValidationResult.Failure)
    }

    @Test
    fun `integer fails for Infinity`() {
        val result = Kvality.number().integer().validate(Double.POSITIVE_INFINITY)
        assertTrue(result is ValidationResult.Failure)
    }

    @Test
    fun `integer fails for NaN`() {
        val result = Kvality.number().integer().validate(Double.NaN)
        assertTrue(result is ValidationResult.Failure)
    }

    // ── Regex safety ────────────────────────────────────────

    @Test
    fun `invalid regex pattern throws IllegalArgumentException`() {
        var threw = false
        try {
            Kvality.string().regex("[invalid")
        } catch (e: IllegalArgumentException) {
            threw = true
        }
        assertTrue(threw)
    }

    @Test
    fun `valid regex pattern works correctly`() {
        val result = Kvality.string().regex("^[A-Z]+$").validate("HELLO")
        assertTrue(result is ValidationResult.Success)
    }
}