package com.dorotatomczak.oauth.user.controller.model

data class UserFullInfo (
    val username: String,
    val enabled: Boolean,
    val permissions: List<String>,
    val groupName: String
)
