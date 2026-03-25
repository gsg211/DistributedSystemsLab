package com.ExpenseTracker.Auth.Services.TokenService

import com.ExpenseTracker.Auth.Tokens.SessionToken


interface TokenServiceI {
    fun generateNewToken(username: String): SessionToken
    fun validateToken(tokenString: String): Pair<Boolean,String>
    fun checkInvalidTokens()
}