package com.aziis98.boxed.utils

import org.w3c.dom.*
import java.util.*

// Copyright 2016 Antonio De Lucreziis

private class ListByNodeList(val nodeList: NodeList) : AbstractList<Element?>() {

    override fun get(index: Int) = nodeList.item(index) as? Element

    override val size: Int
        get() = nodeList.length
}

fun NodeList.asElementList() = if(length == 0) emptyList() else ListByNodeList(this).filterNotNull()
