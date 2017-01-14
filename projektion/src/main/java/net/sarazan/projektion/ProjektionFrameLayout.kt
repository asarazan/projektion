package net.sarazan.projektion

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.MotionEvent.*
import android.view.View
import android.widget.FrameLayout
import net.sarazan.projektion.Projektion.Drag
import net.sarazan.projektion.Projektion.DragListener

/**
 * Created by Aaron Sarazan on 1/11/17
 * Copyright(c) 2017 Level, Inc.
 */
class ProjektionFrameLayout : FrameLayout {

    companion object {
        private const val TAG = "ProjektionFrameLayout"
    }

    private val dragList = mutableListOf<Drag>()
    private val listeners = linkedMapOf<View, DragListener>()

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    internal fun getDragListener(view: View): DragListener? {
        return listeners[view]
    }

    internal fun setDragListener(view: View, listener: DragListener?) {
        if (listener == null) {
            listeners.remove(view)
        } else {
            listeners[view] = listener
        }
    }

    private var lastMotionEvent: MotionEvent? = null
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        lastMotionEvent = ev
        if (dragList.isNotEmpty()) {
            when (ev.action) {
                ACTION_UP -> {
                    handleUp(ev)
                }
            }
            return true
        }
        return false
    }

    fun drag(projektion: Projektion) {
        dragList.add(Drag(projektion).apply {
            lastMotionEvent?.let {
                startX = it.rawX
                startY = it.rawY
            }
        })
    }

    fun undrag(drag: Drag) {
        dragList.remove(drag)
        drag.projektion.destroy()
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            ACTION_MOVE -> {
                handleMove(ev)
                return true
            }
            ACTION_CANCEL -> {
                handleCancel(ev)
                return true
            }
            ACTION_UP -> {
                handleUp(ev)
                return true
            }
        }
        return false
    }

    private fun handleMove(ev: MotionEvent) {
        dragList.toList().forEach { move(it, ev) }
    }

    private fun handleCancel(ev: MotionEvent) {
        if (!listeners.values.reversed().any { it.onDragCanceled(dragList) }) {
            listeners.values.reversed().any { it.onDragFailed(dragList) }
        }
        undrag()
    }

    private fun handleUp(ev: MotionEvent) {
        if (!drop()) {
            listeners.values.reversed().any { it.onDragFailed(dragList) }
        }
        undrag()
    }

    private fun undrag() {
        dragList.toList().forEach { undrag(it) }
    }

    private fun drop(): Boolean = hitDetect() != null
    private fun hitDetect(): View? {
        val scores = listeners.keys.mapNotNull { score(it, dragList)?.let { score -> Triple(it, listeners[it]!!, score) } }.sortedByDescending { it.third }
        return scores.firstOrNull { it.second.onDragDropped(dragList) }?.first
    }

    private fun score(child: View, dragList: List<Drag>): Int? {
        return dragList.mapNotNull { score(child, it) }.max()
    }

    private fun score(child: View, drag: Drag): Int? {
        val bounds = child.globalRect
        val gBounds = drag.projektion.ghostView.globalRect
        return bounds.getOverlap(gBounds)?.getArea()
    }

    private fun hitDetect(child: View, drag: Drag): Boolean {
        val bounds = child.globalRect
        val ghost = drag.projektion.ghostView
        val gBounds = ghost.globalRect
        return bounds.intersect(gBounds)
    }

    private fun move(drag: Drag, ev: MotionEvent) {
        val diffX = ev.rawX - drag.startX!!
        val diffY = ev.rawY - drag.startY!!
        drag.projektion.let {
            it.moveTo(it.view, diffX, diffY)
        }
    }

    private fun Rect.getOverlap(other: Rect): Rect? {
        val left = Math.max(left, other.left)
        val right = Math.min(right, other.right)
        if (right <= left) return null
        val top = Math.max(top, other.top)
        val bottom = Math.min(bottom, other.bottom)
        if (bottom <= top) return null
        return Rect(left, top, right, bottom)
    }

    private fun Rect.getArea(): Int {
        val w = right - left
        val h = bottom - top
        return w * h
    }
}