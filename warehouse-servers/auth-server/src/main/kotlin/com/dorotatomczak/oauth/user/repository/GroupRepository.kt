package com.dorotatomczak.oauth.user.repository

import com.dorotatomczak.oauth.user.repository.model.Group
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GroupRepository : JpaRepository<Group, Int>
