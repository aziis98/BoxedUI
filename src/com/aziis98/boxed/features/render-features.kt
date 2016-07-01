package com.aziis98.boxed.features

import com.aziis98.boxed.Box
import java.awt.Graphics2D
import java.util.*

// Copyright 2016 Antonio De Lucreziis

class RenderizeFeature(override val box: Box, val func: Box.(Graphics2D) -> Unit) : RenderFeature {
    override fun render(g: Graphics2D) {
        box.func(g)
    }
}

fun Box.render(func: Box.(Graphics2D) -> Unit) = RenderizeFeature(this, func)

object RenderRegistry {
    val registry = HashMap<String, (Box, Graphics2D) -> Unit>()


}