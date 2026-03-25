package com.ExpenseTracker.Auth.Tokens

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "TOKENS")
data class SessionToken(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,
    val associatedName: String,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val tokenString: String = UUID.randomUUID().toString()
) {
    fun isExpired(): Boolean {
        return timestamp.plusMinutes(15).isBefore(LocalDateTime.now())
    }
}