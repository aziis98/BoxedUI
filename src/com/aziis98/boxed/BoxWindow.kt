package com.aziis98.boxed

import java.nio.file.Path
import javax.xml.parsers.DocumentBuilderFactory


// Copyright 2016 Antonio De Lucreziis

class BoxWindow {

    companion object {

        fun fromXmlTemplate(path: Path) {
            val window = DocumentBuilderFactory
                .newInstance()
                .newDocumentBuilder()
                .parse(path.toFile()).documentElement


        }

    }

}