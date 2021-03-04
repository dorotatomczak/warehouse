package com.dorotatomczak.oauth.user.controller

import com.dorotatomczak.oauth.user.controller.model.UserFullInfo
import com.dorotatomczak.oauth.user.controller.model.UserRegisterRequest
import com.dorotatomczak.oauth.user.exception.UsernameValidationFailedException
import com.dorotatomczak.oauth.user.service.UserService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(
    val userService: UserService
) {
    private companion object {
        val USERNAME_PATTERN = Regex("\\w+@\\w+\\.\\w+")
    }

    @PostMapping("/register")
    fun registerUser(@RequestBody userRegisterRequest: UserRegisterRequest): UserFullInfo =
        if (!userRegisterRequest.username.contains(USERNAME_PATTERN))
            userService.saveUser(userRegisterRequest)
        else
            throw UsernameValidationFailedException(userRegisterRequest.username)

}
