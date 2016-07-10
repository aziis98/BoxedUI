package com.aziis98.boxed

import com.aziis98.boxed.events.*
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
               override var height: Int = ABSENT,
               var id: String = "_noId_") : IContainer, Comparable<Box> {

    val constraintFlags = BitSet()
    var enabled = true
    var zIndex = 0

    var features: Collection<Feature> = ArrayList()
    var children = SortedList<Box>()

    var tags = HashSet<String>()
    val events = EventDispatcher()

    private var recomputeLayout = 10

    init {
        refreshConstraintFlags()

        events.parent = parent?.events

        events.afterDispatchAction = { eventName ->
            children.forEach {
                it.events.dispatch(eventName)
            }
        }

        events.onCancellable("mouse:click") {
            if (contains(Mouse.x, Mouse.y))
                EventResult.Continue
            else
                EventResult.Cancel
        }
    }

    val parent: Box?
        get() = containter as? Box

    val absoluteLeft: Int
        get() = left + (parent?.absoluteLeft ?: 0)

    val absoluteTop: Int
        get() = top + (parent?.absoluteTop ?: 0)

    fun contains(x: Int, y: Int): Boolean {
        val absTop = absoluteTop
        val absLeft = absoluteLeft
        return x in absLeft .. absLeft + width && y in absTop .. absTop + height
    }

    fun refreshConstraintFlags() {
        //@formatter:off
        constraintFlags.set(LEFT  , left   != ABSENT)
        constraintFlags.set(RIGHT , right  != ABSENT)
        constraintFlags.set(TOP   , top    != ABSENT)
        constraintFlags.set(BOTTOM, bottom != ABSENT)
        constraintFlags.set(WIDTH , width  != ABSENT)
        constraintFlags.set(HEIGHT, height != ABSENT)
        //@formatter:onCancellable
    }

    fun setPosition(left: Int = ABSENT, right: Int = ABSENT,
                    top: Int = ABSENT, bottom: Int = ABSENT,
                    width: Int = ABSENT,
                    height: Int = ABSENT) {
        this.left = left
        this.right = right
        this.top = top
        this.bottom = bottom
        this.width = width
        this.height = height

        refreshConstraintFlags()
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
                TOP.isAbsent() -> top = containter.height - bottom - height
                BOTTOM.isAbsent() -> bottom = containter.height - top - height
                HEIGHT.isAbsent() -> height = containter.height - top - bottom
            }

            recomputeLayout--
        }

        children.forEach(Box::updateLayout)
    }

    fun invalidateLayout() {
        recomputeLayout++

        children.forEach { it.invalidateLayout() }
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

    protected fun Int.isAbsent() = !constraintFlags.get(this)

    override fun toString(): String {
        return "Box(id='$id', left=$left, right=$right, top=$top, bottom=$bottom, width=$width, height=$height)"
    }

    override fun compareTo(other: Box): Int {
        return iff(compareValues(zIndex, other.zIndex), 0, 1)
    }

}