package com.aziis98.boxed.events

import java.awt.Container
import java.awt.event.*
import javax.swing.SwingUtilities

// Copyright 2016 Antonio De Lucreziis

object Mouse {

    var prevX = 0
    var prevY = 0

    var x = 0
    var y = 0

    var button = Buttons.None

    val dx: Int
        get() = x - prevX
    val dy: Int
        get() = y - prevY


    fun register(eventDispatcher: EventDispatcher, container: Container) {
        val adapter = object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                button = Buttons.fromCode(e)

                eventDispatcher.dispatch("mouse:click")
            }

            override fun mousePressed(e: MouseEvent) {
                button = Buttons.fromCode(e)

                eventDispatcher.dispatch("mouse:press")
            }

            override fun mouseReleased(e: MouseEvent) {
                button = Buttons.fromCode(e)

                eventDispatcher.dispatch("mouse:release")
            }

            override fun mouseMoved(e: MouseEvent) {
                prevX = x
                prevY = y

                x = e.x - container.insets.left
                y = e.y - container.insets.top

                eventDispatcher.dispatch("mouse:move")
            }

            override fun mouseWheelMoved(e: MouseWheelEvent) {
                eventDispatcher.dispatch("mouse:scroll", MouseScrollEvent(e.wheelRotation))
            }
        }

        container.addMouseListener(adapter)
        container.addMouseMotionListener(adapter)
        container.addMouseWheelListener(adapter)
    }

    override fun toString(): String {
        return "Mouse(x=$x, y=$y, button=$button)"
    }

    enum class Buttons {
        None, Left, Middle, Right;

        companion object {

            fun fromCode(mouseEvent: MouseEvent): Buttons {
                return when {
                    SwingUtilities.isLeftMouseButton(mouseEvent)   -> Left
                    SwingUtilities.isMiddleMouseButton(mouseEvent) -> Middle
                    SwingUtilities.isRightMouseButton(mouseEvent)  -> Right
                    else -> None
                }
            }

        }
    }

    data class MouseScrollEvent(val scroll: Int)

}