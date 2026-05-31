package io.github.chandu4221.kvality

sealed class ValidationResult {
    object Success : ValidationResult()
    data class Failure(
        val errors: Map<String, List<String>>
    ) : ValidationResult()

    val isValid: Boolean get() = this is Success
}