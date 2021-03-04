package com.dorotatomczak.oauth.user.repository.model

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "users")
data class User(
    @Id
    val username: String? = null,
    val password: String? = null,
    val enabled: Boolean? = null
)
