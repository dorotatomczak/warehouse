package com.dorotatomczak.oauth.user.exception

class InvalidGoogleTokenException(token: String) : Exception("Given token $token is not valid :(")
