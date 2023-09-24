package com.example.points_manager.presentation.utils

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class PointItemDecoration(verticalSpace: Int, context: Context): RecyclerView.ItemDecoration() {

    private val verticalSpaceDp = verticalSpace * context.resources.displayMetrics.density

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildLayoutPosition(view)
        if(position != 0) {
            outRect.top = verticalSpaceDp.toInt()
        }
    }

}