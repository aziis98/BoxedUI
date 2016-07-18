package com.aziis98.boxed.templates

import com.aziis98.boxed.*
import com.aziis98.boxed.features.*
import com.aziis98.boxed.textures.drawNinePatchTexture
import com.aziis98.boxed.utils.*
import org.w3c.dom.Element

// Copyright 2016 Antonio De Lucreziis

object ContextMenuTemplate : ITemplate {
    override fun BoxWindowBuilder.registerTemplates() {
        registerBoxTemplate("ContextMenu") { parent, element ->
            parent.box(left = 0, right = 0, top = 0, bottom = 0) {
                element.childNodes.forEach {
                    parseMenuDefinition(this, it)
                }
            }
        }
    }

    fun parseMenuDefinition(parent: Box, element: Element) {
        assert(element.tagName == "Menu")

        parent.box(left = 0, top = 0, width = 200, height = element.childNodes.length * 30) {
            BoxTemplate.parseCommonAttributes(this, element)

            features += FlowLayout(this, Direction.Vertical, 0)

            features += render { g ->
                g.drawNinePatchTexture(DefaultUI.contextMenu, 0, 0, width, height)
            }

            element.childNodes.forEach {
                parseMenuItem(this, it)
            }
        }
    }

    fun parseMenuItem(parent: Box, element: Element) {
        assert(element.tagName == "MenuItem")

        parent.box(left = 0, right = 0, height = 30) {
            features += render { g ->
                g.drawStringCentered(element["label"], width / 2, height / 2)
            }
        }
    }

}