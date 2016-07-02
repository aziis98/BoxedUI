package com.aziis98.boxed.utils

import java.awt.Graphics2D
import java.awt.geom.Rectangle2D

// Copyright 2016 Antonio De Lucreziis

/**
 * if (value == targetValue) ifTrue else ifFalse
 */
fun <T> iff(value: T, targetValue: T, ifTrue: T, ifFalse: T = value) =
    if (value == targetValue) ifTrue else ifFalse

fun Graphics2D.drawStringCentered(string: String, x: Int, y: Int): Rectangle2D {
    val bounds = fontMetrics.getStringBounds(string, this)
    drawString(string, (x - bounds.width / 2).toInt(), y + (bounds.height / 2).toInt())
    return bounds
}

fun <T> T?.notNull(block: T.() -> Unit) {
    this?.block()
}

enum class Direction {
    Horizontal,
    Vertical
}

fun <T> List<T>.init() = filterIndexed { i, t -> i != lastIndex }

fun <T> List<T>.tail() = filterIndexed { i, t -> i != 0 }