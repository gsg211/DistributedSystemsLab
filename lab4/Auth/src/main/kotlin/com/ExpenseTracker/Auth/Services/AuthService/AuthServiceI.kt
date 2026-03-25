package com.ExpenseTracker.Auth.Services.AuthService

interface AuthServiceI {
    fun checkLoginInfo(firstname: String, password: String): Boolean
    fun createNewUser(userName: String, fullName: String, password: String): Boolean
}