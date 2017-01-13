package net.sarazan.projektion

import android.graphics.Bitmap
import android.util.Log
import android.view.View
import android.view.ViewGroup

/**
 * Created by Aaron Sarazan on 11/6/16
 */

val ViewGroup.children: List<View> get() = (0 until childCount).map { getChildAt(it) }

inline fun <reified T : ViewGroup> View.parentWithClass(): T? {
    var group = this as? ViewGroup ?: parent as? ViewGroup
    while (group !is T) {
        group = group?.parent as? ViewGroup
        if (group == null) return null
    }
    return group
}

internal val View.bitmap: Bitmap? get() {
    this.clearFocus()
    this.isPressed = false

    val willNotCache = this.willNotCacheDrawing()
    this.setWillNotCacheDrawing(false)

    // Reset the drawing cache background color to fully transparent
    // for the duration of this operation
    val color = this.drawingCacheBackgroundColor
    this.drawingCacheBackgroundColor = 0

    if (color != 0) {
        this.destroyDrawingCache()
    }
    this.buildDrawingCache()
    val cacheBitmap = this.drawingCache
    if (cacheBitmap == null) {
        Log.e("Projektion", "failed getViewBitmap($this)", RuntimeException())
        return null
    }

    val bitmap = Bitmap.createBitmap(cacheBitmap)

    // Restore the view
    this.destroyDrawingCache()
    this.setWillNotCacheDrawing(willNotCache)
    this.drawingCacheBackgroundColor = color

    return bitmap
}