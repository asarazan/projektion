package net.sarazan.projektion

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import net.sarazan.projektion.Projektion.DragListener

/**
 * Created by Aaron Sarazan on 11/6/16
 */

var View.projektionDragListener: DragListener?
    get() = parentWithClass<ProjektionFrameLayout>()?.getDragListener(this)
    set(value) { parentWithClass<ProjektionFrameLayout>()!!.setDragListener(this, value) }

fun View.projekt(viewGroup: ViewGroup = Projektion.getProjektionView(this)): Projektion = Projektion(this, viewGroup)
fun ViewGroup.clearProjektion() {
    children.forEach {
        if (it.getTag(R.id.tag_projektion_ghost) != null) {
            removeView(it)
        }
    }
}

fun View.getBoundsIn(viewGroup: ViewGroup): Rect {
    val x = totalTranslationX.toInt()
    val y = totalTranslationY.toInt()
    val me = Rect(left + x, top + y, right + x, bottom + y)
    val parent = (parent as? ViewGroup)?.globalRect ?: Rect()
    val newParent = viewGroup.globalRect
    val padding = Rect(viewGroup.paddingLeft, viewGroup.paddingTop, viewGroup.paddingRight, viewGroup.paddingBottom)
    return me + parent - newParent - padding
}

val View.totalTranslationX: Float get() {
    var retval = translationX
    var parent = parent as? ViewGroup
    while (parent != null) {
        retval += parent.translationX
        parent = parent.parent as? ViewGroup
    }
    return retval
}
val View.totalTranslationY: Float get() {
    var retval = translationY
    var parent = parent as? ViewGroup
    while (parent != null) {
        retval += parent.translationY
        parent = parent.parent as? ViewGroup
    }
    return retval
}

val View.globalRect: Rect get() = Rect().apply { getGlobalVisibleRect(this) }
val View.localRect: Rect get() = Rect().apply { getLocalVisibleRect(this) }

operator fun Rect.minus(other: Rect): Rect = Rect(left - other.left, top - other.top, right - other.right, bottom - other.bottom)
operator fun Rect.plus(other: Rect): Rect = Rect(left + other.left, top + other.top, right + other.right, bottom + other.bottom)