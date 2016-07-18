package com.aziis98.tests

import com.aziis98.boxed.*
import com.aziis98.boxed.events.Mouse
import com.aziis98.boxed.features.RenderRegistry
import com.aziis98.boxed.templates.*
import com.aziis98.boxed.utils.*
import org.w3c.dom.Element
import printfRec
import java.awt.*
import java.nio.file.Paths

// Copyright 2016 Antonio De Lucreziis

const val loremIpsum = "Lorem ipsum dolor sit amet, consectetur adipisci elit"

val scopeMap = mutableMapOf<String, Color>()

fun main(args: Array<String>) {
    val document = readXml(Paths.get("res/latin-1.xml"))
    val lines = document.getElementsByTagName("text").asElementList().first().childNodes.asElementList().map { it.textContent }

    var scroll = 0

    RenderRegistry.register("latin-analyzer") { box, g, el ->
        g.color = Color.BLACK
        g.font = DefaultUI.standardFontMedium

        g.translate(0, scroll * 10)

        lines.forEachIndexed { i, line ->
            val (centerX, centerY) = getLinePosition(box, i)

            g.renderTextLine(line, centerX, centerY)
        }

        (document.getElementsByTagName("comments").item(0) as Element).getElementsByTagName("comment").asElementList().forEach { comment ->
            g.renderTextHighlight(box, lines,
                comment.getAttribute("line").toInteger(),
                comment.getAttribute("pos").toInteger(),
                comment.getAttribute("length").toInteger(),
                getScopeColor(comment.getAttribute("scope"))
                )
        }

        g.color = Color.RED
        g.drawCircle(Mouse.x - box.absoluteLeft, Mouse.y - box.absoluteTop, 3)
    }

    BoxWindow.fromXmlTemplate(Paths.get("res/test-window.xml"), MenuBarTemplate, ContextMenuTemplate).apply {
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


fun Graphics2D.renderTextLine(text: String, centerX: Int, centerY: Int) {
    color = Color.BLACK
    drawStringCentered(text, centerX, centerY)
}

fun Graphics2D.renderTextHighlight(box: Box, source: List<String>, stringLine: Int, stringPosition: Int, stringLength: Int, color: Color) {
    this.color = color
    val (cX, cY) = getLinePosition(box, stringLine)

    val line = source[stringLine]

    val lineBounds = this.fontMetrics.getStringBounds(line, this)
    val beforeBounds = this.fontMetrics.getStringBounds(line, 0, stringPosition - 1, this)
    val highlightBounds = this.fontMetrics.getStringBounds(line, stringPosition - 1, stringPosition + stringLength - 1, this)

    this.color = Color.RED
    drawRect(
        cX - lineBounds.width.toInt() / 2 + beforeBounds.width.toInt() - 2, cY - lineBounds.height.toInt() / 2 + 3,
        highlightBounds.width.toInt() + 2, highlightBounds.height.toInt()
    )
}

fun getLinePosition(box: Box, lineIndex: Int) = Pair(box.width / 2, box.height / 4 + lineIndex * 40)

fun getScopeColor(scope: String): Color {
    if (scopeMap.containsKey(scope) == false)
        scopeMap.put(scope, Color(
            Math.random().toFloat(),
            Math.random().toFloat(),
            Math.random().toFloat()
        ))

    return scopeMap[scope]!!
}