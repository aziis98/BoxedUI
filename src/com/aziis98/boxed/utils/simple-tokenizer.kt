package com.aziis98.boxed.utils

// Copyright 2016 Antonio De Lucreziis

fun standardGluer(a: Char, b: Char) : Boolean = a.isLetterOrDigit() && b.isLetterOrDigit()

fun tokenize(source: String, gluer: (Char, Char) -> Boolean = ::standardGluer) {
    /*

    [@, i, d, #, t, a, g]
            |
            |
            v
    [[@], [i, d], [#], [t, a, g]]

     */
}