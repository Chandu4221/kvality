package io.github.chandu4221.kvality

class Schema internal constructor(
    private val fields: Map<String, Validator<Any>>
) {
    fun validate(input: Map<String, Any?>): ValidationResult {
        val allErrors = mutableMapOf<String, List<String>>()

        fields.forEach { (fieldName, validator) ->
            val value = input[fieldName]
            val result = validator.validate(value)
            if (result is ValidationResult.Failure) {
                val errors = result.errors["value"] ?: emptyList()
                if (errors.isNotEmpty()) {
                    allErrors[fieldName] = errors
                }
            }
        }

        return if (allErrors.isEmpty()) ValidationResult.Success
        else ValidationResult.Failure(allErrors)
    }
}