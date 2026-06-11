package io.github.chandu4221.kvality

class StringValidator internal constructor(
    private val fieldName: String = "value",
    private val path: String = fieldName
) : Validator<String> {

    private val rules = mutableListOf<(String?) -> ValidationError?>()

    fun required(message: String = "field is required"): StringValidator = apply {
        rules += { v ->
            if (v.isNullOrBlank()) ValidationError(fieldName, "string.required", message, path)
            else null
        }
    }

    fun min(n: Int, message: String = "min length is $n characters"): StringValidator = apply {
        rules += { v ->
            if (v != null && v.length < n) ValidationError(fieldName, "string.min", message, path)
            else null
        }
    }

    fun max(n: Int, message: String = "max length is $n characters"): StringValidator = apply {
        rules += { v ->
            if (v != null && v.length > n) ValidationError(fieldName, "string.max", message, path)
            else null
        }
    }

    fun email(message: String = "must be a valid email address"): StringValidator = apply {
        val regex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        rules += { v ->
            if (v != null && !regex.matches(v)) ValidationError(fieldName, "string.email", message, path)
            else null
        }
    }

    fun url(message: String = "must be a valid URL"): StringValidator = apply {
        val regex = Regex("^(https?|ftp)://[^\\s/$.?#].[^\\s]*$")
        rules += { v ->
            if (v != null && !regex.matches(v)) ValidationError(fieldName, "string.url", message, path)
            else null
        }
    }

    fun regex(pattern: String, message: String = "invalid format"): StringValidator = apply {
        val r = try {
            Regex(pattern)
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid regex pattern: $pattern", e)
        }
        rules += { v ->
            if (v != null && !r.matches(v)) ValidationError(fieldName, "string.regex", message, path)
            else null
        }
    }

    fun alphanum(message: String = "must be alphanumeric"): StringValidator = apply {
        val r = Regex("^[A-Za-z0-9]+$")
        rules += { v ->
            if (v != null && !r.matches(v)) ValidationError(fieldName, "string.alphanum", message, path)
            else null
        }
    }

    fun alpha(message: String = "must contain only letters"): StringValidator = apply {
        val r = Regex("^[A-Za-z]+$")
        rules += { v ->
            if (v != null && !r.matches(v)) ValidationError(fieldName, "string.alpha", message, path)
            else null
        }
    }

    fun uuid(message: String = "must be a valid UUID"): StringValidator = apply {
        val r = Regex("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")
        rules += { v ->
            if (v != null && !r.matches(v)) ValidationError(fieldName, "string.uuid", message, path)
            else null
        }
    }

    fun startsWith(prefix: String, message: String = "must start with '$prefix'"): StringValidator = apply {
        rules += { v ->
            if (v != null && !v.startsWith(prefix)) ValidationError(fieldName, "string.startsWith", message, path)
            else null
        }
    }

    fun endsWith(suffix: String, message: String = "must end with '$suffix'"): StringValidator = apply {
        rules += { v ->
            if (v != null && !v.endsWith(suffix)) ValidationError(fieldName, "string.endsWith", message, path)
            else null
        }
    }

    fun contains(substring: String, message: String = "must contain '$substring'"): StringValidator = apply {
        rules += { v ->
            if (v != null && !v.contains(substring)) ValidationError(fieldName, "string.contains", message, path)
            else null
        }
    }

    fun lowercase(message: String = "must be lowercase"): StringValidator = apply {
        rules += { v ->
            if (v != null && v != v.lowercase()) ValidationError(fieldName, "string.lowercase", message, path)
            else null
        }
    }

    fun uppercase(message: String = "must be uppercase"): StringValidator = apply {
        rules += { v ->
            if (v != null && v != v.uppercase()) ValidationError(fieldName, "string.uppercase", message, path)
            else null
        }
    }

    fun length(n: Int, message: String = "must be exactly $n characters"): StringValidator = apply {
        rules += { v ->
            if (v != null && v.length != n) ValidationError(fieldName, "string.length", message, path)
            else null
        }
    }

    private var isNullable = false
    private var isOptional = false

    fun nullable(): StringValidator = apply { isNullable = true }

    fun optional(): StringValidator = apply { isOptional = true }

    fun custom(code: String = "string.custom", fn: (String?) -> String?): StringValidator = apply {
        rules += { v ->
            fn(v)?.let { ValidationError(fieldName, code, it, path) }
        }
    }

    override fun validate(value: String?): ValidationResult {
        if (value == null && (isNullable || isOptional)) return ValidationResult.Success

        val errors = rules.mapNotNull { it(value) }
        return if (errors.isEmpty()) ValidationResult.Success
        else ValidationResult.Failure(errors)
    }

    internal fun withField(name: String, parentPath: String = ""): StringValidator {
        val newPath = if (parentPath.isEmpty()) name else "$parentPath.$name"
        return StringValidator(name, newPath).also {
            it.rules.addAll(this.rules)
            it.isNullable = this.isNullable
            it.isOptional = this.isOptional
        }
    }
}