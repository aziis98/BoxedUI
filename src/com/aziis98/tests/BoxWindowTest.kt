package com.aziis98.tests

import com.aziis98.boxed.*
import com.aziis98.boxed.events.Mouse
import com.aziis98.boxed.features.RenderRegistry
import com.aziis98.boxed.utils.*
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
    val document = readXml(Paths.get("res/latin-1.xml"))
    val lines = document.getElementsByTagName("text").asElementList().first().textContent.toTextBlock(wordPerLine = 10)

    var scroll = 0

    RenderRegistry.register("latin-analyzer") { box, g, el ->
        g.color = Color.BLACK
        g.font = DefaultUI.standardFontMedium

        lines.forEachIndexed { i, line ->
            g.drawStringCentered(line, box.width / 2, box.height / 4 + i * 40 - scroll * 10)
        }

        g.color = Color.RED
        g.drawCircle(Mouse.x - box.absoluteLeft, Mouse.y - box.absoluteTop, 3)
    }

    BoxWindow.fromXmlTemplate(Paths.get("res/test-window.xml")).apply {
        rootUi.apply {
            events.on("menu-file") {
                println("Show the File menu")
            }

            events.on<Mouse.MouseScrollEvent>("mouse:scroll") { event ->
                scroll += event.scroll
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

fun String.toTextBlock(wordPerLine: Int = 30): List<String> {
    val list = this.trim().split(" ").toMutableList()
    val acc = mutableListOf(mutableListOf<String>())

    while (list.size > 0) {
        for (i in 0 .. Math.min(wordPerLine, list.size) - 1) {
            acc.last().add(list.removeAt(0))
        }
        acc.add(mutableListOf())
    }

    return acc.filter { it.size > 0 }.map { it.joinToString(" ") }
}