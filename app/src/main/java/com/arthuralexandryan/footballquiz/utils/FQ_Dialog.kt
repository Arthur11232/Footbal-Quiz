package com.arthuralexandryan.footballquiz.utils

import android.app.Activity
import android.app.Dialog
import android.view.Display
import android.view.View
import android.view.Window
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.arthuralexandryan.footballquiz.R
import com.arthuralexandryan.footballquiz.models.DHM


fun openComingSoon(activity: Activity, dhm: DHM){
    val dialogs = Dialog(activity)
    val display: Display = activity.windowManager.defaultDisplay
    dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialogs.setCancelable(false)
    dialogs.setContentView(R.layout.coming_soon_layout)
    val container: RelativeLayout = dialogs.findViewById(R.id.container)
    container.layoutParams.width = display.width - (display.width / 5)
    container.layoutParams.height = display.height - (display.height / 4)
    val noBtn = dialogs.findViewById(R.id.close_popup) as AppCompatImageView
    noBtn.setOnClickListener { dialogs.dismiss() }
    initTimerViews(container, dhm)
    dialogs.show()
}

fun initTimerViews(container: RelativeLayout, dhm: DHM) {
    val daysItem: View = container.findViewById(R.id.timer_day)
    val hourItem: View = container.findViewById(R.id.timer_hour)
    val minuteItem: View = container.findViewById(R.id.timer_minute)

    val daysTxt = daysItem.findViewById<AppCompatTextView>(R.id.text_item_time)
    val daysCount = daysItem.findViewById<AppCompatTextView>(R.id.item_time)

    val hourTxt = hourItem.findViewById<AppCompatTextView>(R.id.text_item_time)
    val hourCount = hourItem.findViewById<AppCompatTextView>(R.id.item_time)

    val minuteTxt = minuteItem.findViewById<AppCompatTextView>(R.id.text_item_time)
    val minuteCount = minuteItem.findViewById<AppCompatTextView>(R.id.item_time)

    daysTxt.text = "Days"
    hourTxt.text = "Hour"
    minuteTxt.text = "Minute"

    daysCount.text = dhm.days.toString()
    hourCount.text = dhm.hours.toString()
    minuteCount.text = dhm.minute.toString()

}
