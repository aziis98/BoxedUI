package com.aziis98.boxed.features

import com.aziis98.boxed.Box
import com.aziis98.boxed.utils.drawStringCentered
import org.w3c.dom.Element
import java.awt.*
import java.util.*

// Copyright 2016 Antonio De Lucreziis

class RenderizeFeature(override val box: Box, val func: Box.(Graphics2D) -> Unit) : RenderFeature {
    override fun render(g: Graphics2D) {
        box.func(g)
    }
}

fun Box.render(func: Box.(Graphics2D) -> Unit) = RenderizeFeature(this, func)

object RenderRegistry {
    val registry = HashMap<String, (Box, Graphics2D, Element) -> Unit>()

    fun register(id: String, func: (Box, Graphics2D, Element) -> Unit) {
        registry.put(id, func)
    }

    init {
        register("menu") { box, g, el ->

        }
        register("menu-label") { box, g, el ->
            val text = el.getAttribute("text")
            g.color = Color.WHITE
            g.drawStringCentered(text, box.width / 2, box.height / 2)
        }
    }
}