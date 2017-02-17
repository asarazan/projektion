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
        fun getProjektionView(view: View): ViewGroup = view.parentWithClass<ProjektionFrameLayout>() ?: view.rootView as ViewGroup
    }

    val view: View
    val ghostView: ImageView

    internal val viewGroup: ViewGroup

    constructor(view: View, viewGroup: ViewGroup) : super() {
        this.view = view
        this.viewGroup = viewGroup

        ghostView = ImageView(viewGroup.context)
        ghostView.setTag(R.id.tag_projektion_ghost, true)
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

    fun drag() {
        if (viewGroup is ProjektionFrameLayout) {
            viewGroup.drag(this)
        } else {
            throw RuntimeException("Must use ProjektionFrameLayout to utilize drag functionality.")
        }
    }

    fun destroy() {
        // not sure why this nullable is necessary, but it prevents ultra-rare crashes.
        (ghostView.parent as ViewGroup?)?.removeView(ghostView)
    }

    data class Drag(val projektion: Projektion) {
        var startX: Float? = null
        var startY: Float? = null
    }

    interface DragListener {
        fun onDragDropped(dragList: List<Drag>): Boolean { return false }
        fun onDragCanceled(dragList: List<Drag>): Boolean { return false }
        fun onDragFailed(dragList: List<Drag>): Boolean { return false }
    }

    internal class DestroyListener(val projektion: Projektion) : Animator.AnimatorListener {
        override fun onAnimationEnd(p0: Animator?) = projektion.destroy()
        override fun onAnimationCancel(p0: Animator?) = projektion.destroy()
        override fun onAnimationRepeat(p0: Animator?) {}
        override fun onAnimationStart(p0: Animator?) {}
    }
}