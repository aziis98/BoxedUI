package com.aziis98.tests

import com.aziis98.boxed.*
import com.aziis98.boxed.features.RenderRegistry
import com.aziis98.boxed.utils.drawStringCentered
import org.junit.Test
import printfRec
import java.awt.Color
import java.nio.file.Paths

// Copyright 2016 Antonio De Lucreziis

internal class BoxWindowTest {
    @Test
    fun testTokenizer() {

    }
}

const val loremIpsum = "Lorem ipsum dolor sit amet, consectetur adipisci elit"

fun main(args: Array<String>) {
    RenderRegistry.register("latin-analyzer") { box, g, el ->
        g.color = Color.BLACK
        g.font = DefaultUI.standardFontMedium
        g.drawStringCentered(loremIpsum, box.width / 2, box.height / 2)
    }

    BoxWindow.fromXmlTemplate(Paths.get("res/test-window.xml")).apply {
        rootUi.apply {
            events.on("menu-file") {
                println("Show the File menu")
            }
        }

        bootstrap()

        printfRec(rootUi) { fsb, box, rec ->
            fsb.appendln("$box [")
            fsb.indented {
                box.children.forEach {
                    rec(it)
                }
            }
            fsb.appendln("]")
        }
    }
}