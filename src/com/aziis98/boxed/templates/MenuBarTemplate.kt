package com.aziis98.boxed.templates

import com.aziis98.boxed.*
import com.aziis98.boxed.features.*
import com.aziis98.boxed.utils.*
import org.w3c.dom.Element


// Copyright 2016 Antonio De Lucreziis

object MenuBarTemplate : ITemplate {
    override fun BoxWindowBuilder.registerTemplates() {

        registerBoxTemplate("MenuBar") { parent, element ->

            parseMenuBar(parent, element)

        }

    }

    fun parseMenuBar(parent: Box, element: Element) {
        parent.box(left = 0, right = 0, top = 0, height = 30) {
            BoxTemplate.parseCommonAttributes(this, element)

            features += render { g ->
                RenderRegistry.registry["menu"]!!(this, g, element)
            }

            features += FlowLayout(this, Direction.Horizontal, 1)

            element.childNodes.forEachIndexed { i, element ->
                parseMenuItem(this, element, i == 0)
            }
        }
    }

    fun parseMenuItem(parent: Box, element: Element, isFirst: Boolean) {
        assert(element.tagName == "MenuItem")

        parent.box(
            left = if (isFirst) 0 else Box.ABSENT,
            top = 0,
            height = 27) {

            features += render { g ->
                RenderRegistry.registry["menu-label"]!!(this, g, element)
            }

        }
    }
}