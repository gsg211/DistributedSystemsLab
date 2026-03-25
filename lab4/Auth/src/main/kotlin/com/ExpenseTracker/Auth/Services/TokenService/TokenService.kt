package com.ExpenseTracker.Auth.Services.TokenService

import com.ExpenseTracker.Auth.Tokens.SessionToken
import com.ExpenseTracker.Auth.Tokens.TokenRepository
import jakarta.transaction.Transactional
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class TokenService(private val tokenRepository: TokenRepository) : TokenServiceI {

    @Transactional
    override fun generateNewToken(username: String): SessionToken {
        tokenRepository.deleteByAssociatedName(username)
        return tokenRepository.save(SessionToken(associatedName = username))
    }

    override fun validateToken(tokenString: String): Boolean {
        val token = tokenRepository.findByTokenString(tokenString)
        return token != null && !token.isExpired()
    }

    @Scheduled(fixedRate = 3600000)
    override fun checkInvalidTokens() {
        val allTokens = tokenRepository.findAll()
        val expired = allTokens.filter { it.isExpired() }
        tokenRepository.deleteAll(expired)
    }
}