package com.dorotatomczak.oauth.user.exception

import com.dorotatomczak.oauth.user.repository.model.User

class UserAlreadyExistsException(user: User): Exception("User ${user.username} already exists :(")
