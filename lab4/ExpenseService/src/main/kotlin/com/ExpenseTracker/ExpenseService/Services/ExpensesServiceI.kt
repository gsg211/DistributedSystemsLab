package com.ExpenseTracker.ExpenseService.Services

import com.ExpenseTracker.ExpenseService.Expenses.Expense
import com.ExpenseTracker.ExpenseService.Expenses.ExpenseType

interface ExpensesServiceI {
    fun addNewExpense(userName: String, type: ExpenseType, cost: Double): Expense
    fun getAllExpenses(userName: String): List<Expense>
}