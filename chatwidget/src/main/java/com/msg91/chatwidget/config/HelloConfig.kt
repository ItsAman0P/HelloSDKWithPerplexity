package com.msg91.chatwidget.config

// HelloConfig.kt
data class HelloConfig(
    val widgetToken: String,
    val uniqueId: String? = null,
    val name: String? = null,
    val number: String? = null,
    val mail: String? = null
) {
    init {
        require(widgetToken.isNotBlank()) { "widgetToken cannot be empty" }
    }

    fun toMap(): Map<String, Any> {
        val map = mutableMapOf<String, Any>("widgetToken" to widgetToken)
        uniqueId?.let { if (it.isNotBlank()) map["unique_id"] = it }
        name?.let { if (it.isNotBlank()) map["name"] = it }
        number?.let { if (it.isNotBlank()) map["number"] = it }
        mail?.let { if (it.isNotBlank()) map["mail"] = it }
        return map
    }
}