package com.veosps.rsrendering.opengl.engine

import com.veosps.rsrendering.opengl.graph.Mesh
import org.joml.Vector3f

class GameItem(private val mesh: Mesh) {
    val position: Vector3f = Vector3f()
    val rotation: Vector3f = Vector3f()
    var scale: Float = 1f

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

    fun renderMesh() = mesh.render()
    fun cleanMesh() = mesh.cleanUp()
}