package net.sarazan.projektion

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.MotionEvent.*
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout

/**
 * Created by Aaron Sarazan on 1/11/17
 * Copyright(c) 2017 Level, Inc.
 */
class ProjektionFrameLayout : FrameLayout {

    private val dragList = mutableListOf<Drag>()

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return dragList.isNotEmpty()
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
                dragList.forEach { move(it, ev) }
                return true
            }
            ACTION_CANCEL -> {
                dragList.forEach { undrag(it) }
                return true
            }
            ACTION_UP -> {
                drop(dragList, ev)
                dragList.forEach { undrag(it) }
                return true
            }
        }
        return false
    }

    private fun drop(dragList: List<Drag>, ev: MotionEvent): Boolean = drop(this, ev, dragList)

    private fun hitDetect(child: View, ev: MotionEvent): Boolean {
        return ev.x > child.left && ev.x < child.right && ev.y > child.top && ev.y < child.bottom
    }

    private fun drop(view: View, ev: MotionEvent, dragList: List<Drag>): Boolean {
        if (view.projektDragListener != null && hitDetect(view, ev) && view.projektDragListener!!.onDragDropped()) {
            return true
        }
        if (view is ViewGroup) {
            return view.children.any { drop(it, ev, dragList) }
        }
        return false
    }

    private fun move(drag: Drag, ev: MotionEvent) {
        val diffX = ev.rawX - drag.startX!!
        val diffY = ev.rawY - drag.startY!!
        drag.projektion.let {
            it.moveTo(it.view, diffX, diffY)
        }
    }

    data class Drag(val projektion: Projektion) {
        var startX: Float? = null
        var startY: Float? = null
    }
}