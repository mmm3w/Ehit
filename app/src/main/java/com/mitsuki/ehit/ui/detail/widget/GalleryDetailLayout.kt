package com.mitsuki.ehit.ui.detail.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.core.view.*
import androidx.recyclerview.widget.RecyclerView
import com.mitsuki.ehit.R

@Suppress("JoinDeclarationAndAssignment", "ConstantConditionIf")
class GalleryDetailLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr), NestedScrollingParent2 {

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

    private var mTopToPreview = 0f

    private var mBarMoveEvent: ((Float) -> Unit)? = null
    private var stateBack: ((Float) -> Unit)? = null

    init {
        addView(mInfoView)
        addView(mPreviewView)

        mInfoView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                mExtraScrolled -= dy
                moveTitle()
            }
        })
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
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
                stateBack?.invoke(mCurrentOffset)
                moveTitle()
            }
        }

        if (dy < 0) {
            if (!mPreviewView.canScrollVertically(-1)) {
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
                stateBack?.invoke(mCurrentOffset)
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
        mBarMoveEvent?.invoke(mExtraScrolled + mCurrentOffset)

//        mTitleBar.translationY =
//            (mExtraScrolled + mCurrentOffset).coerceIn((-mTitleOffsetRange).toFloat(), 0f)
    }

    /**********************************************************************************************/
    fun infoList(action: (RecyclerView.() -> Unit)? = null): RecyclerView {
        return action?.run { mInfoView.apply(this) } ?: mInfoView
    }

    fun previewList(action: (RecyclerView.() -> Unit)? = null): RecyclerView {
        return action?.run { mPreviewView.apply(this) } ?: mPreviewView
    }

    fun restoreTranslationY(value: Float) {
        mCurrentOffset = value
        mInfoView.translationY = mCurrentOffset
        mPreviewView.translationY = mCurrentOffset
    }

    fun bindBarMove(action: ((Float) -> Unit)? = null) {
        mBarMoveEvent = action
    }

    fun bindState(action: ((Float) -> Unit)? = null) {
        stateBack = action
    }


//    fun setListener(pageJumpListener: (() -> Unit)? = null) {
//        pageJumpListener?.apply {
//            mTitleBar.findViewById<ImageView>(R.id.top_title_page_jump)?.setOnClickListener {
//                //通过动画对其title和preview
//                autoAlign()
//                this()
//            }
//        }
//    }
}