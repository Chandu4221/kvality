# Kvality

[![Maven Central](https://img.shields.io/maven-central/v/io.github.chandu4221/kvality-core.svg)](https://central.sonatype.com/artifact/io.github.chandu4221/kvality-core)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)

**Schema-first validation for Kotlin Multiplatform — fluent, composable, and production-ready.**

Kvality brings Joi-like validation to Kotlin. No annotations, no reflection, no boilerplate — just clean chainable rules and a powerful schema DSL that works across all KMP targets.

---

## Platforms

- ✅ Android
- ✅ iOS (Native)
- ✅ JVM / Desktop
- ✅ Kotlin Multiplatform

---

## Installation

```kotlin
implementation("io.github.chandu4221:kvality-core:2.0.0")
```

---

## Quick Start

```kotlin
// Single field
val result = Kvality.string().min(3).email().required().validate("test@example.com")

when (result) {
    is ValidationResult.Success -> println("Valid!")
    is ValidationResult.Failure -> result.errors.forEach { println(it) }
}

// Schema
val schema = kvality {
    field("name")  { string().min(3).required() }
    field("email") { string().email().required() }
    field("age")   { number().min(18).required() }
}

val result = schema.validate(mapOf(
    "name"  to "Jo",
    "email" to "not-an-email",
    "age"   to 15
))
```

---

## Error Model

Every validation failure returns a structured `ValidationError`:

```kotlin
data class ValidationError(
    val field: String,   // "email"
    val code: String,    // "string.email"
    val message: String, // "must be a valid email address"
    val path: String     // "user.email" for nested objects
)
```

Result type:

```kotlin
sealed class ValidationResult {
    object Success : ValidationResult()
    data class Failure(val errors: List<ValidationError>) : ValidationResult()
}

// Convenience helpers
result.isValid
result.errorsFor("email")
result.toMap() // Map<String, List<String>>
```

---

## String Validators

```kotlin
Kvality.string()
    .required()
    .min(3)
    .max(255)
    .email()
    .url()
    .uuid()
    .alpha()
    .alphanum()
    .lowercase()
    .uppercase()
    .length(10)
    .startsWith("prefix")
    .endsWith(".com")
    .contains("keyword")
    .regex("^[A-Z]+$")
    .custom { if (it != "secret") "must be secret" else null }
```

Custom error messages on any rule:

```kotlin
Kvality.string().min(3, "Name is too short").email("Invalid email format")
```

---

## Number Validators

```kotlin
Kvality.number()
    .required()
    .min(0)
    .max(100)
    .between(1, 10)
    .positive()
    .negative()
    .nonNegative()
    .nonPositive()
    .integer()
    .custom { if (it?.toInt() == 42) "not the answer" else null }
```

---

## Boolean Validators

```kotlin
Kvality.boolean()
    .required()
    .isTrue()
    .isFalse()
    .custom { if (it == null) "required" else null }
```

---

## List Validators

```kotlin
Kvality.list(Kvality.string().min(2))
    .required()
    .nonEmpty()
    .minItems(1)
    .maxItems(10)
```

Item-level errors include indexed paths:

```
tags[0] → "min length is 2 characters"
```

---

## Enum / OneOf Validators

```kotlin
// String values
Kvality.oneOf("ADMIN", "USER", "GUEST").validate("UNKNOWN")

// Kotlin enums
enum class Role { ADMIN, USER, GUEST }
Kvality.enum(Role.entries.toTypedArray()).validate(Role.ADMIN)
```

---

## Schema DSL

### Basic Schema

```kotlin
val userSchema = kvality {
    field("name")    { string().min(3).required() }
    field("email")   { string().email().required() }
    field("age")     { number().min(18).required() }
    field("website") { string().url() }
    field("role")    { oneOf("ADMIN", "USER") }
    field("tags")    { list(string().min(2)).nonEmpty() }
}
```

### Cross-field Validation

```kotlin
val schema = kvality {
    field("password")        { string().required() }
    field("confirmPassword") { string().required() }
    validate("passwords do not match") { input ->
        input["password"] == input["confirmPassword"]
    }
}
```

### Schema Composition

```kotlin
val baseSchema = kvality {
    field("name") { string().required() }
}

// Extend
val extendedSchema = baseSchema.extend {
    field("email") { string().email().required() }
}

// Pick
val pickSchema = userSchema.pick("name", "email")

// Omit
val omitSchema = userSchema.omit("password")

// Merge
val mergedSchema = schemaA.merge(schemaB)
```

---

## Simple Error Map

For simple use cases, convert to `Map<String, List<String>>`:

```kotlin
val result = schema.validate(input)
val errors = result.toMap()
// { "email": ["must be a valid email address"] }
```

---

## License

Apache 2.0 — see [LICENSE](LICENSE)