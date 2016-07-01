package com.aziis98.tests

import com.aziis98.boxed.BoxWindow
import org.junit.Test
import printfRec
import java.nio.file.Paths

// Copyright 2016 Antonio De Lucreziis

internal class BoxWindowTest {

    @Test
    fun testWindow() {

        BoxWindow
            .fromXmlTemplate(Paths.get("res/test-window.xml"))
            .start()

    }

}

fun main(args: Array<String>) {
    val window = BoxWindow.fromXmlTemplate(Paths.get("res/test-window.xml"))

    window.start()
    window.rootUi.updateLayout()

    printfRec(window.rootUi) { fsb, box, rec ->
        fsb.appendln("$box [")
        fsb.indented {
            box.children.forEach {
                rec(it)
            }
        }
        fsb.appendln("]")
    }
}