package com.aziis98.boxed.templates

import com.aziis98.boxed.*
import com.aziis98.boxed.features.*
import com.aziis98.boxed.utils.*
import org.w3c.dom.Element

// Copyright 2016 Antonio De Lucreziis

object BoxTemplate : ITemplate {
    override fun BoxWindowBuilder.registerTemplates() {
        registerBoxTemplate("Box") { parent, element ->
            parent.box(
                left =      element["left"].toInteger(default = Box.ABSENT),
                right =     element["right"].toInteger(default = Box.ABSENT),
                top =       element["top"].toInteger(default = Box.ABSENT),
                bottom =    element["bottom"].toInteger(default = Box.ABSENT),
                width =     element["width"].toInteger(default = Box.ABSENT),
                height =    element["height"].toInteger(default = Box.ABSENT)
            ) {
                parseCommonAttributes(this, element)

                element.childNodes.forEach {
                    if (it.tagName == "Feature")
                        parseFeatures(this, it)
                    else
                        parseElement(this, it)
                }
            }
        }

        registerFeatureType("render") { parent, element ->
            parent.features += parent.render { g ->
                RenderRegistry.registry[element["by"]]!!(this, g, element)
            }
        }
        registerFeatureType("layout-equalized") { parent, element ->
            val attrDirection = element["direction"]

            parent.features += EqualizedLayout(parent,
                left = element["left"].toInteger(default = 0),
                right = element["right"].toInteger(default = 0),
                top = element["top"].toInteger(default = 0),
                bottom = element["bottom"].toInteger(default = 0),
                direction = attrDirection.toDirection(),
                gap = element["gap"].toInteger(default = 0)
            )
        }
        registerFeatureType("layout-flow") { parent, element ->
            parent.features += FlowLayout(parent,
                direction = element["direction"].toDirection(),
                gap = element["gap"].toInteger(default = 0)
            )
        }
    }

    fun parseCommonAttributes(box: Box, element: Element) {
        box.apply {
            zIndex = element["z-index"].toInteger(default = 0)
            element["id"].ifNotEmpty { id = it }
            element["tag"].ifNotEmpty { tags = it.split(" ").toHashSet() }

            // Event Linking
            element["onClick"].ifNotEmpty { attr ->
                events.on("mouse:click") {
                    events.broadcast(attr)
                }
            }
        }
    }
}