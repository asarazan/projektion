package net.sarazan.projektion

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup

/**
 * Created by Aaron Sarazan on 11/6/16
 */

fun View.projektInto(viewGroup: ViewGroup): Projektion = Projektion(this, viewGroup)

fun View.getBoundsIn(viewGroup: ViewGroup): Rect {
    val me = Rect(left, top, right, bottom)
    val parent = (parent as ViewGroup).globalRect
    val newParent = viewGroup.globalRect
    return me + parent - newParent
}

val View.globalRect: Rect get() = Rect().apply { getGlobalVisibleRect(this) }
val View.localRect: Rect get() = Rect().apply { getLocalVisibleRect(this) }

operator fun Rect.minus(other: Rect): Rect = Rect(left - other.left, top - other.top, right - other.right, bottom - other.bottom)
operator fun Rect.plus(other: Rect): Rect = Rect(left + other.left, top + other.top, right + other.right, bottom + other.bottom)