package com.dorotatomczak.oauth.user.service

import com.dorotatomczak.oauth.user.controller.model.UserFullInfo
import com.dorotatomczak.oauth.user.controller.model.UserRegisterRequest
import com.dorotatomczak.oauth.user.exception.UserAlreadyExistsException
import com.dorotatomczak.oauth.user.repository.GroupAuthoritiesRepository
import com.dorotatomczak.oauth.user.repository.GroupMembershipRepository
import com.dorotatomczak.oauth.user.repository.GroupRepository
import com.dorotatomczak.oauth.user.repository.UserRepository
import com.dorotatomczak.oauth.user.repository.model.Group
import com.dorotatomczak.oauth.user.repository.model.GroupAuthorities
import com.dorotatomczak.oauth.user.repository.model.GroupMembership
import com.dorotatomczak.oauth.user.repository.model.User
import org.springframework.data.domain.Example
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    val bcryptPasswordEncoder: PasswordEncoder,
    val userRepository: UserRepository,
    val groupRepository: GroupRepository,
    val groupMembershipRepository: GroupMembershipRepository,
    val groupAuthoritiesRepository: GroupAuthoritiesRepository,
) {
    fun saveUser(userRequest: UserRegisterRequest): UserFullInfo {
        val group: Group = groupRepository.findOne(Example.of(Group(groupName = userRequest.groupName)))
            .orElseThrow { Exception() }

        val newUser = User(
            username = userRequest.username,
            password = bcryptPasswordEncoder.encode(userRequest.password),
            enabled = userRequest.enabled
        )

        getUser(userRequest.username)?.let {
            throw UserAlreadyExistsException(it)
        }

        val savedUser = userRepository.save(newUser)

        val groupMembership = GroupMembership(
            username = savedUser.username!!,
            group_id = group.id!!
        )

        groupMembershipRepository.save(groupMembership)

        return getUserInfo(savedUser.username, group.groupName!!)
    }


    fun getUser(username: String): User? = userRepository.findByIdOrNull(username)

    fun getUserInfo(username: String, groupName: String): UserFullInfo {
        val user = userRepository.findById(username).orElseThrow { Exception() }
        val group = groupRepository.findOne(Example.of(Group(groupName = groupName)))
            .orElseThrow { throw Exception() }
        val groupAuthorities = groupAuthoritiesRepository.findAll(
            Example.of(GroupAuthorities(group_id = group.id))
        ).map { it.authority!! }

        return UserFullInfo(
            username = user.username!!,
            enabled = user.enabled!!,
            groupName = group.groupName!!,
            permissions = groupAuthorities
        )
    }
}
