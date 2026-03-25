package com.ExpenseTracker.Auth.Services.AuthService

import com.ExpenseTracker.Auth.CryptoClient
import com.ExpenseTracker.Auth.Users.User
import com.ExpenseTracker.Auth.Users.UserRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val cryptoClient: CryptoClient
) : AuthServiceI {

    override fun checkLoginInfo(firstname: String, password: String): Boolean {
        val user = userRepository.findByUsername(firstname) ?: return false

        val salt = user.passwordsalt

        val saltedInput = password + salt

        val hashedInput = cryptoClient.getHash(saltedInput)

        return user.passwordhash == hashedInput
    }

    override fun createNewUser(userName: String, fullName: String, password: String): Boolean {
        if (userRepository.findByUsername(userName) != null) return false

        val salt = UUID.randomUUID().toString().substring(0, 8)

        val saltedPassword = password + salt

        val hashedPassword = cryptoClient.getHash(saltedPassword)

        val newUser = User(
            username = userName,
            fullname = cryptoClient.encrypt(fullName),
            passwordhash = hashedPassword,
            passwordsalt = salt
        )
        userRepository.save(newUser)
        return true
    }
}