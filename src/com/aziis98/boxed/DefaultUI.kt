package com.aziis98.boxed

import com.aziis98.boxed.textures.TextureLoader
import java.awt.Font
import java.nio.file.Paths

// Copyright 2016 Antonio De Lucreziis

object DefaultUI {

    val standardFont = Font("Segoe UI", Font.PLAIN, 14)
    val standardFontMedium = Font("Segoe UI", Font.PLAIN, 18)
    val standardFontLarge = Font("Segoe UI", Font.PLAIN, 22)

    val menuBar = TextureLoader.ninePatch(Paths.get("assets/ui/menubar.png"))
    val statusBar = TextureLoader.ninePatch(Paths.get("assets/ui/light-statusbar.png"))

}