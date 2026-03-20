package com.arthuralexandryan.footballquiz.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.core.content.ContextCompat;

class JustifyAttrHandler {

    private final String KEY_TEXT_SIZE = "textSize";
    private final Resources mRes;
    private final Context mContext;
    private final AttributeSet mAttributeSet;
    private final String namespace = "IF_YOU_CAN";

    JustifyAttrHandler(Context context, AttributeSet attributeSet) {
        mContext = context;
        mRes = mContext.getResources();
        mAttributeSet = attributeSet;


    }

    String getTextValue() {

        String KEY_TEXT = "text";
        String value = mAttributeSet.getAttributeValue(namespace, KEY_TEXT);

        if (value == null)
            return "";

        if (value.length() > 1 &&
                value.charAt(0) == '@' &&
                value.contains("@string/")) {
            int resId = mRes.getIdentifier(mContext.getPackageName() + ":" + value.substring(1), null, null);
            value = mRes.getString(resId);
        }

        return value;

    }

    public int getColorValue() {

        String KEY_TEXT_COLOR = "textColor";
        String value = mAttributeSet.getAttributeValue(namespace, KEY_TEXT_COLOR);

        int color = Color.BLACK;

        if (value == null)
            return color;

        if (value.length() > 1 &&
                value.charAt(0) == '@' &&
                value.contains("@color/")) {
            int resId = mRes.getIdentifier(mContext.getPackageName() + ":" + value.substring(1), null, null);
            color = ContextCompat.getColor(mContext,resId);

            return color;
        }


        try {
            color = Color.parseColor(value);
        } catch (Exception e) {
            return Color.BLACK;
        }

        return color;
    }


    public int getTextSize() {
        int textSize = 15;

        String value = mAttributeSet.getAttributeValue(namespace, KEY_TEXT_SIZE);

        if (value == null)
            return textSize;

        if (value.length() > 1 &&
                value.charAt(0) == '@' &&
                value.contains("@dimen/")) {
            int resId = mRes.getIdentifier(mContext.getPackageName() + ":" + value.substring(1), null, null);
            textSize = mRes.getDimensionPixelSize(resId);

            return textSize;
        }

        try {
            textSize = Integer.parseInt(value.substring(0, value.length() - 2));
        } catch (Exception e) {
            return 15;
        }

        return textSize;
    }

    public int getTextSizeUnit() {

        String value = mAttributeSet.getAttributeValue(namespace, KEY_TEXT_SIZE);

        if (value == null || value.length() < 2)
            return TypedValue.COMPLEX_UNIT_SP;

        try {
            String type = value.substring(value.length() - 2);

            switch (type) {
                case "dp":
                    return TypedValue.COMPLEX_UNIT_DIP;
                case "sp":
                    return TypedValue.COMPLEX_UNIT_SP;
                case "pt":
                    return TypedValue.COMPLEX_UNIT_PT;
                case "mm":
                    return TypedValue.COMPLEX_UNIT_MM;
                case "in":
                    return TypedValue.COMPLEX_UNIT_IN;
                case "px":
                    return TypedValue.COMPLEX_UNIT_PX;
            }
        } catch (Exception e) {
            return -1;
        }

        return -1;
    }

}
