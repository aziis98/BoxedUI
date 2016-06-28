package com.aziis98.boxed

import com.aziis98.boxed.features.render
import com.aziis98.boxed.utils.*
import org.w3c.dom.Element
import java.awt.*
import java.awt.image.BufferedImage
import java.nio.file.Path
import java.util.*
import javax.swing.JFrame
import javax.xml.parsers.DocumentBuilderFactory


// Copyright 2016 Antonio De Lucreziis

class BoxWindow() : IContainer {

    private val jframe = object : JFrame() {
        override fun paint(g: Graphics) { }
        override fun update(g: Graphics) { }
    }.apply {
        setLocationRelativeTo(null)
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

    var visible: Boolean
        get() = jframe.isVisible
        set(value) { jframe.isVisible = value }

    var resizable: Boolean
        get() = jframe.isResizable
        set(value) { jframe.isResizable = value }

    // Rendering
    //

    var interpolation = 1.0
    internal var frameCount = 0
    var fps = 0

    val timeBetweenUpdates: Int
        get() = 1000000000 / 30

    val timeBetweenRenders: Int
        get () = 1000000000 / 60

    internal var lastUpdateTime = System.nanoTime()

    internal var lastRenderTime = System.nanoTime()

    internal var lastSecondTime = (lastUpdateTime / 1000000000).toInt()

    var maxUpdatesBeforeRender = 5
    var showFPS = true
    // var pauseRendering = false
    var totalUpdates = 0

    fun applicationLoop() {

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

    internal var buffer: BufferedImage? = null

    internal fun renderInternal() {

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

        val templates = HashMap<String, Box.(Element, Box.(Element) -> Box) -> Box>()

        init {
            templates.put("menubar") { element, parseElement ->
                box() {
                    features += render { g ->

                    }
                }
            }
        }

        fun fromXmlTemplate(path: Path): BoxWindow {
            val window = DocumentBuilderFactory
                .newInstance()
                .newDocumentBuilder()
                .parse(path.toFile()).documentElement

            assert(window.tagName == "window")

            val boxWindow = BoxWindow().apply {

            }

            return boxWindow
        }

    }

}