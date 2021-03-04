package com.dorotatomczak.oauth.user.repository

import com.dorotatomczak.oauth.user.repository.model.GroupMembership
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GroupMembershipRepository : JpaRepository<GroupMembership, Int>
