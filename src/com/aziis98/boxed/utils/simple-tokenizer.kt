package com.aziis98.boxed.utils

// Copyright 2016 Antonio De Lucreziis

fun standardGluer(a: Char, b: Char) : Boolean = a.isLetterOrDigit() && b.isLetterOrDigit()

fun tokenize(source: String, gluer: (Char, Char) -> Boolean = ::standardGluer): List<String> {
    return tokenize(gluer, source.toList()).map { String(it.toCharArray()) }
}

fun <T> tokenize(predicate: (T, T) -> Boolean,
                 list: List<T>,
                 accumulator: MutableList<MutableList<T>> = mutableListOf(mutableListOf(list.first()))): MutableList<MutableList<T>> {

    if (list.size > 1) {
        val tail = list.tail()

        val char1 = list.first()
        val char2 = tail.first()

        if (!predicate(char1, char2)) {
            accumulator.add(mutableListOf())
        }
        accumulator.last().add(char2)

        tokenize(predicate, tail, accumulator)
    }

    return accumulator
}