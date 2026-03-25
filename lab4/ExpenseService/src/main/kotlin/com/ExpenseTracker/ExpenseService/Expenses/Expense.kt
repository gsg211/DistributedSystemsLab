package com.ExpenseTracker.ExpenseService.Expenses

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

enum class ExpenseType {
    UPKEEP, FOOD, FUN, SCHOOL, PERSONAL
}

@Entity
@Table(name = "EXPENSES")
data class Expense(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,

    val userName: String, // Legătura cu utilizatorul (din token)

    @Enumerated(EnumType.STRING)
    val type: ExpenseType,

    val cost: Double,

    val timestamp: LocalDateTime = LocalDateTime.now()
)