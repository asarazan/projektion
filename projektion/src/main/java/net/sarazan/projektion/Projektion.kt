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
    companion object

    val view: View
    val ghostView: ImageView

    internal val viewGroup: ViewGroup

    constructor(view: View, viewGroup: ViewGroup) : super() {
        this.view = view
        this.viewGroup = viewGroup

        ghostView = ImageView(viewGroup.context)
        viewGroup.addView(ghostView)
        (ghostView.layoutParams as ViewGroup.LayoutParams).apply {
            height = view.measuredHeight
            width = view.measuredWidth
        }
        ghostView.setImageBitmap(view.bitmap)
        moveTo(this.view)
    }

    fun animateTo(view: View, translateX: Float = 0f, translateY: Float = 0f, andDestroy: Boolean = true): ViewPropertyAnimator {
        val dest = view.getBoundsIn(viewGroup)
        return ghostView.animate()
                .x(dest.left.toFloat() + translateX)
                .y(dest.top.toFloat() + translateY)
                .apply { if (andDestroy) { setListener(DestroyListener(this@Projektion)) } }
    }

    fun moveTo(view: View, translateX: Float = 0f, translateY: Float = 0f) {
        val dest = view.getBoundsIn(viewGroup)
        this.ghostView.apply {
            translationX = dest.left.toFloat() + translateX
            translationY = dest.top.toFloat() + translateY
        }
    }

    fun destroy() {
        (ghostView.parent as ViewGroup).removeView(ghostView)
    }

    internal class DestroyListener(val projektion: Projektion) : Animator.AnimatorListener {
        override fun onAnimationEnd(p0: Animator?) = projektion.destroy()
        override fun onAnimationCancel(p0: Animator?) = projektion.destroy()
        override fun onAnimationRepeat(p0: Animator?) {}
        override fun onAnimationStart(p0: Animator?) {}
    }
}