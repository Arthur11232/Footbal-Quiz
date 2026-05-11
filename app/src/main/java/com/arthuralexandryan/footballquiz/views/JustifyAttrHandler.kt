package com.arthuralexandryan.footballquiz.views

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import androidx.core.content.ContextCompat

internal class JustifyAttrHandler(private val mContext: Context, private val mAttributeSet: AttributeSet?) {

    private val KEY_TEXT_SIZE = "textSize"
    private val mRes: Resources = mContext.resources
    private val namespace = "IF_YOU_CAN"

    val textValue: String
        get() {
            if (mAttributeSet == null) return ""
            val KEY_TEXT = "text"
            var value = mAttributeSet.getAttributeValue(namespace, KEY_TEXT) ?: return ""

            if (value.length > 1 && value.startsWith("@") && value.contains("@string/")) {
                val resId = mRes.getIdentifier(mContext.packageName + ":" + value.substring(1), null, null)
                if (resId != 0) {
                    try {
                        value = mRes.getString(resId)
                    } catch (e: Resources.NotFoundException) {
                        // ignore
                    }
                }
            }

            return value
        }

    val colorValue: Int
        get() {
            if (mAttributeSet == null) return Color.BLACK
            val KEY_TEXT_COLOR = "textColor"
            val value = mAttributeSet.getAttributeValue(namespace, KEY_TEXT_COLOR) ?: return Color.BLACK

            if (value.length > 1 && value.startsWith("@") && value.contains("@color/")) {
                val resId = mRes.getIdentifier(mContext.packageName + ":" + value.substring(1), null, null)
                if (resId != 0) {
                    return ContextCompat.getColor(mContext, resId)
                }
            }

            return try {
                Color.parseColor(value)
            } catch (e: Exception) {
                Color.BLACK
            }
        }

    val textSize: Int
        get() {
            if (mAttributeSet == null) return 15
            val value = mAttributeSet.getAttributeValue(namespace, KEY_TEXT_SIZE) ?: return 15

            if (value.length > 1 && value.startsWith("@") && value.contains("@dimen/")) {
                val resId = mRes.getIdentifier(mContext.packageName + ":" + value.substring(1), null, null)
                if (resId != 0) {
                    return mRes.getDimensionPixelSize(resId)
                }
            }

            return try {
                value.substring(0, value.length - 2).toInt()
            } catch (e: Exception) {
                15
            }
        }

    val textSizeUnit: Int
        get() {
            if (mAttributeSet == null) return TypedValue.COMPLEX_UNIT_SP
            val value = mAttributeSet.getAttributeValue(namespace, KEY_TEXT_SIZE) ?: return TypedValue.COMPLEX_UNIT_SP

            return try {
                val type = value.substring(value.length - 2)
                when (type) {
                    "dp" -> TypedValue.COMPLEX_UNIT_DIP
                    "sp" -> TypedValue.COMPLEX_UNIT_SP
                    "pt" -> TypedValue.COMPLEX_UNIT_PT
                    "mm" -> TypedValue.COMPLEX_UNIT_MM
                    "in" -> TypedValue.COMPLEX_UNIT_IN
                    "px" -> TypedValue.COMPLEX_UNIT_PX
                    else -> -1
                }
            } catch (e: Exception) {
                -1
            }
        }
}
