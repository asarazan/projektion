package net.sarazan.projektion

import android.content.Context
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
    private val listeners = mutableMapOf<View, DragListener>()

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

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
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
        dragList.add(Drag(projektion))
    }

    fun undrag(drag: Drag) {
        dragList.remove(drag)
        drag.projektion.destroy()
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        dragList.forEach {
            if (it.startX == null || it.startY == null) {
                it.startX = ev.rawX
                it.startY = ev.rawY
            }
        }
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
        dragList.forEach { move(it, ev) }
    }

    private fun handleCancel(ev: MotionEvent) {
        listeners.values.any { it.onDragCanceled(dragList) }
        dragList.forEach { undrag(it) }
    }

    private fun handleUp(ev: MotionEvent) {
        drop(dragList, ev)
        dragList.forEach { undrag(it) }
    }

    private fun drop(dragList: List<Drag>, ev: MotionEvent): Boolean = listeners.any { hitDetect(it.key, ev) && it.value.onDragDropped(dragList) }
    private fun hitDetect(child: View, ev: MotionEvent): Boolean {
        val bounds = child.globalRect
        return ev.rawX > bounds.left && ev.rawX < bounds.right && ev.rawY > bounds.top && ev.rawY < bounds.bottom
    }

    private fun move(drag: Drag, ev: MotionEvent) {
        val diffX = ev.rawX - drag.startX!!
        val diffY = ev.rawY - drag.startY!!
        drag.projektion.let {
            it.moveTo(it.view, diffX, diffY)
        }
    }
}