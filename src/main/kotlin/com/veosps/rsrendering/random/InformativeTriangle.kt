package com.veosps.rsrendering.random

import org.joml.Vector3f

class InformativeTriangle(
    val v1: Vector3f,
    val v2: Vector3f,
    val v3: Vector3f,
    val triangleIndex: Int,
    val v1Index: Int,
    val v2Index: Int,
    val v3Index: Int,
)

data class Vertex(
    val position: Vector3f,
    val u: Double,
    val v: Double,
    val index: Int
)