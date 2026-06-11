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
implementation("io.github.chandu4221:kvality-core:3.3.0")
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
    val path: String     // "address.city" for nested objects
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
    .nullable()   // null is allowed
    .optional()   // skip validation if null
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
    .nullable()
    .optional()
    .custom { if (it?.toInt() == 42) "not the answer" else null }
```

---

## Boolean Validators

```kotlin
Kvality.boolean()
    .required()
    .isTrue()
    .isFalse()
    .nullable()
    .optional()
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
    .nullable()
    .optional()
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

### Nested Object Validation

```kotlin
val schema = kvality {
    field("name") { string().required() }
    field("address") {
        objectSchema {
            field("city") { string().required() }
            field("zip")  { string().length(6).required() }
        }
    }
}

val result = schema.validate(mapOf(
    "name" to "Chandu",
    "address" to mapOf(
        "city" to "",
        "zip"  to "123"
    )
))

// errors:
// address.city → "field is required"
// address.zip  → "must be exactly 6 characters"
```

Nullable nested object:

```kotlin
field("address") {
    objectSchema {
        field("city") { string().required() }
    }.nullable() // address can be null
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

Use typed accessor helpers for safe cross-field access:

```kotlin
val schema = kvality {
    field("password")        { string().required() }
    field("confirmPassword") { string().required() }
    validate("passwords do not match") { input ->
        input.getString("password") == input.getString("confirmPassword")
    }
}
```

Available helpers on `Map<String, Any?>`:

```kotlin
input.getString("field")
input.getInt("field")
input.getLong("field")
input.getDouble("field")
input.getBoolean("field")
input.getList("field")
input.getMap("field")
```

### Partial Schema (PATCH APIs)

```kotlin
// all fields become optional
val patchSchema = userSchema.partial()

patchSchema.validate(mapOf("name" to "Chandu")) // Success — only validates present fields
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

### Strict Mode

Reject any fields not defined in the schema:

```kotlin
val schema = kvality {
    field("name")  { string().required() }
    field("email") { string().email().required() }
}.strict()

val result = schema.validate(mapOf(
    "name"  to "Chandu",
    "email" to "chandu@example.com",
    "extra" to "not allowed"
))

// ValidationResult.Failure
// extra → "unknown field 'extra'" (code: schema.unknownField)
```
---

## Simple Error Map

For simple use cases, convert to `Map<String, List<String>>`:

```kotlin
val result = schema.validate(input)
val errors = result.toMap()
// { "email": ["must be a valid email address"] }
```

## Result Helpers

```kotlin
// errors for exact field
result.errorsFor("email")

// all errors under a nested path
result.errorsUnder("address")  // returns address.city, address.zip etc.

// quick boolean check
result.hasErrors("email")

// first error message for a field
result.firstErrorFor("email")

// first error object under a path
result.firstErrorUnder("address")

// path → messages map (useful for nested objects)
result.toPathMap()
// { "address.city": ["field is required"], "address.zip": ["must be exactly 6 characters"] }
```


---

## Changelog

| Version | Highlights |
|---------|-----------|
| 3.3.0 | Nested error extraction helpers — errorsUnder, hasErrors, firstErrorFor, toPathMap |
| 3.2.0 | Typed accessor helpers for safe cross-field validation |
| 3.1.0 | Strict mode — reject unknown fields |
| 3.0.1 | Bug fixes — nullable/optional flag propagation, numeric precision, regex safety |
| 3.0.0 | Nested object validation, nullable/optional distinction, partial schema |
| 2.0.0 | Structured errors with codes/paths, custom messages, list/enum validators, schema composition |
| 1.0.0 | Core validators, schema DSL, basic error model |

## License

Apache 2.0 — see [LICENSE](LICENSE)