package com.dorotatomczak.oauth.user.repository

import com.dorotatomczak.oauth.user.repository.model.GroupAuthorities
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GroupAuthoritiesRepository : JpaRepository<GroupAuthorities, Int>
