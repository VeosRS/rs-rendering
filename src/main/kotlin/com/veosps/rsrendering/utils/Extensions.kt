package com.veosps.rsrendering.utils

fun Any.loadResourceDataAsString(fileName: String) =
    this.javaClass.getResourceAsStream(fileName)
        ?.readAllBytes()
        ?.decodeToString() ?: error("File not found: $fileName")


fun Any.loadResourceData(fileName: String) =
    this.javaClass.getResourceAsStream(fileName)?.readAllBytes() ?: error("File not found.")
