package io.github.chandu4221.kvality

data class ValidationError(
    val field: String,
    val code: String,
    val message: String,
    val path: String = field
)

sealed class ValidationResult {
    object Success : ValidationResult()
    data class Failure(
        val errors: List<ValidationError>
    ) : ValidationResult()

    val isValid: Boolean get() = this is Success

    // get errors for a specific field
    fun errorsFor(field: String): List<ValidationError> =
        if (this is Failure) errors.filter { it.field == field } else emptyList()

    // get flat map of field -> messages (for simple use cases)
    fun toMap(): Map<String, List<String>> =
        if (this is Failure) errors.groupBy({ it.field }, { it.message }) else emptyMap()

}