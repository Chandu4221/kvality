package io.github.chandu4221.kvality

class EnumValidator<T> internal constructor(
    private val allowedValues: Set<T>,
    private val fieldName: String = "value",
    private val path: String = fieldName
) : Validator<T> {

    private val rules = mutableListOf<(T?) -> ValidationError?>()

    fun required(message: String = "field is required"): EnumValidator<T> = apply {
        rules += { v ->
            if (v == null) ValidationError(fieldName, "enum.required", message, path)
            else null
        }
    }

    fun optional(): EnumValidator<T> = apply {
        rules.clear()
    }

    fun custom(code: String = "enum.custom", fn: (T?) -> String?): EnumValidator<T> = apply {
        rules += { v ->
            fn(v)?.let { ValidationError(fieldName, code, it, path) }
        }
    }

    override fun validate(value: T?): ValidationResult {
        val errors = mutableListOf<ValidationError>()

        rules.mapNotNull { it(value) }.forEach { errors.add(it) }

        if (value != null && !allowedValues.contains(value)) {
            errors.add(
                ValidationError(
                    field = fieldName,
                    code = "enum.invalid",
                    message = "must be one of: ${allowedValues.joinToString(", ")}",
                    path = path
                )
            )
        }

        return if (errors.isEmpty()) ValidationResult.Success
        else ValidationResult.Failure(errors)
    }

    internal fun withField(name: String, parentPath: String = ""): EnumValidator<T> {
        val newPath = if (parentPath.isEmpty()) name else "$parentPath.$name"
        return EnumValidator(allowedValues, name, newPath).also { it.rules.addAll(this.rules) }
    }
}