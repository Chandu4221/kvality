package io.github.chandu4221.kvality

object Kvality {
    fun string(): StringValidator = StringValidator()
    fun number(): NumberValidator = NumberValidator()
    fun boolean(): BooleanValidator = BooleanValidator()
}