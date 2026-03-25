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

    data class LoginRequest(val userName: String, val password: String)
    data class RegisterRequest(val userName: String, val fullName: String, val password: String)

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<SessionToken> {
        return if (authService.checkLoginInfo(request.userName, request.password)) {
            val token = tokenService.generateNewToken(request.userName)
            ResponseEntity.ok(token)
        } else {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
    }

    @PostMapping("/register")
    fun postUser(@RequestBody request: RegisterRequest): ResponseEntity<String> {
        val success = authService.createNewUser(request.userName, request.fullName, request.password)
        return if (success) {
            ResponseEntity.status(HttpStatus.CREATED).body("User created")
        } else {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already exists")
        }
    }

    @GetMapping("/validate")
    fun validateToken(@RequestParam tokenString: String): ResponseEntity<Pair<Boolean, String>> {
        val tokenResponse = tokenService.validateToken(tokenString)
        return if (tokenResponse.first) {
            ResponseEntity.ok(Pair(true,tokenResponse.second))
        } else {
            ResponseEntity.status(HttpStatus.FORBIDDEN).body(Pair(false," "))
        }
    }
}