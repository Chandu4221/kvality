# Kvality

[![Maven Central](https://img.shields.io/maven-central/v/io.github.chandu4221/kvality-core.svg)](https://central.sonatype.com/artifact/io.github.chandu4221/kvality-core)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)

**Schema-first validation for Kotlin Multiplatform — fluent, composable, and easy to use.**

Kvality brings Joi-like validation to Kotlin. No annotations, no reflection, no boilerplate — just clean chainable rules and a powerful schema DSL.

---

## Platforms

- ✅ Android
- ✅ iOS (Native)
- ✅ JVM / Desktop
- ✅ Kotlin Multiplatform

---

## Installation

Add to your `build.gradle.kts`:

```kotlin
implementation("io.github.chandu4221:kvality-core:1.0.0")
```

---

## Usage

### Single Field Validation

```kotlin
// String
val result = Kvality.string().min(3).max(255).email().required().validate("test@example.com")

// Number
val result = Kvality.number().min(18).required().validate(25)

// Boolean
val result = Kvality.boolean().isTrue().validate(true)

// Check result
when (result) {
    is ValidationResult.Success -> println("Valid!")
    is ValidationResult.Failure -> println(result.errors)
}
```

### Schema DSL (Object Validation)

```kotlin
val userSchema = kvality {
    field("firstname") { string().min(3).max(255).required() }
    field("email")     { string().email().required() }
    field("age")       { number().min(18).required() }
    field("website")   { string().url() }
}

val result = userSchema.validate(mapOf(
    "firstname" to "Jo",
    "email"     to "not-an-email",
    "age"       to 15
))

// ValidationResult.Failure(
//   errors = {
//     "firstname": ["min length is 3 characters"],
//     "email":     ["must be a valid email address"],
//     "age":       ["must be at least 18"]
//   }
// )
```

---

## Available Validators

### String
| Method | Description |
|--------|-------------|
| `.required()` | Not null, not blank |
| `.min(n)` | Minimum character length |
| `.max(n)` | Maximum character length |
| `.email()` | Valid email format |
| `.url()` | Valid URL format |
| `.regex(pattern)` | Matches regex pattern |
| `.alphanum()` | Only letters and numbers |
| `.custom(fn)` | Custom validator lambda |

### Number
| Method | Description |
|--------|-------------|
| `.required()` | Not null |
| `.min(n)` | Minimum value |
| `.max(n)` | Maximum value |
| `.positive()` | Value > 0 |
| `.negative()` | Value < 0 |
| `.integer()` | Whole numbers only |
| `.custom(fn)` | Custom validator lambda |

### Boolean
| Method | Description |
|--------|-------------|
| `.required()` | Not null |
| `.isTrue()` | Must be true |
| `.isFalse()` | Must be false |
| `.custom(fn)` | Custom validator lambda |

---

## Error Shape

Errors are returned as `Map<String, List<String>>` — field name to list of all failures:

```json
{
  "firstname": ["min length is 3 characters"],
  "email":     ["must be a valid email address"],
  "age":       ["must be at least 18"]
}
```

---

## License

Apache 2.0 — see [LICENSE](LICENSE)