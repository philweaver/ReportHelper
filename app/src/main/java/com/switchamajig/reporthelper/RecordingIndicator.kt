package com.switchamajig.reporthelper

import android.content.Context
import android.graphics.PixelFormat
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout

class RecordingIndicator(context: Context) {
    private val layout: FrameLayout
    private var isVisible = false

    var visible
        get() = isVisible
        set(newVisibility) = setVisibleInternal(newVisibility)

    init {
        layout = FrameLayout(context)
        val wm = context.getSystemService(Context.WINDOW_SERVICE)
        if (wm is WindowManager) {
            val lp = WindowManager.LayoutParams()
            lp.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
            lp.format = PixelFormat.TRANSLUCENT
            lp.flags = lp.flags or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.MATCH_PARENT
            LayoutInflater.from(context).inflate(R.layout.recording_indicator, layout)
            layout.visibility = View.INVISIBLE
            wm.addView(layout, lp)
        }
    }

    private fun setVisibleInternal(newVisibility: Boolean) {
        layout.visibility = if (newVisibility) View.VISIBLE else View.INVISIBLE
    }
}
