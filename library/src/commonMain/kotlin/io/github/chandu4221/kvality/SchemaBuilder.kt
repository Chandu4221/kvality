package io.github.chandu4221.kvality

class SchemaBuilder internal constructor() {

    private val fields = mutableMapOf<String, Validator<Any>>()
    private val crossFieldValidators = mutableListOf<(Map<String, Any?>) -> ValidationError?>()

    @Suppress("UNCHECKED_CAST")
    fun <T> field(name: String, block: Kvality.() -> Validator<T>) {
        fields[name] = Kvality.block() as Validator<Any>
    }

    fun validate(message: String = "validation failed", fn: (Map<String, Any?>) -> Boolean): SchemaBuilder = apply {
        crossFieldValidators += { input ->
            if (!fn(input)) ValidationError("_schema", "schema.crossField", message, "_schema")
            else null
        }
    }

    internal fun getFields(): Map<String, Validator<Any>> = fields
    internal fun getCrossFieldValidators() = crossFieldValidators

    internal fun build(): Schema = Schema(fields, crossFieldValidators)
}

fun kvality(block: SchemaBuilder.() -> Unit): Schema {
    return SchemaBuilder().apply(block).build()
}