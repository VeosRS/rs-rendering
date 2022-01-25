package com.veosps.rsrendering.opengl.engine

import com.veosps.rsrendering.opengl.graphics.Mesh
import org.joml.Vector3f

class GameItem(mesh: Mesh) {
    private val mesh: Mesh
    val position: Vector3f
    var scale: Float
    val rotation: Vector3f

    init {
        this.mesh = mesh
        position = Vector3f()
        scale = 1f
        rotation = Vector3f()
    }

    fun setPosition(x: Float, y: Float, z: Float) {
        position.x = x
        position.y = y
        position.z = z
    }

    fun setRotation(x: Float, y: Float, z: Float) {
        rotation.x = x
        rotation.y = y
        rotation.z = z
    }

    fun getMesh(): Mesh {
        return mesh
    }
}