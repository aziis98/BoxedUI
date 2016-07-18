package com.aziis98.boxed

import com.aziis98.boxed.textures.TextureLoader
import java.awt.Font
import java.nio.file.*

// Copyright 2016 Antonio De Lucreziis

object DefaultUI {

    val standardFont = Font("Segoe UI", Font.PLAIN, 14)
    val standardFontMedium = Font("Segoe UI", Font.PLAIN, 18)
    val standardFontLarge = Font("Segoe UI", Font.PLAIN, 22)

    val menuBar = TextureLoader.ninePatch(
        javaClass.getResourceAsStream("/ui/menubar.png")
            ?: Files.newInputStream(Paths.get("assets/ui/menubar.png"))
    )
    val statusBar = TextureLoader.ninePatch(
        javaClass.getResourceAsStream("/ui/light-statusbar.png")
            ?: Files.newInputStream(Paths.get("assets/ui/light-statusbar.png"))
    )
    val contextMenu = TextureLoader.ninePatch(
        javaClass.getResourceAsStream("/ui/context-menu.png")
            ?: Files.newInputStream(Paths.get("assets/ui/context-menu.png"))
    )
}