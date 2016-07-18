package com.aziis98.boxed.utils

import java.awt.Color
import javax.naming.directory.InvalidAttributesException

// Copyright 2016 Antonio De Lucreziis

fun String.toInteger(default: Int = 0): Int {
    try {
        return toInt()
    } catch (e: NumberFormatException) {
        return default
    }
}

fun String.toColor(): Color? {
    try {
        return Color.decode(this)
    } catch (e: NumberFormatException) {
        return null
    }
}

fun String.toDirection(): Direction = when (this) {
    "horizontal" -> Direction.Horizontal
    "vertical" -> Direction.Vertical
    else -> throw InvalidAttributesException("Invalid direction: $this")
}


fun String.ifNotEmpty(block: (String) -> Unit) {
    if (!isEmpty()) {
        block(this)
    }
}