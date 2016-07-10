package com.aziis98.tests

import com.aziis98.boxed.BoxWindow
import com.aziis98.boxed.utils.tokenize
import org.junit.Assert.assertEquals
import org.junit.Test
import printfRec
import java.nio.file.Paths

// Copyright 2016 Antonio De Lucreziis

internal class BoxWindowTest {

    @Test
    fun testTokenizer() {
        val query = "@box1#tag1"

        assertEquals(listOf("@", "box1", "#", "tag1"), tokenize(query))
    }

}

fun main(args: Array<String>) {
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