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

/**
 * Ex. "@idOfBox"
 * Ex. "@id#tag1#tag2"
 * Ex. "#aTag"
 * @param query A query on a single element in the tree
 */
fun stringToSingleQuery(query: String): (Box) -> Boolean {
    val tokens = tokenize(query)
    var id: String? = null
    val tags = mutableListOf<String>()

    tokens.init().zip(tokens.tail()).forEach { entry ->
        val (token, next) = entry

        when(token) {
            "@" -> id = next
            "#" -> tags.add(next)
        }
    }

    /*
    id == null    box.id == id    result
    F             T               T
    F             F               F
    T             T               T
    T             F               T
     */

    return { box ->
        // Applied boolean logic here
        (id == null || box.id == id) && box.tags.containsAll(tags)
    }
}

/**
 * @param query A query on the whole tree
 * @return A list of queries on the various levels of the queried tree
 */
fun stringToQuery(query: String): List<(Box) -> Boolean> {
    return query.split(" ").map { stringToSingleQuery(it) }
}

/**
 * For the query format see [stringToQuery]
 */
fun Box.query(predicates: List<(Box) -> Boolean>, collection: MutableCollection<Box> = mutableListOf<Box>()): Collection<Box> {
    if (predicates.size > 0) {
        val selected = children.filter(predicates.first())
        collection += selected
        selected.forEach { it.query(predicates.tail(), collection) }
    }
    return collection
}

fun Box.queryChildren(predicate: (Box) -> Boolean): Box {
    return children.first(predicate)
}

fun Box.queryAllChildren(predicate: (Box) -> Boolean) : Collection<Box> {
    return children.filter(predicate)
}