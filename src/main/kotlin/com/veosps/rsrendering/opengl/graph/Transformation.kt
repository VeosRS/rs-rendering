package com.veosps.rsrendering.opengl.graph

import com.veosps.rsrendering.opengl.engine.GameItem
import com.veosps.rsrendering.utils.radians
import org.joml.Matrix4f
import org.joml.Vector3f

class Transformation(
    private val projectionMatrix: Matrix4f = Matrix4f(),
    private val modelViewMatrix: Matrix4f = Matrix4f(),
    private val viewMatrix: Matrix4f = Matrix4f()
) {

    fun projectionMatrix(fov: Float, width: Float, height: Float, zNear: Float, zFar: Float): Matrix4f {
        return projectionMatrix.setPerspective(fov, width / height, zNear, zFar)
    }

    fun viewMatrix(camera: Camera): Matrix4f {
        val cameraPosition = camera.position
        val cameraRotation = camera.rotation

        viewMatrix.identity()
        viewMatrix.rotate(radians(cameraRotation.x).toFloat(), Vector3f(1f, 0f, 0f))
            .rotate(radians(cameraRotation.y).toFloat(), Vector3f(0f, 1f, 0f))
        viewMatrix.translate(-cameraPosition.x, -cameraPosition.y, -cameraPosition.z)

        return viewMatrix
    }

    fun modelViewMatrix(gameItem: GameItem, viewMatrix: Matrix4f): Matrix4f {
        val rotation = gameItem.rotation

        modelViewMatrix.identity().translate(gameItem.position)
            .rotateX(radians(-rotation.x).toFloat())
            .rotateY(radians(-rotation.y).toFloat())
            .rotateZ(radians(-rotation.z).toFloat())
            .scale(gameItem.scale)

        return Matrix4f(viewMatrix).mul(modelViewMatrix)
    }
}