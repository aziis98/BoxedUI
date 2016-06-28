package com.aziis98.boxed.utils

// Copyright 2016 Antonio De Lucreziis

/**
 * if (value == targetValue) ifTrue else ifFalse
 */
fun <T> iff(value: T, targetValue: T, ifTrue: T, ifFalse: T = value) =
    if (value == targetValue) ifTrue else ifFalse