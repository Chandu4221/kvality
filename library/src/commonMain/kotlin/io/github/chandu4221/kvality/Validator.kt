package io.github.chandu4221.kvality

interface Validator<T> {
    fun validate(value: T?): ValidationResult
}