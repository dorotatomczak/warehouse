package com.dorotatomczak.oauth.user.controller.model

data class GoogleAuthRequest(
    val token: String = "",
    val groupName: String = "EMPLOYEES"
)
