package com.mitsuki.ehit.adaptation

import android.graphics.Rect
import android.os.Build
import androidx.drawerlayout.widget.DrawerLayout

/**
 * DrawerLayout android10 手势区域排除
 * 因为是左侧划出，所有排除了左上角1/4的一块区域
 */
fun DrawerLayout.gestureExclusion() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        context.resources.displayMetrics?.apply {
            systemGestureExclusionRects = listOf(Rect(0, 0, widthPixels / 3, heightPixels))
        }
    }
}