package com.dorotatomczak.oauth.user.controller

import com.dorotatomczak.oauth.user.exception.InvalidGoogleTokenException
import com.dorotatomczak.oauth.user.exception.UserAlreadyExistsException
import com.dorotatomczak.oauth.user.exception.UsernameValidationFailedException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class ErrorHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(UserAlreadyExistsException::class)
    fun handleConflict(ex: Exception, webRequest: WebRequest): ResponseEntity<Any> =
        handleExceptionInternal(ex, "User already exists", HttpHeaders(), HttpStatus.CONFLICT, webRequest)

    @ExceptionHandler(UsernameValidationFailedException::class)
    fun handleValidationError(ex: Exception, webRequest: WebRequest): ResponseEntity<Any> =
        handleExceptionInternal(ex, "Username not valid", HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest)

    @ExceptionHandler(InvalidGoogleTokenException::class)
    fun handleGoogleTokenException(ex: Exception, webRequest: WebRequest): ResponseEntity<Any> =
        handleExceptionInternal(ex, "Google token not valid", HttpHeaders(), HttpStatus.UNAUTHORIZED, webRequest)
}
