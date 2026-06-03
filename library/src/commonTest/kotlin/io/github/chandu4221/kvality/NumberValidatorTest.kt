package io.github.chandu4221.kvality

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NumberValidatorTest {

    @Test
    fun `required fails on null`() {
        val result = Kvality.number().required().validate(null)
        assertTrue(result is ValidationResult.Failure)
        assertEquals("number.required", (result as ValidationResult.Failure).errors.first().code)
    }

    @Test
    fun `required passes on value`() {
        val result = Kvality.number().required().validate(5)
        assertTrue(result is ValidationResult.Success)
    }

    @Test
    fun `min fails when below minimum`() {
        val result = Kvality.number().min(18).validate(15)
        assertTrue(result is ValidationResult.Failure)
        assertEquals("number.min", (result as ValidationResult.Failure).errors.first().code)
    }

    @Test
    fun `min passes when equal to minimum`() {
        val result = Kvality.number().min(18).validate(18)
        assertTrue(result is ValidationResult.Success)
    }

    @Test
    fun `max fails when above maximum`() {
        val result = Kvality.number().max(100).validate(150)
        assertTrue(result is ValidationResult.Failure)
        assertEquals("number.max", (result as ValidationResult.Failure).errors.first().code)
    }

    @Test
    fun `positive fails on zero`() {
        val result = Kvality.number().positive().validate(0)
        assertTrue(result is ValidationResult.Failure)
        assertEquals("number.positive", (result as ValidationResult.Failure).errors.first().code)
    }

    @Test
    fun `positive passes on positive number`() {
        val result = Kvality.number().positive().validate(5)
        assertTrue(result is ValidationResult.Success)
    }

    @Test
    fun `negative fails on positive number`() {
        val result = Kvality.number().negative().validate(5)
        assertTrue(result is ValidationResult.Failure)
        assertEquals("number.negative", (result as ValidationResult.Failure).errors.first().code)
    }

    @Test
    fun `integer fails on decimal`() {
        val result = Kvality.number().integer().validate(3.5)
        assertTrue(result is ValidationResult.Failure)
        assertEquals("number.integer", (result as ValidationResult.Failure).errors.first().code)
    }

    @Test
    fun `integer passes on whole number`() {
        val result = Kvality.number().integer().validate(3)
        assertTrue(result is ValidationResult.Success)
    }

    @Test
    fun `nonNegative fails on negative`() {
        val result = Kvality.number().nonNegative().validate(-1)
        assertTrue(result is ValidationResult.Failure)
        assertEquals("number.nonNegative", (result as ValidationResult.Failure).errors.first().code)
    }

    @Test
    fun `nonNegative passes on zero`() {
        val result = Kvality.number().nonNegative().validate(0)
        assertTrue(result is ValidationResult.Success)
    }

    @Test
    fun `nonPositive fails on positive`() {
        val result = Kvality.number().nonPositive().validate(1)
        assertTrue(result is ValidationResult.Failure)
        assertEquals("number.nonPositive", (result as ValidationResult.Failure).errors.first().code)
    }

    @Test
    fun `between fails outside range`() {
        val result = Kvality.number().between(1, 10).validate(15)
        assertTrue(result is ValidationResult.Failure)
        assertEquals("number.between", (result as ValidationResult.Failure).errors.first().code)
    }

    @Test
    fun `between passes inside range`() {
        val result = Kvality.number().between(1, 10).validate(5)
        assertTrue(result is ValidationResult.Success)
    }

    @Test
    fun `multiple rules collect all errors`() {
        val result = Kvality.number().min(18).positive().validate(-5)
        assertTrue(result is ValidationResult.Failure)
        assertEquals(2, (result as ValidationResult.Failure).errors.size)
    }

    @Test
    fun `custom error message works`() {
        val result = Kvality.number().min(18, "must be an adult").validate(15)
        assertTrue(result is ValidationResult.Failure)
        assertEquals("must be an adult", (result as ValidationResult.Failure).errors.first().message)
    }
}