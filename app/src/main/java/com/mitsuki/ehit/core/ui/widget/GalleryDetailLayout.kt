package com.mitsuki.ehit.core.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.*
import androidx.recyclerview.widget.RecyclerView
import com.mitsuki.armory.extend.*
import com.mitsuki.ehit.R

@Suppress("JoinDeclarationAndAssignment", "ConstantConditionIf")
class GalleryDetailLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr), NestedScrollingParent2 {

    private val mTitleBar =
        LayoutInflater.from(context).inflate(R.layout.top_bar_detail_ver, this, false)

    private val mInfoView: RecyclerView = RecyclerView(context).apply {
        id = R.id.gallery_detail_inner_info
        overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        clipToPadding = false
    }

    private val mPreviewView: RecyclerView = RecyclerView(context).apply {
        id = R.id.gallery_detail_inner_preview
        overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        clipToPadding = false
    }

    private var mMoveRange = 0f
    private var mCurrentOffset = 0f

    private var mExtraScrolled = 0f
    private var mTitleOffsetRange = 0

    private var mTopToPreview = 0f

    init {
        addView(mInfoView)
        addView(mPreviewView)
        addView(mTitleBar)

        mInfoView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                mExtraScrolled -= dy
                moveTitle()
            }
        })
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        mTitleBar.measure(
            widthMeasureSpec,
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.AT_MOST)
        )

        mTitleOffsetRange = mTitleBar.measuredHeight

        mInfoView.setPadding(0, mTitleBar.measuredHeight, 0, 0)
        mInfoView.measure(
            widthMeasureSpec,
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.AT_MOST)
        )

        mTopToPreview = mInfoView.measuredHeight.toFloat()

        mPreviewView.measure(
            widthMeasureSpec,
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.AT_MOST)
        )

        mMoveRange = (mPreviewView.measuredHeight + mInfoView.measuredHeight -
                MeasureSpec.getSize(heightMeasureSpec)).toFloat()

        setMeasuredDimension(
            MeasureSpec.getSize(widthMeasureSpec),
            MeasureSpec.getSize(heightMeasureSpec)
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        mTitleBar.apply { layout(0, 0, measuredWidth, measuredHeight) }

        mInfoView.apply { layout(0, 0, measuredWidth, measuredHeight) }

        mPreviewView.apply {
            layout(
                0, mInfoView.measuredHeight,
                measuredWidth, measuredHeight + mInfoView.measuredHeight
            )
        }
    }


    /** nested scroll *****************************************************************************/
    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        return axes and ViewCompat.SCROLL_AXIS_VERTICAL != 0 && mMoveRange > 0
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
        mInfoView.stopScroll()
        mPreviewView.stopScroll()
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        if (dy > 0) {
            if (!mInfoView.canScrollVertically(1)) {
                if (mCurrentOffset - dy < -mMoveRange) {
                    consumed[1] = (mMoveRange + mCurrentOffset).toInt()
                    if (type == ViewCompat.TYPE_NON_TOUCH) {
                        //fling传递
                        if (mPreviewView.canScrollVertically(1) && target != mPreviewView) {
                            mPreviewView.scrollBy(0, (dy - mMoveRange - mCurrentOffset).toInt())
                        }
                    }
                    mCurrentOffset = -mMoveRange
                } else {
                    consumed[1] = dy
                    mCurrentOffset -= dy
                }
                mInfoView.translationY = mCurrentOffset
                mPreviewView.translationY = mCurrentOffset
                moveTitle()
            }
        }

        if (dy < 0) {
            if (!mPreviewView.canScrollVertically(-1) && endOfPrepend) {
                if (mCurrentOffset - dy > 0) {
                    consumed[1] = (mCurrentOffset).toInt()
                    if (mInfoView.canScrollVertically(-1)) {
                        if (type == ViewCompat.TYPE_NON_TOUCH && target != mInfoView) {
                            mInfoView.scrollBy(0, (dy - mCurrentOffset).toInt())
                        }
                    }
                    mCurrentOffset = 0f
                } else {
                    consumed[1] = dy
                    mCurrentOffset -= dy
                }
                mInfoView.translationY = mCurrentOffset
                mPreviewView.translationY = mCurrentOffset
                moveTitle()
            }
        }


    }

    override fun onNestedScroll(
        target: View, dxConsumed: Int, dyConsumed: Int,
        dxUnconsumed: Int, dyUnconsumed: Int, type: Int
    ) {

    }

    override fun onStopNestedScroll(target: View, type: Int) {

    }

    /**********************************************************************************************/
    private fun moveTitle() {

        if (mCurrentOffset + mTopToPreview == 0f) {
            //显示按钮
        } else {
            //隐藏按钮
        }

        mTitleBar.translationY =
            (mExtraScrolled + mCurrentOffset).coerceIn((-mTitleOffsetRange).toFloat(), 0f)
    }

    private fun autoAlign() {

    }

    /**********************************************************************************************/

    var endOfPrepend: Boolean = true

    fun infoList(action: (RecyclerView.() -> Unit)? = null): RecyclerView {
        return action?.run { mInfoView.apply(this) } ?: mInfoView
    }

    fun previewList(action: (RecyclerView.() -> Unit)? = null): RecyclerView {
        return action?.run { mPreviewView.apply(this) } ?: mPreviewView
    }

    fun setListener(pageJumpListener: (() -> Unit)? = null) {
        pageJumpListener?.apply {
            mTitleBar.findViewById<ImageView>(R.id.top_title_page_jump)?.setOnClickListener {
                //通过动画对其title和preview
                autoAlign()
                this()
            }
        }
    }
}