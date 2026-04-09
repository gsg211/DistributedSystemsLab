package com.lab6.BookMicroService

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Lob


import jakarta.persistence.*

@Entity
@Table(name = "books")
data class Book(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val author: String = "",

    @Column(columnDefinition = "TEXT")
    val text: String = "",

    val writingDate: String = ""
)