package com.ExpenseTracker.ExpenseService.Expenses

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ExpenseRepository: JpaRepository<Expense, Int>  {
    fun findByUserName(userName: String): List<Expense>
}