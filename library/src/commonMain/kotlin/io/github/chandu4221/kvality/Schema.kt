package io.github.chandu4221.kvality

class Schema internal constructor(
    private val fields: Map<String, Validator<Any>>,
    private val crossFieldValidators: MutableList<(Map<String, Any?>) -> ValidationError?> = mutableListOf()
) {
    fun validate(input: Map<String, Any?>, parentPath: String = ""): ValidationResult {
        val allErrors = mutableListOf<ValidationError>()

        fields.forEach { (fieldName, validator) ->
            val value = input[fieldName]
            val path = if (parentPath.isEmpty()) fieldName else "$parentPath.$fieldName"

            @Suppress("UNCHECKED_CAST")
            val result = validator.validate(value)

            if (result is ValidationResult.Failure) {
                result.errors.forEach { error ->
                    allErrors.add(error.copy(field = fieldName, path = path))
                }
            }
        }

        crossFieldValidators.mapNotNull { it(input) }.forEach { allErrors.add(it) }

        return if (allErrors.isEmpty()) ValidationResult.Success
        else ValidationResult.Failure(allErrors)
    }

    fun extend(block: SchemaBuilder.() -> Unit): Schema {
        val newFields = fields.toMutableMap()
        val builder = SchemaBuilder().apply(block)
        newFields.putAll(builder.getFields())
        return Schema(newFields, crossFieldValidators.toMutableList())
    }

    fun pick(vararg fieldNames: String): Schema {
        val picked = fields.filter { it.key in fieldNames }
        return Schema(picked.toMutableMap())
    }

    fun omit(vararg fieldNames: String): Schema {
        val omitted = fields.filter { it.key !in fieldNames }
        return Schema(omitted.toMutableMap())
    }

    fun merge(other: Schema): Schema {
        val merged = fields.toMutableMap()
        merged.putAll(other.fields)
        return Schema(merged, crossFieldValidators.toMutableList())
    }

    fun partial(): Schema {
        return Schema(fields.toMutableMap())
    }
}