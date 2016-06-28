package com.aziis98.boxed.utils

import java.util.*

// Copyright 2016 Antonio De Lucreziis

class SortedList<T : Comparable<T>>(comparator: Comparator<T> = Comparator.naturalOrder<T>()) : MutableCollection<T> {

    private val treeSet = TreeSet<T>(comparator)

    fun reSort() {
        val elements = treeSet.toList()

        treeSet.clear()
        treeSet.addAll(elements)
    }


    override val size: Int
        get() = treeSet.size

    override fun add(element: T) = treeSet.add(element)

    override fun addAll(elements: Collection<T>) = treeSet.addAll(elements)

    override fun clear() = treeSet.clear()

    override fun remove(element: T) = treeSet.remove(element)

    override fun removeAll(elements: Collection<T>) = treeSet.removeAll(elements)

    override fun retainAll(elements: Collection<T>) = treeSet.retainAll(elements)

    override fun contains(element: T) = treeSet.contains(element)

    override fun containsAll(elements: Collection<T>) = treeSet.containsAll(elements)

    override fun isEmpty() = treeSet.isEmpty()

    override fun iterator() = treeSet.iterator()

}