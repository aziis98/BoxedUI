package com.aziis98.boxed.features

import com.aziis98.boxed.Box
import java.awt.Graphics2D

// Copyright 2016 Antonio De Lucreziis

interface Feature {
    val box: Box
}

interface ConstraintFeature : Feature {
    fun updateConstraint()
}

interface RenderFeature : Feature {
    fun render(g: Graphics2D)
}