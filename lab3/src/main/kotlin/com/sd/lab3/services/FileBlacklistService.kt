package com.sd.lab3.services

import com.sd.lab3.interfaces.BlacklistInterface
import org.springframework.stereotype.Service
import java.io.File

@Service
class FileBlacklistService(): BlacklistInterface {

    private val filepath="blacklist.txt"
    private val blacklist: Set<String> = try {
        File(filepath).readLines().map { it.trim() }.toSet()
    } catch (e: Exception) {
        emptySet()
    }

    override fun filter(input: String): String {
        return if (blacklist.contains(input)) {
            throw Exception("Nu ai voie $input")
        } else {
            input
        }
    }
}