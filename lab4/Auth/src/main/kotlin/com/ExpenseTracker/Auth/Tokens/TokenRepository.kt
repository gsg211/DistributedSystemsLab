package com.ExpenseTracker.Auth.Tokens

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TokenRepository : JpaRepository<SessionToken, Int> {
    fun findByTokenString(tokenString: String): SessionToken?
    fun deleteByAssociatedName(username: String)
}