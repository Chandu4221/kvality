package io.github.chandu4221.kvality

class ListValidator<T> internal constructor(
    private val itemValidator: Validator<T>,
    private val fieldName: String = "value",
    private val path: String = fieldName
) : Validator<List<T>> {

    private val rules = mutableListOf<(List<T>?) -> ValidationError?>()

    fun required(message: String = "field is required"): ListValidator<T> = apply {
        rules += { v ->
            if (v == null) ValidationError(fieldName, "list.required", message, path)
            else null
        }
    }

    fun minItems(n: Int, message: String = "must have at least $n items"): ListValidator<T> = apply {
        rules += { v ->
            if (v != null && v.size < n) ValidationError(fieldName, "list.minItems", message, path)
            else null
        }
    }

    fun maxItems(n: Int, message: String = "must have at most $n items"): ListValidator<T> = apply {
        rules += { v ->
            if (v != null && v.size > n) ValidationError(fieldName, "list.maxItems", message, path)
            else null
        }
    }

    fun nonEmpty(message: String = "must not be empty"): ListValidator<T> = apply {
        rules += { v ->
            if (v != null && v.isEmpty()) ValidationError(fieldName, "list.nonEmpty", message, path)
            else null
        }
    }

    private var isNullable = false
    private var isOptional = false

    fun nullable(): ListValidator<T> = apply { isNullable = true }

    fun optional(): ListValidator<T> = apply { isOptional = true }

    override fun validate(value: List<T>?): ValidationResult {
        if (value == null && (isNullable || isOptional)) return ValidationResult.Success

        val errors = mutableListOf<ValidationError>()

        rules.mapNotNull { it(value) }.forEach { errors.add(it) }

        value?.forEachIndexed { index, item ->
            val itemPath = "$path[$index]"
            val result = itemValidator.validate(item)
            if (result is ValidationResult.Failure) {
                result.errors.forEach { error ->
                    errors.add(error.copy(path = itemPath))
                }
            }
        }

        return if (errors.isEmpty()) ValidationResult.Success
        else ValidationResult.Failure(errors)
    }

    internal fun withField(name: String, parentPath: String = ""): ListValidator<T> {
        val newPath = if (parentPath.isEmpty()) name else "$parentPath.$name"
        return ListValidator(itemValidator, name, newPath).also { it.rules.addAll(this.rules) }
    }
}