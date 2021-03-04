package com.dorotatomczak.oauth.user.controller

import com.dorotatomczak.oauth.user.controller.model.GoogleAuthRequest
import com.dorotatomczak.oauth.user.controller.model.UserRegisterRequest
import com.dorotatomczak.oauth.user.exception.InvalidGoogleTokenException
import com.dorotatomczak.oauth.user.service.UserService
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import org.springframework.core.env.Environment
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/google")
class GoogleController(
    val userService: UserService,
    val env: Environment,
    val bcryptPasswordEncoder: PasswordEncoder,
    val tokenCreator: TokenEndpoint,
) {

    private companion object {
        const val DEFAULT_APPCLIENT_PROPERTY_NAME = "principal-name.default"
        const val DEFAULT_APPCLIENT_PASSWORD_PROPERTY_NAME = "principal-password.default"
        const val DEFAULT_GRANT_TYPE = "password"
    }

    @PostMapping("/verify")
    fun verifyGoogleToken(@RequestBody requestData: GoogleAuthRequest): ResponseEntity<OAuth2AccessToken>? {
        val httpTransport = NetHttpTransport.Builder()
            .build()
        val jsonFactory = JacksonFactory()
        val tokenVerifier = GoogleIdTokenVerifier.Builder(httpTransport, jsonFactory)
            .setAudience(listOf(env.getProperty("google.client-id")))
            .build()

        tokenVerifier.verify(requestData.token)
            ?.let { googleIdToken ->
                val payload = googleIdToken.payload
                userService.getUser(payload.email)
                    ?: userService.saveUser(
                        UserRegisterRequest(
                            username = payload.email,
                            password = payload.subject,
                            enabled = true,
                            groupName = requestData.groupName
                        )
                    )

                return tokenCreator.postAccessToken(
                    UsernamePasswordAuthenticationToken(
                        User(
                            env.getProperty(DEFAULT_APPCLIENT_PROPERTY_NAME),
                            env.getProperty(DEFAULT_APPCLIENT_PASSWORD_PROPERTY_NAME),
                            true, true, true, true, listOf()
                        ),
                        null, listOf()
                    ),
                    mapOf(
                        "grant_type" to DEFAULT_GRANT_TYPE,
                        "username" to payload.email,
                        "password" to payload.subject
                    )
                )
            }

        throw InvalidGoogleTokenException(requestData.token)
    }
}
