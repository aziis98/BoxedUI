package com.aziis98.boxed.utils

import org.w3c.dom.*
import java.nio.file.Path
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory

// Copyright 2016 Antonio De Lucreziis

private class ListByNodeList(val nodeList: NodeList) : AbstractList<Element?>() {

    override fun get(index: Int) = nodeList.item(index) as? Element

    override val size: Int
        get() = nodeList.length
}

fun NodeList.asElementList() = if(length == 0) emptyList() else ListByNodeList(this).filterNotNull()

fun readXml(path: Path): Element {
    return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(path.toFile()).documentElement
}