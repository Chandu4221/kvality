package io.github.chandu4221.kvality

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StringValidatorTest {

    @Test
    fun `required fails on null`() {
        val result = Kvality.string().required().validate(null)
        assertTrue(result is ValidationResult.Failure)
        assertEquals("string.required", (result as ValidationResult.Failure).errors.first().code)
    }

    @Test
    fun `required fails on blank`() {
        val result = Kvality.string().required().validate("   ")
        assertTrue(result is ValidationResult.Failure)
    }

    @Test
    fun `required passes on valid string`() {
        val result = Kvality.string().required().validate("hello")
        assertTrue(result is ValidationResult.Success)
    }

    @Test
    fun `min fails when string too short`() {
        val result = Kvality.string().min(5).validate("hi")
        assertTrue(result is ValidationResult.Failure)
        assertEquals("string.min", (result as ValidationResult.Failure).errors.first().code)
    }

    @Test
    fun `min passes when string meets length`() {
        val result = Kvality.string().min(3).validate("hello")
        assertTrue(result is ValidationResult.Success)
    }

    @Test
    fun `max fails when string too long`() {
        val result = Kvality.string().max(3).validate("hello")
        assertTrue(result is ValidationResult.Failure)
        assertEquals("string.max", (result as ValidationResult.Failure).errors.first().code)
    }

    @Test
    fun `email fails on invalid email`() {
        val result = Kvality.string().email().validate("not-an-email")
        assertTrue(result is ValidationResult.Failure)
        assertEquals("string.email", (result as ValidationResult.Failure).errors.first().code)
    }

    @Test
    fun `email passes on valid email`() {
        val result = Kvality.string().email().validate("test@example.com")
        assertTrue(result is ValidationResult.Success)
    }

    @Test
    fun `alphanum fails on special chars`() {
        val result = Kvality.string().alphanum().validate("hello@world")
        assertTrue(result is ValidationResult.Failure)
        assertEquals("string.alphanum", (result as ValidationResult.Failure).errors.first().code)
    }

    @Test
    fun `multiple rules collect all errors`() {
        val result = Kvality.string().min(10).email().validate("hi")
        assertTrue(result is ValidationResult.Failure)
        assertEquals(2, (result as ValidationResult.Failure).errors.size)
    }

    @Test
    fun `custom validator works`() {
        val result = Kvality.string()
            .custom { if (it != "secret") "must be secret" else null }
            .validate("wrong")
        assertTrue(result is ValidationResult.Failure)
        assertEquals("string.custom", (result as ValidationResult.Failure).errors.first().code)
    }

    @Test
    fun `custom error message works`() {
        val result = Kvality.string().min(5, "too short!").validate("hi")
        assertTrue(result is ValidationResult.Failure)
        assertEquals("too short!", (result as ValidationResult.Failure).errors.first().message)
    }

    @Test
    fun `uuid fails on invalid uuid`() {
        val result = Kvality.string().uuid().validate("not-a-uuid")
        assertTrue(result is ValidationResult.Failure)
        assertEquals("string.uuid", (result as ValidationResult.Failure).errors.first().code)
    }

    @Test
    fun `startsWith fails when prefix missing`() {
        val result = Kvality.string().startsWith("hello").validate("world")
        assertTrue(result is ValidationResult.Failure)
    }

    @Test
    fun `endsWith fails when suffix missing`() {
        val result = Kvality.string().endsWith(".com").validate("example.org")
        assertTrue(result is ValidationResult.Failure)
    }

    @Test
    fun `contains fails when substring missing`() {
        val result = Kvality.string().contains("admin").validate("user")
        assertTrue(result is ValidationResult.Failure)
    }

    @Test
    fun `lowercase fails on uppercase string`() {
        val result = Kvality.string().lowercase().validate("HELLO")
        assertTrue(result is ValidationResult.Failure)
    }

    @Test
    fun `uppercase fails on lowercase string`() {
        val result = Kvality.string().uppercase().validate("hello")
        assertTrue(result is ValidationResult.Failure)
    }

    @Test
    fun `length fails when not exact`() {
        val result = Kvality.string().length(5).validate("hi")
        assertTrue(result is ValidationResult.Failure)
    }
}