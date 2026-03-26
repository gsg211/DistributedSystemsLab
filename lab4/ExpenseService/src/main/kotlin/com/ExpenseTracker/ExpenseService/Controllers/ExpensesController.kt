package com.ExpenseTracker.ExpenseService.Controllers

import com.ExpenseTracker.ExpenseService.Expenses.Expense
import com.ExpenseTracker.ExpenseService.Expenses.ExpenseType

import com.ExpenseTracker.ExpenseService.Services.ExpensesServiceI

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate

@RestController
class ExpensesController(
    private val expensesService: ExpensesServiceI,
    private val restTemplate: RestTemplate
) {
    private val AUTH_VALIDATE_URL="http://auth-service:8080/validate?tokenString="
    data class ExpenseRequest(val type: String, val cost:Double)

    @GetMapping("/expenses")
    fun getAllExpenses(@RequestHeader("Authorization") token: String): ResponseEntity<List<Expense>>{
        val (isValid,userName) = validateSessionToken(token)
        if(!isValid){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

        return ResponseEntity.ok(expensesService.getAllExpenses(userName))
    }


    @PostMapping("/expenses")
    fun addExpense(
        @RequestHeader("Authorization") token: String,
        @RequestBody request: ExpenseRequest):  ResponseEntity<Any>{
        val (isValid,userName) = validateSessionToken(token)
        if(!isValid){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        val typeEnum = getExpenseType(request.type)
            ?: return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Expense Type: ${request.type}")

        expensesService.addNewExpense(userName, typeEnum, request.cost)
        return ResponseEntity.ok("done")
    }


    fun validateSessionToken(tokenString: String): Pair<Boolean, String> {
        return try {
            val response = restTemplate.getForObject("$AUTH_VALIDATE_URL$tokenString", Map::class.java)

            val isValid = response?.get("first") as? Boolean ?: false
            val username = response?.get("second") as? String ?: ""
            Pair(isValid, username)
        } catch (e: Exception) {
            Pair(false, "")
        }
    }

    private fun getExpenseType(typeStr: String): ExpenseType? {
        return try {
            ExpenseType.valueOf(typeStr.uppercase())
        } catch (e: Exception) {
            null
        }
    }

}