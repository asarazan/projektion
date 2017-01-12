package net.sarazan.projektion

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_CANCEL
import android.view.MotionEvent.ACTION_MOVE
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

    override fun onTouchEvent(event: MotionEvent): Boolean {
        dragList.forEach {
            if (it.startX == null || it.startY == null) {
                it.startX = event.rawX
                it.startY = event.rawY
            }
        }
        when (event.action) {
            ACTION_MOVE -> {
                dragList.forEach { move(it, event) }
            }
            ACTION_CANCEL -> {
                dragList.forEach { undrag(it) }
            }
        }
        return super.onTouchEvent(event)
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