package com.aziis98.boxed.utils

// Copyright 2016 Antonio De Lucreziis

fun String.toNullableInt(): Int? {
    try {
        return this.toInt()
    } catch (e: NumberFormatException) {
        return null
    }
}