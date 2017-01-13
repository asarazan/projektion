package net.sarazan.projektion.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import kotlinx.android.synthetic.main.activity_main.*
import net.sarazan.projektion.Projektion.Drag
import net.sarazan.projektion.Projektion.DragListener
import net.sarazan.projektion.projekt
import net.sarazan.projektion.projektionDragListener
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity() {

    val standardViews by lazy { listOf(view1, view2, view3) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        view1.setOnClickListener {
            it.projekt().animateTo(view2).start()
        }
        view2.setOnClickListener {
            it.projekt().animateTo(view3).start()
        }
        view3.setOnClickListener {
            it.projekt().animateTo(view2).start()
        }

        view4.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    view.projekt().drag()
                    false
                }
                else -> false
            }
        }

        standardViews.forEachIndexed { i, view ->
            view.projektionDragListener = object : DragListener {
                override fun onDragDropped(dragList: List<Drag>): Boolean {
                    toast("View ${i+1}")
                    return true
                }
            }
        }
    }
}
