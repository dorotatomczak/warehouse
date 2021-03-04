package com.dorotatomczak.oauth.user.controller.model

data class UserRegisterRequest (
    val username: String,
    val password: String?,
    val enabled: Boolean,
    val groupName: String
)
