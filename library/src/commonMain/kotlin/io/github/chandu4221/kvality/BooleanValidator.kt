package io.github.chandu4221.kvality

class BooleanValidator internal constructor(
    private val fieldName: String = "value",
    private val path: String = fieldName
) : Validator<Boolean> {

    private val rules = mutableListOf<(Boolean?) -> ValidationError?>()

    fun required(message: String = "field is required"): BooleanValidator = apply {
        rules += { v ->
            if (v == null) ValidationError(fieldName, "boolean.required", message, path)
            else null
        }
    }

    fun isTrue(message: String = "must be true"): BooleanValidator = apply {
        rules += { v ->
            if (v != true) ValidationError(fieldName, "boolean.isTrue", message, path)
            else null
        }
    }

    fun isFalse(message: String = "must be false"): BooleanValidator = apply {
        rules += { v ->
            if (v != false) ValidationError(fieldName, "boolean.isFalse", message, path)
            else null
        }
    }

    private var isNullable = false
    private var isOptional = false

    fun nullable(): BooleanValidator = apply { isNullable = true }

    fun optional(): BooleanValidator = apply { isOptional = true }

    fun custom(code: String = "boolean.custom", fn: (Boolean?) -> String?): BooleanValidator = apply {
        rules += { v ->
            fn(v)?.let { ValidationError(fieldName, code, it, path) }
        }
    }

    override fun validate(value: Boolean?): ValidationResult {
        if (value == null && (isNullable || isOptional)) return ValidationResult.Success

        val errors = rules.mapNotNull { it(value) }
        return if (errors.isEmpty()) ValidationResult.Success
        else ValidationResult.Failure(errors)
    }

    internal fun withField(name: String, parentPath: String = ""): BooleanValidator {
        val newPath = if (parentPath.isEmpty()) name else "$parentPath.$name"
        return BooleanValidator(name, newPath).also { it.rules.addAll(this.rules) }
    }
}