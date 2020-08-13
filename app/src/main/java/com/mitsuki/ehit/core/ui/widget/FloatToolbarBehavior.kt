package com.mitsuki.ehit.core.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.mitsuki.armory.extend.marginVertical

/**
 * 依赖于RecyclerView的顶部浮动View
 * 包含沉浸式处理
 * 需要为view添加 android:fitsSystemWindows="true" 让系统添加状态栏的padding
 * 注意不要为该View设置纵向padding值，可能会出现位置异常
 */
class FloatToolbarBehavior : CoordinatorLayout.Behavior<View> {

    constructor() : super()
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    private var floatViewHeight = -1f
    private var offsetLimit = -1f

//    var canTargetScrollUp: (() -> Boolean)? = null  //某View能否被下拉的回调

    private var floatViewOffset = 0f
    private var scrollViewOffset = 0f
    private var attachedTag = false

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        return dependency is RecyclerView
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {

        //这部必须在这里，保证操作之前滚动列表位置加载正确
        //因为该方法在绑定的时候必定会回调一次
        if (floatViewHeight < 0) {
            floatViewHeight = (child.measuredHeight + child.marginVertical()).toFloat()
            //减去view底部被系统填充的底部padding
            // (虽然也会减去自己设置的padding，所以这个view尽量不要设置纵向的padding)
            if (ViewCompat.getFitsSystemWindows(child)) floatViewHeight -= child.paddingBottom
            floatViewOffset = 0f
            scrollViewOffset = floatViewHeight
            return true
        }

        if (attachedTag) {
            dependency.translationY = scrollViewOffset
            child.translationY = floatViewOffset
            attachedTag = false
        }

        return false
    }

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        return (axes and ViewCompat.SCROLL_AXIS_VERTICAL) != 0
    }

    override fun onNestedPreScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray,
        type: Int
    ) {
        if (floatViewHeight < 0) return
        //这个方法必须在这里
        //因为当view绑定的时候，recyclerview中不一定有数据
        //recyclerview不一定拥有高度
        //所以需要最后操作的时候才能知道recyclerview的明确的高度
        if (offsetLimit < 0) {
            offsetLimit =
                (target.measuredHeight + target.marginVertical() + floatViewHeight - coordinatorLayout.measuredHeight)
                    .coerceIn(0f, floatViewHeight)
        }

        if (dy > 0) {
            if (floatViewOffset > -offsetLimit) {
                floatViewOffset = (floatViewOffset - dy).coerceIn(-offsetLimit, 0F)
                child.translationY = floatViewOffset
            }
            if (scrollViewOffset > floatViewHeight - offsetLimit) {
                scrollViewOffset =
                    (scrollViewOffset - dy).coerceIn(
                        floatViewHeight - offsetLimit,
                        floatViewHeight
                    )
                target.translationY = scrollViewOffset
                consumed[1] = dy
            }
        }

        if (dy < 0) {
            //向下滑动
            if (floatViewOffset < 0) {
                floatViewOffset = (floatViewOffset - dy).coerceIn(-floatViewHeight, 0F)
                child.translationY = floatViewOffset
            }

            if (!canTargetScrollUp(target) && scrollViewOffset < floatViewHeight) {
                scrollViewOffset =
                    (scrollViewOffset - dy).coerceIn(
                        floatViewHeight - offsetLimit,
                        floatViewHeight
                    )
                target.translationY = scrollViewOffset
                consumed[1] = dy
            }
        }
    }

    override fun onAttachedToLayoutParams(params: CoordinatorLayout.LayoutParams) {
        //fragment重建View的时候会重新设置behavior
        //进行tag标记
        attachedTag = true
    }

    private fun canTargetScrollUp(target: View): Boolean {
//        return canTargetScrollUp?.run { invoke() } ?: target.canScrollVertically(-1)
        return target.canScrollVertically(-1)
    }
}