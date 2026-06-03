package io.github.chandu4221.kvality

object Kvality {
    fun string(): StringValidator = StringValidator()
    fun number(): NumberValidator = NumberValidator()
    fun boolean(): BooleanValidator = BooleanValidator()
    fun <T> list(itemValidator: Validator<T>): ListValidator<T> = ListValidator(itemValidator)
    fun <T> oneOf(vararg values: T): EnumValidator<T> = EnumValidator(values.toSet())
    fun <T : Enum<T>> enum(values: Array<T>): EnumValidator<T> = EnumValidator(values.toSet())
}