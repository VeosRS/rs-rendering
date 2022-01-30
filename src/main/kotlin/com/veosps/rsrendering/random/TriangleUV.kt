package com.veosps.rsrendering.random

class TriangleUV(
    val u1: Float,
    val u2: Float,
    val u3: Float,
    val v1: Float,
    val v2: Float,
    val v3: Float
) {

    fun normalize(): TriangleUV {
        return TriangleUV(u1 % 1f, u2 % 1f, u3 % 1f, v1 % 1f, v2 % 1f, v3 % 1f)
    }
}