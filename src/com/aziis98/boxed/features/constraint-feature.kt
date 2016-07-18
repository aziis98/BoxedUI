package com.aziis98.boxed.features

import com.aziis98.boxed.Box
import com.aziis98.boxed.utils.*

// Copyright 2016 Antonio De Lucreziis

/**
 *
 */
class EqualizedLayout(override val box: Box,
                      val left: Int, val right: Int, val top: Int, val bottom: Int,
                      val gap: Int = 0,
                      val direction: Direction = Direction.Horizontal) : ConstraintFeature {
    override fun updateConstraint() {
        val innerWidth = box.width - left - right
        val innerHeight = box.height - top - bottom

        val childrenCount = box.children.size

        when (direction) {
            Direction.Horizontal -> {
                val boxWidth = (innerWidth - (childrenCount - 1) * gap) / childrenCount

                box.children.forEachIndexed { i, box ->
                    box.setPosition(left = left + (boxWidth + gap) * i, width = boxWidth, top = top, bottom = bottom)
                }
            }
            Direction.Vertical -> {
                val boxHeight = (innerHeight - (childrenCount - 1) * gap) / childrenCount

                box.children.forEachIndexed { i, box ->
                    box.setPosition(top = top + (boxHeight + gap) * i, height = boxHeight, left = left, right = right)
                }
            }
        }
    }
}

class FlowLayout(override val box: Box, val direction: Direction, val gap: Int = 0) : ConstraintFeature {
    override fun updateConstraint() {
        box.children.toList().tail().zip(box.children.toList().init()).forEach { pair ->
            val (box, previous) = pair

            when(direction) {
                Direction.Horizontal -> {
                    box.left = previous.left + previous.width + gap
                    box.constraintFlags.set(Box.LEFT)
                }
                Direction.Vertical -> {
                    box.top = previous.top + previous.height + gap
                    box.constraintFlags.set(Box.TOP)
                }
            }

        }
    }
}