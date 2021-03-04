package com.dorotatomczak.oauth.user.repository.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "group_authorities")
data class GroupAuthorities(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val group_id: Int? = null,
    val authority: String? = null
)
