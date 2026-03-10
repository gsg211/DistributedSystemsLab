package com.sd.lab3.Chain

interface Chainable<I, O> {
    fun proceed(input: I): O
}