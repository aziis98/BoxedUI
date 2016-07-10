package com.aziis98.boxed.utils

import java.awt.Graphics2D
import java.awt.geom.Rectangle2D
import java.util.*

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

fun Graphics2D.drawCircle(x: Int, y: Int, radius: Int) {
    drawOval(x - radius - 1, y - radius - 1, radius * 2 + 1, radius * 2 + 1)
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

fun <T> Collection<T>.get(element: T) = contains(element)

fun <K, V> Map<K, V>.get(element: K) = containsKey(element)


fun <T> linkedListOf(vararg elements: T): LinkedList<T> {
    return LinkedList<T>().apply {
        addAll(elements)
    }
}