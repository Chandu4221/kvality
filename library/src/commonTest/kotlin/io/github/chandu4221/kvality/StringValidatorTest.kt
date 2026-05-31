package io.github.chandu4221.kvality

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StringValidatorTest {

    @Test
    fun `required fails on null`() {
        val result = Kvality.string().required().validate(null)
        assertTrue(result is ValidationResult.Failure)
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
    }

    @Test
    fun `email fails on invalid email`() {
        val result = Kvality.string().email().validate("not-an-email")
        assertTrue(result is ValidationResult.Failure)
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
    }

    @Test
    fun `multiple rules collect all errors`() {
        val result = Kvality.string().min(10).email().validate("hi")
        assertTrue(result is ValidationResult.Failure)
        assertEquals(2, (result as? ValidationResult.Failure)?.errors["value"]?.size)
    }

    @Test
    fun `custom validator works`() {
        val result = Kvality.string()
            .custom { if (it != "secret") "must be secret" else null }
            .validate("wrong")
        assertTrue(result is ValidationResult.Failure)
    }
}