package io.github.chandu4221.kvality

class BooleanValidator internal constructor() : Validator<Boolean> {

    private val rules = mutableListOf<(Boolean?) -> String?>()

    fun required(): BooleanValidator = apply {
        rules += { v -> if (v == null) "field is required" else null }
    }

    fun isTrue(): BooleanValidator = apply {
        rules += { v -> if (v != true) "must be true" else null }
    }

    fun isFalse(): BooleanValidator = apply {
        rules += { v -> if (v != false) "must be false" else null }
    }

    fun custom(fn: (Boolean?) -> String?): BooleanValidator = apply {
        rules += fn
    }

    override fun validate(value: Boolean?): ValidationResult {
        val errors = rules.mapNotNull { it(value) }
        return if (errors.isEmpty()) ValidationResult.Success
        else ValidationResult.Failure(mapOf("value" to errors))
    }
}