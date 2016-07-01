package com.aziis98.boxed

import com.aziis98.boxed.features.*
import com.aziis98.boxed.utils.*
import java.awt.Graphics2D
import java.util.*
import kotlin.comparisons.compareValues

// Copyright 2016 Antonio De Lucreziis

open class Box(val containter: IContainer,
               var left: Int = ABSENT, var right: Int = ABSENT,
               var top: Int = ABSENT, var bottom: Int = ABSENT,
               override var width: Int = ABSENT,
               override var height: Int = ABSENT) : IContainer, Comparable<Box> {

    var enabled = true
    var zIndex = 0

    var features: List<Feature> = ArrayList()

    var children = SortedList<Box>()
    val tags     = HashSet<String>()

    private val constraintFlags = BitSet()
    private var recomputeLayout = 10

    init {
        constraintFlags.set(LEFT, left >= 0)
        constraintFlags.set(RIGHT, right >= 0)
        constraintFlags.set(TOP, top >= 0)
        constraintFlags.set(BOTTOM, bottom >= 0)
        constraintFlags.set(WIDTH, width >= 0)
        constraintFlags.set(HEIGHT, height >= 0)
    }

    fun render(g: Graphics2D) {
        if (!enabled) return

        tryRun<RenderFeature> { it.render(g) }

        children.forEach {
            g.translate(it.left, it.top)
            it.render(g)
            g.translate(-it.left, -it.top)
        }
    }

    fun updateLayout() {
        if (!enabled) return

        if (recomputeLayout > 0) {

            tryRun<ConstraintFeature> { it.updateConstraint() }

            when {
                LEFT.isAbsent() -> left = containter.width - right - width
                RIGHT.isAbsent() -> right = containter.width - left - width
                WIDTH.isAbsent() -> width = containter.width - left - right
            }

            when {
                TOP.isAbsent() -> top = containter.width - right - width
                BOTTOM.isAbsent() -> bottom = containter.width - left - width
                HEIGHT.isAbsent() -> height = containter.width - left - right
            }

            recomputeLayout--
        }

        children.forEach(Box::updateLayout)
    }


    companion object {
        const val LEFT = 0
        const val RIGHT = 1
        const val TOP = 2
        const val BOTTOM = 3
        const val WIDTH = 4
        const val HEIGHT = 5

        const val ABSENT = -1
    }

    fun Int.isAbsent() = !constraintFlags.get(this)

    override fun compareTo(other: Box): Int {
        return iff(compareValues(zIndex, other.zIndex), 0, 1)
    }

}