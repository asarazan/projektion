package net.sarazan.projektion.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import net.sarazan.projektion.projekt

class MainActivity : AppCompatActivity() {

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
    }
}
