package com.aziis98.boxed

import com.aziis98.boxed.features.*
import com.aziis98.boxed.textures.*
import com.aziis98.boxed.utils.*
import org.w3c.dom.Element
import java.awt.*
import java.awt.image.BufferedImage
import java.nio.file.Path
import java.util.*
import javax.swing.*
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.concurrent.thread


// Copyright 2016 Antonio De Lucreziis

class BoxWindow() : IContainer {

    private val jframe = object : JFrame() {
        override fun paint(g: Graphics) { }
        override fun update(g: Graphics) { }
    }.apply {
        defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        background = Color.BLACK
        contentPane.background = Color.BLACK

        System.setProperty("sun.awt.noerasebackground", "true")
    }

    val rootUi = Box(this, left = 0, right = 0, top = 0, bottom = 0)

    override var width: Int
        get() = jframe.width - (jframe.insets.left + jframe.insets.right)
        set(value) { jframe.setSize(value + (jframe.insets.left + jframe.insets.right), jframe.height) }

    override var height: Int
        get() = jframe.height - (jframe.insets.top + jframe.insets.bottom)
        set(value) { jframe.setSize(jframe.width, value + (jframe.insets.top + jframe.insets.bottom)) }

    var title: String
        get() = jframe.title
        set(value) { jframe.title = value }

    var visible: Boolean
        get() = jframe.isVisible
        set(value) { jframe.isVisible = value }

    var resizable: Boolean
        get() = jframe.isResizable
        set(value) { jframe.isResizable = value }

    fun start() {
        visible = true
        thread {
            applicationLoop()
        }
    }

    // Rendering
    //

    var interpolation = 1.0
    internal var frameCount = 0
    var fps = 0

    val timeBetweenUpdates: Int
        get() = 1000000000 / 30

    val timeBetweenRenders: Int
        get () = 1000000000 / 60

    private var lastUpdateTime = System.nanoTime()

    private var lastRenderTime = System.nanoTime()

    private var lastSecondTime = (lastUpdateTime / 1000000000).toInt()

    var maxUpdatesBeforeRender = 5
    var showFPS = true
    // var pauseRendering = false
    var totalUpdates = 0

    private fun applicationLoop() {

        while (visible) {

            var now = System.nanoTime()
            var updateCount = 0

            while (now - lastUpdateTime > timeBetweenUpdates && updateCount < maxUpdatesBeforeRender) {
                update()
                lastUpdateTime += timeBetweenUpdates
                updateCount++

                totalUpdates++
            }

            //If for some reason an update takes forever, we don't want to do an insane number of catchups.
            //If you were doing some sort of game that needed to keep EXACT time, you would get rid of this.
            if (now - lastUpdateTime > timeBetweenUpdates) {
                lastUpdateTime = now - timeBetweenUpdates
            }

            //Render. To do so, we need to calculate interpolation for a smooth render.
            interpolation = Math.min(1.0, ((now - lastUpdateTime) / timeBetweenUpdates).toDouble())
            renderInternal()
            lastRenderTime = now

            //Update the frames we got.
            val thisSecond = (lastUpdateTime / 1000000000).toInt()

            if (thisSecond > lastSecondTime) {
                if (showFPS) System.out.println("$frameCount FPS")
                fps = frameCount
                frameCount = 0
                lastSecondTime = thisSecond
            }

            //Yield until it has been at least the target time between renders. This saves the CPU from hogging.
            while (now - lastRenderTime < timeBetweenRenders && now - lastUpdateTime < timeBetweenUpdates) {

                Thread.`yield`()

                //This stops the app from consuming all your CPU. It makes this slightly less accurate, but is worth it.
                //You can remove this line and it will still work (better), your CPU just climbs on certain OSes.
                //FYI on some OS's this can cause pretty bad stuttering. Scroll down and have a look at different peoples' solutions to this.

                Thread.sleep(1)

                now = System.nanoTime()
            }

        }
    }

    private var buffer: BufferedImage? = null

    private fun renderInternal() {

        if (buffer == null) {
            buffer = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        }

        val g = buffer?.graphics as Graphics2D

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB)
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)

        render(g)

        jframe.graphics.drawImage(buffer, jframe.insets.left, jframe.insets.top, null)

        frameCount++
    }

    private fun render(g: Graphics2D) {
        rootUi.render(g)
    }

    private fun update() {
        rootUi.updateLayout()
    }

    //

    companion object {

        val boxTemplates = HashMap<String, (parent: Box, Element) -> Unit>()
        val featureTypes = HashMap<String, (parent: Box, Element) -> Unit>()

        init {
            boxTemplates.put("Box") { parent, element ->
                parent.box(
                    left = element.getAttribute("left").toNullableInt() ?: Box.ABSENT,
                    right = element.getAttribute("right").toNullableInt() ?: Box.ABSENT,
                    top = element.getAttribute("top").toNullableInt() ?: Box.ABSENT,
                    bottom = element.getAttribute("bottom").toNullableInt() ?: Box.ABSENT,
                    width = element.getAttribute("width").toNullableInt() ?: Box.ABSENT,
                    height = element.getAttribute("height").toNullableInt() ?: Box.ABSENT
                ) {
                    element.childNodes.asElementList().forEach {
                        if (it.tagName == "Feature") {
                            parseFeature(this, it)
                        }
                        else {
                            parseElement(this, it)
                        }
                    }
                }
            }

            boxTemplates.put("menubar") { parent, element ->
                parent.box() {
                    features += render { g ->
                        g.drawNinePatchTexture(DefaultUITextures.menuBar, 0, 0, width, height)
                    }

                    element.getElementsByTagName("menu").asElementList().forEach {
                        println(it.getAttribute("onClick"))
                    }
                }
            }


            featureTypes.put("render") { parent, element ->
                parent.render { g ->
                    RenderRegistry.registry[element.getAttribute("by")]!!(this, g, element)
                }
            }
        }

        fun fromXmlTemplate(path: Path): BoxWindow {
            val window = DocumentBuilderFactory
                .newInstance()
                .newDocumentBuilder()
                .parse(path.toFile()).documentElement

            assert(window.tagName == "Window")

            val boxWindow = BoxWindow().apply {
                title = window.getAttribute("title")
                width = window.getAttribute("width").toNullableInt() ?: 640
                height = window.getAttribute("height").toNullableInt() ?: 480

                window.childNodes.asElementList().forEach {
                    parseElement(rootUi, it)
                }
            }

            return boxWindow
        }

        fun parseElement(parent: Box, element: Element) {
            val templater = boxTemplates[element.tagName]!!

            templater(parent, element)
        }

        fun parseFeature(parent: Box, element: Element) {
            featureTypes[element.getAttribute("type")]!!(parent, element)
        }

    }

}