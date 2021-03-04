package com.dorotatomczak.oauth.user.exception

class UsernameValidationFailedException(username: String): Exception("Username $username is not valid.")
