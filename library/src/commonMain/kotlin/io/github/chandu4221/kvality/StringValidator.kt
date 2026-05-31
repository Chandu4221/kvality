package io.github.chandu4221.kvality

class StringValidator internal constructor() : Validator<String> {

    private val rules = mutableListOf<(String?) -> String?>()

    fun required(): StringValidator = apply {
        rules += { v -> if (v.isNullOrBlank()) "field is required" else null }
    }

    fun min(n: Int): StringValidator = apply {
        rules += { v -> if (v != null && v.length < n) "min length is $n characters" else null }
    }

    fun max(n: Int): StringValidator = apply {
        rules += { v -> if (v != null && v.length > n) "max length is $n characters" else null }
    }

    fun email(): StringValidator = apply {
        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        rules += { v -> if (v != null && !emailRegex.matches(v)) "must be a valid email address" else null }
    }

    fun url(): StringValidator = apply {
        val urlRegex = Regex("^(https?|ftp)://[^\\s/$.?#].[^\\s]*$")
        rules += { v -> if (v != null && !urlRegex.matches(v)) "must be a valid URL" else null }
    }

    fun regex(pattern: String): StringValidator = apply {
        val r = Regex(pattern)
        rules += { v -> if (v != null && !r.matches(v)) "invalid format" else null }
    }

    fun alphanum(): StringValidator = apply {
        val r = Regex("^[A-Za-z0-9]+$")
        rules += { v -> if (v != null && !r.matches(v)) "must be alphanumeric" else null }
    }

    fun custom(fn: (String?) -> String?): StringValidator = apply {
        rules += fn
    }

    override fun validate(value: String?): ValidationResult {
        val errors = rules.mapNotNull { it(value) }
        return if (errors.isEmpty()) ValidationResult.Success
        else ValidationResult.Failure(mapOf("value" to errors))
    }

}