package io.github.chandu4221.kvality

class SchemaBuilder internal constructor() {

    private val fields = mutableMapOf<String, Validator<Any>>()

    @Suppress("UNCHECKED_CAST")
    fun <T> field(name: String, block: Kvality.() -> Validator<T>) {
        fields[name] = Kvality.block() as Validator<Any>
    }

    internal fun build(): Schema = Schema(fields)
}

fun kvality(block: SchemaBuilder.() -> Unit): Schema {
    return SchemaBuilder().apply(block).build()
}