package io.github.chandu4221.kvality

fun Map<String, Any?>.getString(key: String): String? = this[key] as? String

fun Map<String, Any?>.getInt(key: String): Int? = when (val v = this[key]) {
    is Int -> v
    is Number -> v.toInt()
    else -> null
}

fun Map<String, Any?>.getLong(key: String): Long? = when (val v = this[key]) {
    is Long -> v
    is Number -> v.toLong()
    else -> null
}

fun Map<String, Any?>.getDouble(key: String): Double? = when (val v = this[key]) {
    is Double -> v
    is Number -> v.toDouble()
    else -> null
}

fun Map<String, Any?>.getBoolean(key: String): Boolean? = this[key] as? Boolean

fun Map<String, Any?>.getList(key: String): List<*>? = this[key] as? List<*>

@Suppress("UNCHECKED_CAST")
fun Map<String, Any?>.getMap(key: String): Map<String, Any?>? =
    @Suppress("UNCHECKED_CAST")
    this[key] as? Map<String, Any?>