package com.aziis98.boxed.textures

import java.awt.*
import java.awt.image.BufferedImage
import java.nio.file.*
import javax.imageio.ImageIO

// Copyright 2016 Antonio De Lucreziis

object TextureLoader {

    fun texture(path: Path) = Texture(
        ImageIO.read(Files.newInputStream(path))
    )

    fun ninePatch(path: Path, left: Int = 9, right: Int = 9, top: Int = 9, bottom: Int = 9) = NinePatch(
        ImageIO.read(Files.newInputStream(path)),
        left, right, top, bottom
    )

}

open class Texture(val image: BufferedImage) {
    // internal val image: BufferedImage = ImageIO.read(Geometric::class.java.getResource(url))

    val width: Int
        get() = image.width

    val height: Int
        get() = image.height
}

class NinePatch(image: BufferedImage,
                val left: Int = 9, val right: Int = 9,
                val top: Int = 9, val bottom: Int = 9) : Texture(image) {
    // @formatter:off
    val patchTopLeft        = image.getSubimage(0            , 0, left                , top)!!
    val patchTopCenter      = image.getSubimage(left         , 0, width - left - right, top)!!
    val patchTopRight       = image.getSubimage(width - right, 0, right               , top)!!

    val patchCenterLeft     = image.getSubimage(0            , top,                left , height - top - bottom)!!
    val patchCenterCenter   = image.getSubimage(left         , top, width - left - right, height - top - bottom)!!
    val patchCenterRight    = image.getSubimage(width - right, top,                right, height - top - bottom)!!

    val patchBottomLeft     = image.getSubimage(0            , height - bottom, left                , bottom)!!
    val patchBottomCenter   = image.getSubimage(left         , height - bottom, width - left - right, bottom)!!
    val patchBottomRight    = image.getSubimage(width - right, height - bottom, right               , bottom)!!
    // @formatter:onCancellable
}

fun Graphics2D.drawTexture(texture: Texture, x: Int, y: Int, width: Int = texture.width, height: Int = texture.height) {
    drawImage(texture.image, x, y, width, height, null)
}

fun Graphics2D.drawNinePatchTexture(ninePatch: NinePatch, x: Int, y: Int, width: Int, height: Int) {
    ninePatch.apply {

        // @formatter:off
        drawImage(patchTopLeft, x, y, left, top, null)
        drawImage(patchTopCenter, x + left, y, width - left - right, top, null)
        drawImage(patchTopRight, x + width - right, y, right, top, null)

        drawImage(patchCenterLeft, x, y + top, left, height - top - bottom, null)
        drawImage(patchCenterCenter, x + left, y + top, width - left - right, height - top - bottom, null)
        drawImage(patchCenterRight, x + width - right, y + top, right, height - top - bottom, null)

        drawImage(patchBottomLeft, x, y + height - bottom, left, bottom, null)
        drawImage(patchBottomCenter, x + left, y + height - bottom, width - left - right, bottom, null)
        drawImage(patchBottomRight, x + width - right, y + height - bottom, right, bottom, null)
        // @formatter:onCancellable

    }
}

object DefaultUI {

    val standardFont = Font("Roboto", Font.PLAIN, 14)

    val menuBar = TextureLoader.ninePatch(Paths.get("assets/ui/menubar.png"))
    val statusBar = TextureLoader.ninePatch(Paths.get("assets/ui/light-statusbar.png"))

}