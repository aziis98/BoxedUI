package com.aziis98.boxed

import com.aziis98.boxed.events.Mouse
import com.aziis98.boxed.templates.ITemplate
import com.aziis98.boxed.utils.IContainer
import java.awt.*
import java.awt.event.*
import java.awt.image.BufferedImage
import java.nio.file.Path
import javax.swing.*
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.concurrent.thread


// Copyright 2016 Antonio De Lucreziis

class BoxWindow() : IContainer {

    val rootUi = Box(this, id = "window", left = 0, right = 0, top = 0, bottom = 0)

    private val jframe = object : JFrame() {
        override fun paint(g: Graphics) { }
        override fun update(g: Graphics) { }
    }.apply {
        defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        background = Color.BLACK
        contentPane.background = Color.BLACK

        minimumSize = Dimension(200, 200)

        addComponentListener(object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent) {
                rootUi.invalidateLayout()
                dirtyBuffer = true
            }
        })

        Mouse.register(rootUi.events, this)

        System.setProperty("sun.awt.noerasebackground", "true")
    }

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

    fun bootstrap() {
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
    var showFPS = false
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
                //You can remove this line and it will still work (better), your CPU just climbs onCancellable certain OSes.
                //FYI onCancellable some OS's this can cause pretty bad stuttering. Scroll down and have a look at different peoples' solutions to this.

                Thread.sleep(1)

                now = System.nanoTime()
            }

        }
    }

    private var dirtyBuffer = false
    private var buffer: BufferedImage? = null

    private fun renderInternal() {

        if (buffer == null || dirtyBuffer) {
            buffer = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        }

        val g = buffer?.graphics as Graphics2D

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)

        g.font = DefaultUI.standardFont

        render(g)

        jframe.graphics.drawImage(buffer, jframe.insets.left, jframe.insets.top, null)

        frameCount++
    }

    private fun render(g: Graphics2D) {
        g.background = Color.WHITE
        g.clearRect(0, 0, width, height)

        rootUi.render(g)
    }

    private fun update() {
        rootUi.updateLayout()
    }

    //

    companion object {

        fun fromXmlTemplate(path: Path, vararg templates: ITemplate): BoxWindow {
            val window = DocumentBuilderFactory
                .newInstance()
                .newDocumentBuilder()
                .parse(path.toFile()).documentElement

            val builder = BoxWindowBuilder()

            templates.forEach {
                it.apply { builder.registerTemplates() }
            }

            return builder.parseFromXml(window)
        }

    }

}