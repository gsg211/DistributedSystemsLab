package com.ExpenseTracker.ExpenseService.Services

import com.ExpenseTracker.ExpenseService.Expenses.Expense
import com.ExpenseTracker.ExpenseService.Expenses.ExpenseRepository
import com.ExpenseTracker.ExpenseService.Expenses.ExpenseType
import org.springframework.stereotype.Service

@Service
class ExpensesService(private val expenseRepository: ExpenseRepository): ExpensesServiceI {


    override fun addNewExpense(userName: String, type: ExpenseType, cost: Double): Expense {
        val newExpense= Expense(userName=userName, type=type,cost=cost)
        return expenseRepository.save(newExpense)
    }

    override fun getAllExpenses(userName: String): List<Expense> {
        return expenseRepository.findByUserName(userName)
    }
}