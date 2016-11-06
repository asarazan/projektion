package net.sarazan.projektion

import android.animation.Animator
import android.view.View
import android.view.ViewGroup
import android.view.ViewPropertyAnimator
import android.widget.ImageView

/**
 * Created by Aaron Sarazan on 11/6/16
 */
class Projektion {
    companion object {

    }

    internal val view: View
    internal val viewGroup: ViewGroup
    internal val ghost: ImageView

    constructor(view: View, viewGroup: ViewGroup) : super() {
        this.view = view
        this.viewGroup = viewGroup

        ghost = ImageView(viewGroup.context)
        viewGroup.addView(ghost)
        (ghost.layoutParams as ViewGroup.LayoutParams).apply {
            height = view.measuredHeight
            width = view.measuredWidth
        }
        ghost.setImageBitmap(view.bitmap)
        moveTo(this.view)
    }

    fun animateTo(view: View, andDestroy: Boolean = true): ViewPropertyAnimator {
        val dest = view.getBoundsIn(viewGroup)
        return ghost.animate()
                .x(dest.left.toFloat())
                .y(dest.top.toFloat())
                .apply { if (andDestroy) { setListener(DestroyListener(this@Projektion)) } }
    }

    fun moveTo(view: View) {
        val dest = view.getBoundsIn(viewGroup)
        this.ghost.apply {
            translationX = dest.left.toFloat()
            translationY = dest.top.toFloat()
        }
    }

    fun destroy() {
        (ghost.parent as ViewGroup).removeView(ghost)
    }

    internal class DestroyListener(val projektion: Projektion) : Animator.AnimatorListener {
        override fun onAnimationEnd(p0: Animator?) = projektion.destroy()
        override fun onAnimationCancel(p0: Animator?) = projektion.destroy()
        override fun onAnimationRepeat(p0: Animator?) {}
        override fun onAnimationStart(p0: Animator?) {}
    }
}