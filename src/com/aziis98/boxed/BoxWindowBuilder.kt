package com.aziis98.boxed

import com.aziis98.boxed.templates.BoxTemplate
import com.aziis98.boxed.utils.*
import org.w3c.dom.Element
import java.util.*

// Copyright 2016 Antonio De Lucreziis

class BoxWindowBuilder {

    private val boxTemplates = HashMap<String, (parent: Box, Element) -> Unit>()
    private val featureTypes = HashMap<String, (parent: Box, Element) -> Unit>()

    init {
        BoxTemplate.apply { registerTemplates() }
    }

    fun registerBoxTemplate(name: String, templater: (parent: Box, Element) -> Unit) {
        boxTemplates.put(name, templater)
    }

    fun registerFeatureType(name: String, templater: (parent: Box, Element) -> Unit) {
        featureTypes.put(name, templater)
    }

    fun parseFromXml(window: Element): BoxWindow {
        assert(window.tagName == "Window")

        return BoxWindow().apply {
            title = window["title"]
            width = window["width"].toInteger(default = 640)
            height = window["height"].toInteger(default = 480)

            window.childNodes.forEach {
                parseElement(rootUi, it)
            }
        }
    }

    fun parseElement(parent: Box, element: Element) {
        boxTemplates[element.tagName]?.invoke(parent, element) ?: println("Jumped unknown box template with name: '${element.tagName}'")
    }

    fun parseFeatures(parent: Box, element: Element) {
        featureTypes[element["type"]]!!(parent, element)
    }

}