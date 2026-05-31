package io.github.chandu4221.kvality

class NumberValidator internal constructor() : Validator<Number> {

    private val rules = mutableListOf<(Number?) -> String?>()

    fun required(): NumberValidator = apply {
        rules += { v -> if (v == null) "field is required" else null }
    }

    fun min(n: Number): NumberValidator = apply {
        rules += { v -> if (v != null && v.toDouble() < n.toDouble()) "must be at least $n" else null }
    }

    fun max(n: Number): NumberValidator = apply {
        rules += { v -> if (v != null && v.toDouble() > n.toDouble()) "must be at most $n" else null }
    }

    fun positive(): NumberValidator = apply {
        rules += { v -> if (v != null && v.toDouble() <= 0) "must be a positive number" else null }
    }

    fun negative(): NumberValidator = apply {
        rules += { v -> if (v != null && v.toDouble() >= 0) "must be a negative number" else null }
    }

    fun integer(): NumberValidator = apply {
        rules += { v -> if (v != null && v.toDouble() % 1 != 0.0) "must be an integer" else null }
    }

    fun custom(fn: (Number?) -> String?): NumberValidator = apply {
        rules += fn
    }

    override fun validate(value: Number?): ValidationResult {
        val errors = rules.mapNotNull { it(value) }
        return if (errors.isEmpty()) ValidationResult.Success
        else ValidationResult.Failure(mapOf("value" to errors))
    }
}