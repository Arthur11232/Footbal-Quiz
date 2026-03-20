package com.arthuralexandryan.footballquiz.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class JustifiedTextView extends View {

    private Context mContext;

    private TextPaint textPaint;

    private int lineHeight;

    private int textAreaWidth;

    private int measuredViewHeight, measuredViewWidth;

    private String text;

    @NonNull
    private List<String> lineList = new ArrayList<>();

    /**
     * when we want to draw text after view created to avoid loop in drawing we use this boolean
     */
    private boolean hasTextBeenDrown = false;
    private String line;
    private boolean isBoldStarted;
    private boolean isStarted;
    private boolean isEnded = true;

    public JustifiedTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        constructor(context, attrs);
    }

    public JustifiedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        constructor(context, attrs);
    }

    public JustifiedTextView(Context context) {
        super(context);
        constructor(context, null);

    }

    private void constructor(Context context, @Nullable AttributeSet attrs) {

        mContext = context;
        JustifyAttrHandler mXmlParser = new JustifyAttrHandler(mContext, attrs);


        initTextPaint();

        if (attrs != null) {
            String text;

            text = mXmlParser.getTextValue();
            int textColor = mXmlParser.getColorValue();
            int textSize = mXmlParser.getTextSize();
            int textSizeUnit = mXmlParser.getTextSizeUnit();


            setText(text);
            setTextColor(textColor);
            if (textSizeUnit == -1)
                setTextSize(textSize);
            else
                setTextSize(textSizeUnit, textSize);

        }

        ViewTreeObserver observer = getViewTreeObserver();


        observer.addOnGlobalLayoutListener(() -> {

            if (hasTextBeenDrown)
                return;
            hasTextBeenDrown = true;
            setTextAreaWidth(getWidth() - (getPaddingLeft() + getPaddingRight()));
            calculate();

        });

    }

    private void calculate() {
        setLineHeight(getTextPaint());
        lineList.clear();
//        lineList = divideOriginalTextToStringLineList(getText());
        lineList = divideOriginalTextToStringLineList(getText());
//        initMap();
        setMeasuredDimentions(lineList.size(), getLineHeight(), getLineSpace());
        measure(getMeasuredViewWidth(), getMeasuredViewHeight());
    }

    private void initTextPaint() {
        textPaint = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
        textPaint.setTextAlign(Align.LEFT);
        textPaint.setColor(Color.WHITE);
        TextPaint boldTextPaint = getTextPaint();
        boldTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (getMeasuredViewWidth() > 0) {
            requestLayout();
            setMeasuredDimension(getMeasuredViewWidth(), getMeasuredViewHeight());
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
        invalidate();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {

        int rowIndex = getPaddingTop();
        int colIndex;
        if (getAlignment() == Align.RIGHT)
            colIndex = getPaddingLeft() + getTextAreaWidth();
        else
            colIndex = getPaddingLeft();

        for (int i = 0; i < lineList.size(); i++) {
            rowIndex += getLineHeight() + getLineSpace();

            // TODO: 23.03.2017 The part which makes titles BOLD

            if (lineList.get(i).contains("#@")) {
                isBoldStarted = true;
            } else {
                line = lineList.get(i);
            }

            if (isBoldStarted) {
                textPaint.setTypeface(Typeface.DEFAULT_BOLD);
                textPaint.setColor(Color.WHITE);
                line = lineList.get(i).replaceAll("#@", "");
            }

            if (line.contains("@#")) {
                isBoldStarted = false;
                line = line.replace("@#", "");
            }

            canvas.drawText(line, colIndex, rowIndex, textPaint);

            if (!isBoldStarted) {
                textPaint.setTypeface(Typeface.DEFAULT);
                textPaint.setColor(Color.WHITE);
            }
            // endregion
        }
    }


    @NonNull
    private List<String> divideOriginalTextToStringLineList(String originalText) {

        List<String> listStringLine = new ArrayList<>();

        String line = "";
        float textWidth;

        String[] listParageraphes = originalText.split("\n");

        for (String listParagraph : listParageraphes) {
            String[] arrayWords = listParagraph.split(" ");

            for (int i = 0; i < arrayWords.length; i++) {

                line += arrayWords[i] + " ";
                textWidth = getTextPaint().measureText(line);

                //if text width is equal to textAreaWidth then just add it to ListStringLine
                if (getTextAreaWidth() == textWidth) {

                    listStringLine.add(line);
                    line = "";//make line clear
                    continue;
                }
                //else if text width excite textAreaWidth then remove last word and justify the StringLine
                else if (getTextAreaWidth() < textWidth) {

                    int lastWordCount = arrayWords[i].length();

                    //remove last word that cause line width to excite textAreaWidth
                    line = line.substring(0, line.length() - lastWordCount - 1);

                    // if line is empty then should be skipped
                    if (line.trim().length() == 0)
                        continue;

                    // TODO: 23.03.2017 The part which skip's the titles justification
                    if (line.contains("#@")) {
                        isStarted = true;
                    }
                    if (line.contains("@#")) {
                        isEnded = false;
                    }
                    if (isStarted && isEnded) {
                        listStringLine.add(line);
                        line = "";
                        continue;
                    }
                    // endregion

                    //and then we need to justify line
                    line = justifyTextLine(textPaint, line.trim(), getTextAreaWidth());

                    listStringLine.add(line);
                    line = "";
                    i--;
                    continue;
                }

                //if we are now at last line of paragraph then just add it
                if (i == arrayWords.length - 1) {
                    listStringLine.add(line);
                    // TODO: 23.03.2017 The part which skip's the titles justification
                    isEnded = false;
                    line = "";
                }
            }
        }

        return listStringLine;

    }


    @NonNull
    private String justifyTextLine(TextPaint textPaint, @NonNull String lineString, int textAreaWidth) {

        int gapIndex = 0;

        float lineWidth = textPaint.measureText(lineString);

        while (lineWidth < textAreaWidth && lineWidth > 0) {

            gapIndex = lineString.indexOf(" ", gapIndex + 2);
            if (gapIndex == -1) {
                gapIndex = 0;
                gapIndex = lineString.indexOf(" ", gapIndex + 1);
                if (gapIndex == -1)
                    return lineString;
            }

            lineString = lineString.substring(0, gapIndex) + "  " + lineString.substring(gapIndex + 1);

            lineWidth = textPaint.measureText(lineString);
        }
        return lineString;
    }


    private void setLineHeight(TextPaint textPaint) {

        Rect bounds = new Rect();
        String sampleStr = "It may be a federal and/or state offense to install monitoring";
        textPaint.getTextBounds(sampleStr, 0, sampleStr.length(), bounds);

        setLineHeight((int) (bounds.height() * 1.5));

    }


    private void setMeasuredDimentions(int lineListSize, int lineHeigth, int lineSpace) {
        int mHeight = lineListSize * (lineHeigth + lineSpace) + lineSpace;

        mHeight += getPaddingRight() + getPaddingLeft();

        setMeasuredViewHeight(mHeight);

        setMeasuredViewWidth(getWidth());
    }


    private int getTextAreaWidth() {
        return textAreaWidth;
    }

    private void setTextAreaWidth(int textAreaWidth) {
        this.textAreaWidth = textAreaWidth;
    }

    private int getLineHeight() {
        return lineHeight;
    }

    private void setLineHeight(int lineHeight) {
        this.lineHeight = lineHeight;
    }

    private int getMeasuredViewHeight() {
        return measuredViewHeight;
    }

    private void setMeasuredViewHeight(int measuredViewHeight) {
        this.measuredViewHeight = measuredViewHeight;
    }

    private int getMeasuredViewWidth() {
        return measuredViewWidth;
    }

    private void setMeasuredViewWidth(int measuredViewWidth) {
        this.measuredViewWidth = measuredViewWidth;
    }

    private String getText() {
        return text;
    }


    public void setText(String text) {
        this.text = text;
        calculate();
        invalidate();
    }

    private void setTextSize(float textSize) {
        getTextPaint().setTextSize(textSize);
        calculate();
        invalidate();
    }

    private void setTextSize(int unit, float textSize) {
        textSize = TypedValue.applyDimension(unit, textSize, mContext.getResources().getDisplayMetrics());
        setTextSize(textSize);
    }

    private TextPaint getTextPaint() {
        return textPaint;
    }


    private void setTextColor(int textColor) {
        getTextPaint().setColor(textColor);
        invalidate();
    }


    private int getLineSpace() {
        return 0;
    }



    private Align getAlignment() {
        return getTextPaint().getTextAlign();
    }


}
