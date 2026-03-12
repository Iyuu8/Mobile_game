package com.game.medievalrpg.world

import android.graphics.RectF

object Collision {

    fun resolveEntityVsTileMap(
        entityX: Float, entityY: Float,
        entityWidth: Float, entityHeight: Float,
        velX: Float, velY: Float,
        tileMap: TileMap
    ): Pair<Float, Float> {
        var newX = entityX + velX
        var newY = entityY + velY
        val ts = tileMap.tileSize.toFloat()

        // Check horizontal movement
        val leftX   = newX
        val rightX  = newX + entityWidth - 1
        val topY    = entityY + 4f
        val bottomY = entityY + entityHeight - 1

        if (tileMap.isSolid(leftX, topY) || tileMap.isSolid(leftX, bottomY) ||
            tileMap.isSolid(rightX, topY) || tileMap.isSolid(rightX, bottomY)) {
            newX = entityX
        }

        // Check vertical movement
        val leftX2  = newX + 4f
        val rightX2 = newX + entityWidth - 4f
        val topY2   = newY
        val bottomY2 = newY + entityHeight - 1

        if (tileMap.isSolid(leftX2, topY2) || tileMap.isSolid(rightX2, topY2) ||
            tileMap.isSolid(leftX2, bottomY2) || tileMap.isSolid(rightX2, bottomY2)) {
            newY = entityY
        }

        // Clamp to map bounds
        newX = newX.coerceIn(0f, tileMap.getPixelWidth() - entityWidth)
        newY = newY.coerceIn(0f, tileMap.getPixelHeight() - entityHeight)

        return Pair(newX, newY)
    }

    fun circleRectOverlap(cx: Float, cy: Float, radius: Float, rect: RectF): Boolean {
        val nearX = cx.coerceIn(rect.left, rect.right)
        val nearY = cy.coerceIn(rect.top, rect.bottom)
        val dx = cx - nearX
        val dy = cy - nearY
        return (dx * dx + dy * dy) <= radius * radius
    }

    fun rectOverlap(a: RectF, b: RectF): Boolean = a.intersect(b)

    fun keepInBounds(x: Float, y: Float, w: Float, h: Float,
                     mapW: Float, mapH: Float): Pair<Float, Float> {
        return Pair(x.coerceIn(0f, mapW - w), y.coerceIn(0f, mapH - h))
    }
}
