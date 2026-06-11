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

    // get all errors for exact field name
    fun errorsFor(field: String): List<ValidationError> =
        if (this is Failure) errors.filter { it.field == field } else emptyList()

    // get all errors under a path prefix (e.g. "address" returns "address.city", "address.zip")
    fun errorsUnder(path: String): List<ValidationError> =
        if (this is Failure) errors.filter { it.path == path || it.path.startsWith("$path.") || it.path.startsWith("$path[") }
        else emptyList()

    // check if any errors exist for a field
    fun hasErrors(field: String): Boolean = errorsFor(field).isNotEmpty()

    // get first error message for a field
    fun firstErrorFor(field: String): String? = errorsFor(field).firstOrNull()?.message

    // get first error for a path
    fun firstErrorUnder(path: String): ValidationError? = errorsUnder(path).firstOrNull()

    // flat map of field -> messages
    fun toMap(): Map<String, List<String>> =
        if (this is Failure) errors.groupBy({ it.field }, { it.message }) else emptyMap()

    // flat map of path -> messages (useful for nested objects)
    fun toPathMap(): Map<String, List<String>> =
        if (this is Failure) errors.groupBy({ it.path }, { it.message }) else emptyMap()
}