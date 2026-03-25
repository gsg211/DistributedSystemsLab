package com.ExpenseTracker.Auth.Controllers

import com.ExpenseTracker.Auth.Services.AuthService.AuthServiceI
import com.ExpenseTracker.Auth.Services.TokenService.TokenServiceI
import com.ExpenseTracker.Auth.Tokens.SessionToken
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController(
    private val authService: AuthServiceI,
    private val tokenService: TokenServiceI
) {

    data class LoginRequest(val firstname: String, val passwordhash: String)
    data class RegisterRequest(val firstname: String, val lastname: String, val passwordhash: String)

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<SessionToken> {
        return if (authService.checkLoginInfo(request.firstname, request.passwordhash)) {
            val token = tokenService.generateNewToken(request.firstname)
            ResponseEntity.ok(token)
        } else {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
    }

    @PostMapping("/register")
    fun postUser(@RequestBody request: RegisterRequest): ResponseEntity<String> {
        val success = authService.createNewUser(request.firstname, request.lastname, request.passwordhash)
        return if (success) {
            ResponseEntity.status(HttpStatus.CREATED).body("User created")
        } else {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already exists")
        }
    }

    @GetMapping("/validate")
    fun validateToken(@RequestParam tokenString: String): ResponseEntity<Boolean> {
        val isValid = tokenService.validateToken(tokenString)
        return if (isValid) {
            ResponseEntity.ok(true)
        } else {
            ResponseEntity.status(HttpStatus.FORBIDDEN).body(false)
        }
    }
}