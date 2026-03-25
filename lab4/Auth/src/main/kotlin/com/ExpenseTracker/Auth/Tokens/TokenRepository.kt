package com.ExpenseTracker.Auth.Tokens

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface TokenRepository : JpaRepository<SessionToken, Int> {
    fun findByTokenString(tokenString: String): SessionToken?
    fun deleteByAssociatedName(username: String)
    fun deleteByTimestampBefore(threshold: LocalDateTime)
}