package com.dorotatomczak.warehouseapp.data.model

data class RegisterRequest (
    val username: String,
    val password: String,
    val enabled: Boolean = true,
    val groupName: Group = Group.EMPLOYEES
)
