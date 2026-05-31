package io.github.chandu4221.kvality

import kotlin.test.Test
import kotlin.test.assertTrue

class BooleanValidatorTest {

    @Test
    fun `required fails on null`() {
        val result = Kvality.boolean().required().validate(null)
        assertTrue(result is ValidationResult.Failure)
    }

    @Test
    fun `required passes on value`() {
        val result = Kvality.boolean().required().validate(true)
        assertTrue(result is ValidationResult.Success)
    }

    @Test
    fun `isTrue fails on false`() {
        val result = Kvality.boolean().isTrue().validate(false)
        assertTrue(result is ValidationResult.Failure)
    }

    @Test
    fun `isTrue passes on true`() {
        val result = Kvality.boolean().isTrue().validate(true)
        assertTrue(result is ValidationResult.Success)
    }

    @Test
    fun `isFalse fails on true`() {
        val result = Kvality.boolean().isFalse().validate(true)
        assertTrue(result is ValidationResult.Failure)
    }

    @Test
    fun `isFalse passes on false`() {
        val result = Kvality.boolean().isFalse().validate(false)
        assertTrue(result is ValidationResult.Success)
    }

    @Test
    fun `custom validator works`() {
        val result = Kvality.boolean()
            .custom { if (it == null) "must not be null" else null }
            .validate(null)
        assertTrue(result is ValidationResult.Failure)
    }
}