package com.ExpenseTracker.Auth.Services.AuthService

import com.ExpenseTracker.Auth.Users.User
import com.ExpenseTracker.Auth.Users.UserRepository
import org.springframework.stereotype.Service

@Service
class AuthService(private val userRepository: UserRepository) : AuthServiceI {

    override fun checkLoginInfo(firstname: String, passwordhash: String): Boolean {
        val user = userRepository.findByUsername(firstname)
        return user?.passwordhash == passwordhash
    }

    override fun createNewUser(firstname: String, lastname: String, passwordhash: String): Boolean {
        if (userRepository.findByUsername(firstname) != null) return false
        userRepository.save(User(username = firstname, fullname = lastname, passwordhash = passwordhash))
        return true
    }
}
