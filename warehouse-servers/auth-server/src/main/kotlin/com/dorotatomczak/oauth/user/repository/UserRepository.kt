package com.dorotatomczak.oauth.user.repository

import com.dorotatomczak.oauth.user.repository.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, String>
