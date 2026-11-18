package com.uidmanager.app.clipboard

import com.uidmanager.app.overlay.OverlayService

object OverlayBridge {

    var onUidCaptured: (String) -> Unit = {}
    var onCookiesCaptured: (String) -> Unit = {}
    var onError: (String) -> Unit = {}

    fun bind(service: OverlayService) {
        onUidCaptured = { uid -> service.handleUid(uid) }
        onCookiesCaptured = { cookies -> service.handleCookies(cookies) }
        onError = { msg -> service.showToast(msg) }
    }

    fun unbind() {
        onUidCaptured = {}
        onCookiesCaptured = {}
        onError = {}
    }
}
