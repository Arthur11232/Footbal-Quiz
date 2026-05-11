package com.arthuralexandryan.footballquiz.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.RadioGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.arthuralexandryan.footballquiz.R
import com.arthuralexandryan.footballquiz.models.DHM

object DialogManager {

    fun showActionDialog(
        context: Context,
        inflater: LayoutInflater,
        title: CharSequence,
        message: CharSequence,
        primaryText: CharSequence,
        secondaryText: CharSequence,
        cancelOutside: Boolean = true,
        onDismiss: (() -> Unit)? = null,
        onPrimaryClick: () -> Unit,
        onSecondaryClick: (() -> Unit)? = null
    ): Dialog {
        val dialogView = inflater.inflate(R.layout.dialog_cloud_sync, null)
        val dialog = createStyledDialog(context, dialogView, cancelOutside, onDismiss)

        dialogView.findViewById<TextView>(R.id.dialogTitle).text = title
        dialogView.findViewById<TextView>(R.id.dialogMessage).text = message

        dialogView.findViewById<AppCompatButton>(R.id.btnPrimary).apply {
            text = primaryText
            setOnClickListener {
                dialog.dismiss()
                onPrimaryClick()
            }
        }

        dialogView.findViewById<AppCompatButton>(R.id.btnSecondary).apply {
            text = secondaryText
            setOnClickListener {
                dialog.dismiss()
                onSecondaryClick?.invoke()
            }
        }

        dialog.show()
        return dialog
    }

    fun showNewGameDialog(
        context: Context,
        inflater: LayoutInflater,
        onConfirm: () -> Unit
    ): Dialog {
        val dialogView = inflater.inflate(R.layout.dialog_new_game, null)
        val dialog = createStyledDialog(context, dialogView, cancelOutside = true)

        dialogView.findViewById<AppCompatButton>(R.id.btn_yes).setOnClickListener {
            dialog.dismiss()
            onConfirm()
        }
        dialogView.findViewById<AppCompatButton>(R.id.btn_no).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        return dialog
    }

    fun showLanguageDialog(
        context: Context,
        inflater: LayoutInflater,
        checkedLanguageId: Int,
        onLanguageSelected: (RadioGroup, Int) -> Unit,
        onSave: () -> Unit
    ): Dialog {
        val dialogView = inflater.inflate(R.layout.dialog_language, null)
        val dialog = createStyledDialog(context, dialogView, cancelOutside = true)

        val group = dialogView.findViewById<RadioGroup>(R.id.app_languages)
        group.check(checkedLanguageId)
        group.setOnCheckedChangeListener { radioGroup, checkedId ->
            onLanguageSelected(radioGroup, checkedId)
        }

        dialogView.findViewById<AppCompatButton>(R.id.save_language).setOnClickListener {
            dialog.dismiss()
            onSave()
        }

        dialog.show()
        return dialog
    }

    fun showComingSoonDialog(activity: Activity, dhm: DHM): Dialog {
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.coming_soon_layout)

        val displayMetrics = activity.resources.displayMetrics
        val container: RelativeLayout = dialog.findViewById(R.id.container)
        container.layoutParams.width = displayMetrics.widthPixels - (displayMetrics.widthPixels / 5)
        container.layoutParams.height = displayMetrics.heightPixels - (displayMetrics.heightPixels / 4)

        dialog.findViewById<AppCompatImageView>(R.id.close_popup).setOnClickListener {
            dialog.dismiss()
        }

        initTimerViews(container, dhm)
        dialog.show()
        return dialog
    }

    private fun createStyledDialog(
        context: Context,
        contentView: View,
        cancelOutside: Boolean,
        onDismiss: (() -> Unit)? = null
    ): Dialog {
        return Dialog(context, R.style.FQ_CustomDialog).apply {
            setContentView(contentView)
            setCanceledOnTouchOutside(cancelOutside)
            setOnDismissListener { onDismiss?.invoke() }
            window?.apply {
                setBackgroundDrawableResource(android.R.color.transparent)
                setLayout(
                    (context.resources.displayMetrics.widthPixels * 0.9).toInt(),
                    WindowManager.LayoutParams.WRAP_CONTENT
                )
            }
        }
    }

    private fun initTimerViews(container: RelativeLayout, dhm: DHM) {
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
}
