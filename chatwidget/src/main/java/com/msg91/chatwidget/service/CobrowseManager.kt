package com.msg91.chatwidget.service

object CobrowseManager {
    private const val COBROWSE_LICENSE = "FZBGaF9-Od0GEQ"

    fun registerForCobrowse(uuid: String) {
        try {
            // Initialize cobrowse with UUID
            // You'll need to add cobrowse-sdk-android dependency
            log("Registering for cobrowse with UUID: $uuid")
            // CobrowseIO.getInstance().license = COBROWSE_LICENSE
            // CobrowseIO.getInstance().customData = mapOf("device_id" to uuid)
            // CobrowseIO.getInstance().start()
        } catch (e: Exception) {
            ApiService.log("Error registering cobrowse: ${e.message}")
        }
    }

    private fun log(message: String) = ApiService.log(message)
}