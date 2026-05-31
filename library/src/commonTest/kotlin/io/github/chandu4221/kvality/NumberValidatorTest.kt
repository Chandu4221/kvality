package io.github.chandu4221.kvality

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertEquals

class NumberValidatorTest {

    @Test
    fun `required fails on null`() {
        val result = Kvality.number().required().validate(null)
        assertTrue(result is ValidationResult.Failure)
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
    }

    @Test
    fun `positive fails on zero`() {
        val result = Kvality.number().positive().validate(0)
        assertTrue(result is ValidationResult.Failure)
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
    }

    @Test
    fun `integer fails on decimal`() {
        val result = Kvality.number().integer().validate(3.5)
        assertTrue(result is ValidationResult.Failure)
    }

    @Test
    fun `integer passes on whole number`() {
        val result = Kvality.number().integer().validate(3)
        assertTrue(result is ValidationResult.Success)
    }

    @Test
    fun `multiple rules collect all errors`() {
        val result = Kvality.number().min(18).positive().validate(-5)
        assertTrue(result is ValidationResult.Failure)
        assertEquals(2, (result as? ValidationResult.Failure)?.errors["value"]?.size)
    }
}