package net.sarazan.projektion

import android.util.Log
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_CANCEL
import android.view.MotionEvent.ACTION_UP
import android.view.View

/**
 * Created by Aaron Sarazan on 1/11/17
 * Copyright(c) 2017 Level, Inc.
 */
class ProjektionTouchListener(val projektion: Projektion) : View.OnTouchListener {

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        Log.d("ProjektionTouchListener", "$event")
        when (event.action) {
            ACTION_CANCEL, ACTION_UP -> projektion.destroy()
        }
        return true
    }
}