package com.veosps.rsrendering.opengl.graph

import com.veosps.rsrendering.utils.cosF
import com.veosps.rsrendering.utils.radians
import com.veosps.rsrendering.utils.sinF
import org.joml.Vector3f
import kotlin.math.cos
import kotlin.math.sin

class Camera(
    val position: Vector3f = Vector3f(),
    val rotation: Vector3f = Vector3f()
) {

    fun position(x: Float, y: Float, z: Float) {
        position.x = x
        position.y = y
        position.z = z
    }

    fun rotation(x: Float, y: Float, z: Float) {
        rotation.x = x
        rotation.y = y
        rotation.z = z
    }

    fun move(offsetX: Float, offsetY: Float, offsetZ: Float) {

        if (offsetZ != 0f) {
            println("Moving backwards and forwards?!")
            position.x += sinF(radians(rotation.y())) * -1.0f * offsetZ
            position.z += cosF(radians(rotation.y())) * offsetZ
        }

        if (offsetX != 0f) {
            println("Moving left and right?!")
            position.x = sinF(radians((rotation.y() - 90))) * -1.0f * offsetX
            position.z = cosF(radians((rotation.y() - 90))) * offsetX
        }

        position.y += offsetY
    }

    fun rotate(offsetX: Float, offsetY: Float, offsetZ: Float) {
        rotation.x += offsetX
        rotation.y += offsetY
        rotation.z += offsetZ
    }
}