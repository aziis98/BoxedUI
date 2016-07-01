package com.aziis98.boxed.utils

import com.aziis98.boxed.Box
import com.aziis98.boxed.features.Feature

// Copyright 2016 Antonio De Lucreziis

inline fun <reified R : Feature> Box.tryRun(action: (R) -> Unit) {
    features.filterIsInstance<R>().forEach(action)
}

operator fun <T> Set<T>.get(element: T): Boolean {
    return contains(element)
}

fun Box.box(left: Int = -1,
            right: Int = -1,
            top: Int = -1,
            bottom: Int = -1,
            width: Int = -1,
            height: Int = -1,
            block: Box.() -> Unit): Box {

    return Box(this, left, right, top, bottom, width, height).apply {
        block()
        this@box.children.add(this)
    }
}

fun Box.queryChildren(predicate: (Box) -> Boolean): Box {
    return children.first(predicate)
}

fun Box.queryAllChildren(predicate: (Box) -> Boolean) : Collection<Box> {
    return children.filter(predicate)
}
