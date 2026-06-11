package io.github.chandu4221.kvality

class NumberValidator internal constructor(
    private val fieldName: String = "value",
    private val path: String = fieldName
) : Validator<Number> {

    private val rules = mutableListOf<(Number?) -> ValidationError?>()

    fun required(message: String = "field is required"): NumberValidator = apply {
        rules += { v ->
            if (v == null) ValidationError(fieldName, "number.required", message, path)
            else null
        }
    }

    fun min(n: Number, message: String = "must be at least $n"): NumberValidator = apply {
        rules += { v ->
            if (v != null && v.compareTo(n) < 0) ValidationError(fieldName, "number.min", message, path)
            else null
        }
    }

    fun max(n: Number, message: String = "must be at most $n"): NumberValidator = apply {
        rules += { v ->
            if (v != null && v.compareTo(n) > 0) ValidationError(fieldName, "number.max", message, path)
            else null
        }
    }

    fun positive(message: String = "must be a positive number"): NumberValidator = apply {
        rules += { v ->
            if (v != null && v.compareTo(0) <= 0) ValidationError(fieldName, "number.positive", message, path)
            else null
        }
    }

    fun negative(message: String = "must be a negative number"): NumberValidator = apply {
        rules += { v ->
            if (v != null && v.compareTo(0) >= 0) ValidationError(fieldName, "number.negative", message, path)
            else null
        }
    }

    fun nonNegative(message: String = "must be zero or positive"): NumberValidator = apply {
        rules += { v ->
            if (v != null && v.compareTo(0) < 0) ValidationError(fieldName, "number.nonNegative", message, path)
            else null
        }
    }

    fun nonPositive(message: String = "must be zero or negative"): NumberValidator = apply {
        rules += { v ->
            if (v != null && v.compareTo(0) > 0) ValidationError(fieldName, "number.nonPositive", message, path)
            else null
        }
    }

    fun between(min: Number, max: Number, message: String = "must be between $min and $max"): NumberValidator = apply {
        rules += { v ->
            if (v != null && (v.compareTo(min) < 0 || v.compareTo(max) > 0))
                ValidationError(fieldName, "number.between", message, path)
            else null
        }
    }

    fun integer(message: String = "must be an integer"): NumberValidator = apply {
        rules += { v ->
            if (v != null) {
                val isInteger = when (v) {
                    is Int, is Long, is Short, is Byte -> false
                    is Float -> v != kotlin.math.floor(v.toDouble()).toFloat() || v.isNaN() || v.isInfinite()
                    is Double -> v != kotlin.math.floor(v) || v.isNaN() || v.isInfinite()
                    else -> v.toDouble() % 1 != 0.0
                }
                if (isInteger) ValidationError(fieldName, "number.integer", message, path)
                else null
            } else null
        }
    }

    private var isNullable = false
    private var isOptional = false

    fun nullable(): NumberValidator = apply { isNullable = true }

    fun optional(): NumberValidator = apply { isOptional = true }

    fun custom(code: String = "number.custom", fn: (Number?) -> String?): NumberValidator = apply {
        rules += { v ->
            fn(v)?.let { ValidationError(fieldName, code, it, path) }
        }
    }

    override fun validate(value: Number?): ValidationResult {
        if (value == null && (isNullable || isOptional)) return ValidationResult.Success

        val errors = rules.mapNotNull { it(value) }
        return if (errors.isEmpty()) ValidationResult.Success
        else ValidationResult.Failure(errors)
    }


    internal fun withField(name: String, parentPath: String = ""): NumberValidator {
        val newPath = if (parentPath.isEmpty()) name else "$parentPath.$name"
        return NumberValidator(name, newPath).also {
            it.rules.addAll(this.rules)
            it.isNullable = this.isNullable
            it.isOptional = this.isOptional
        }
    }

    private fun Number.compareTo(other: Number): Int {
        return when (this) {
            is Long if other is Long -> this.compareTo(other)
            is Int if other is Int -> this.compareTo(other)
            is Float if other is Float -> this.compareTo(other)
            else -> this.toDouble().compareTo(other.toDouble())
        }
    }
}