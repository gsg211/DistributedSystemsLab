package com.ExpenseTracker.Auth.Services.AuthService

interface AuthServiceI {
    fun checkLoginInfo(firstname: String, passwordhash: String): Boolean
    fun createNewUser(firstname: String, lastname: String, password: String): Boolean
}