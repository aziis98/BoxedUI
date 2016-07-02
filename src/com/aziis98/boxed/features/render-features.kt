package com.aziis98.boxed.features

import com.aziis98.boxed.Box
import com.aziis98.boxed.textures.*
import com.aziis98.boxed.utils.*
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
            g.drawNinePatchTexture(DefaultUI.menuBar, 0, 0, box.width, box.height)
        }
        register("menu-label") { box, g, el ->
            val text = el.getAttribute("text")
            val color = el.getAttribute("color").toColor() ?: Color.WHITE
            g.color = color
            val bounds = g.drawStringCentered(text, box.width / 2, box.height / 2 - 3)

            box.width = bounds.width.toInt() + 15
            box.constraintFlags[Box.WIDTH] = true
        }
        register("border") { box, g, el ->
            val color = el.getAttribute("color").toColor() ?: Color.WHITE
            g.color = color
            g.drawRect(0, 0, box.width, box.height)
        }
    }
}