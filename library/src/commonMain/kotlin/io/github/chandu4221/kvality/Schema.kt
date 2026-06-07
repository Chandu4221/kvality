package io.github.chandu4221.kvality

class Schema internal constructor(
    private val fields: Map<String, Validator<*>>,
    private val crossFieldValidators: MutableList<(Map<String, Any?>) -> ValidationError?> = mutableListOf()
) {
    fun validate(input: Map<String, Any?>, parentPath: String = ""): ValidationResult {
        val allErrors = mutableListOf<ValidationError>()

        fields.forEach { (fieldName, validator) ->
            val value = input[fieldName]
            val path = if (parentPath.isEmpty()) fieldName else "$parentPath.$fieldName"

            val result = when (validator) {
                is ObjectValidator -> {
                    @Suppress("UNCHECKED_CAST")
                    validator.withField(fieldName, parentPath).validate(value as? Map<String, Any?>)
                }

                else -> @Suppress("UNCHECKED_CAST") (validator as Validator<Any?>).validate(value)
            }

            if (result is ValidationResult.Failure) {
                result.errors.forEach { error ->
                    allErrors.add(
                        error.copy(
                            field = if (error.field == "value") fieldName else error.field,
                            path = if (error.path == "value") path else error.path
                        )
                    )
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
        val partialFields = fields.mapValues { (_, validator) ->
            when (validator) {
                is StringValidator -> validator.optional()
                is NumberValidator -> validator.optional()
                is BooleanValidator -> validator.optional()
                is ListValidator<*> -> validator.optional()
                is ObjectValidator -> validator.optional()
                else -> validator
            }
        }
        return Schema(partialFields.toMutableMap(), crossFieldValidators.toMutableList())
    }
}