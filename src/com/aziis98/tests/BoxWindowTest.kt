package com.aziis98.tests

import com.aziis98.boxed.BoxWindow
import org.junit.Test
import java.nio.file.Paths

// Copyright 2016 Antonio De Lucreziis

internal class BoxWindowTest {

    @Test
    fun testWindow() {

        val window = BoxWindow.fromXmlTemplate(Paths.get("res/test-window.xml"))



    }

}
