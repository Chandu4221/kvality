package io.github.chandu4221.kvality

class ObjectValidator internal constructor(
    private val schema: Schema,
    private val fieldName: String = "value",
    private val path: String = fieldName
) : Validator<Map<String, Any?>> {

    private var isNullable = false
    private var isOptional = false

    fun nullable(): ObjectValidator = apply { isNullable = true }
    fun optional(): ObjectValidator = apply { isOptional = true }

    override fun validate(value: Map<String, Any?>?): ValidationResult {
        if (value == null) {
            return if (isNullable || isOptional) ValidationResult.Success
            else ValidationResult.Failure(
                listOf(ValidationError(fieldName, "object.required", "field is required", path))
            )
        }

        val result = schema.validate(value, path)

        if (result is ValidationResult.Failure) {
            return result
        }

        return ValidationResult.Success
    }

    internal fun withField(name: String, parentPath: String = ""): ObjectValidator {
        val newPath = if (parentPath.isEmpty()) name else "$parentPath.$name"
        return ObjectValidator(schema, name, newPath).also {
            it.isNullable = isNullable
            it.isOptional = isOptional
        }
    }
}