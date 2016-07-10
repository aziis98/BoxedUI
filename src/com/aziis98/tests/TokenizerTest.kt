package com.aziis98.tests

import com.aziis98.boxed.utils.tokenize
import org.junit.Assert.assertEquals
import org.junit.Test

// Copyright 2016 Antonio De Lucreziis

class TokenizerTest {

    @Test
    fun testTokenize() {
        assertEquals(
            listOf("@", "box1", "#", "tag1"),
            tokenize("@box1#tag1")
        )
    }

    @Test
    fun testTokenizeGeneralized() {
        assertEquals(
            listOf(listOf(1, 1, 1, 1), listOf(2, 2, 2), listOf(3), listOf(1, 1), listOf(2, 2)),
            tokenize({ a, b -> a == b }, listOf(1, 1, 1, 1, 2, 2, 2, 3, 1, 1, 2, 2))
        )
    }

}