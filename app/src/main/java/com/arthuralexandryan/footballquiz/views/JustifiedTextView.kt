package com.arthuralexandryan.footballquiz.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint.Align
import android.graphics.Rect
import android.graphics.Typeface
import android.text.TextPaint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewTreeObserver

class JustifiedTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    private val textPaint = TextPaint(TextPaint.ANTI_ALIAS_FLAG).apply {
        textAlign = Align.LEFT
        color = Color.WHITE
    }

    private var lineHeight: Int = 0
    private var textAreaWidth: Int = 0
    private var measuredViewHeight: Int = 0
    private var measuredViewWidth: Int = 0

    var text: String = ""
        set(value) {
            field = value
            calculate()
            invalidate()
        }

    private val lineList = mutableListOf<String>()

    private var hasTextBeenDrawn = false
    private var isBoldStarted = false

    init {
        setup(attrs)
    }

    private fun setup(attrs: AttributeSet?) {
        val mXmlParser = JustifyAttrHandler(context, attrs)

        attrs?.let {
            val textValue = mXmlParser.textValue
            val textColor = mXmlParser.colorValue
            val textSizeValue = mXmlParser.textSize.toFloat()
            val textSizeUnit = mXmlParser.textSizeUnit

            text = textValue
            setTextColor(textColor)

            if (textSizeUnit == -1) {
                setTextSize(textSizeValue)
            } else {
                setTextSize(textSizeUnit, textSizeValue)
            }
        }

        viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (hasTextBeenDrawn) {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                    return
                }

                if (width > 0) {
                    hasTextBeenDrawn = true
                    textAreaWidth = width - (paddingLeft + paddingRight)
                    calculate()
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            }
        })
    }

    private fun calculate() {
        if (textAreaWidth <= 0) return

        updateLineHeight()
        lineList.clear()
        lineList.addAll(divideOriginalTextToStringLineList(text))

        setMeasuredDimensions(lineList.size, lineHeight, lineSpace = 0)
        requestLayout()
    }

    private fun updateLineHeight() {
        val bounds = Rect()
        val sampleStr = "It may be a federal and/or state offense to install monitoring"
        textPaint.getTextBounds(sampleStr, 0, sampleStr.length, bounds)
        lineHeight = (bounds.height() * 1.5).toInt()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (measuredViewWidth > 0) {
            setMeasuredDimension(measuredViewWidth, measuredViewHeight)
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        var rowIndex = paddingTop
        val colIndex = if (textPaint.textAlign == Align.RIGHT) {
            paddingLeft + textAreaWidth
        } else {
            paddingLeft
        }

        isBoldStarted = false

        lineList.forEach { currentLine ->
            rowIndex += lineHeight

            var lineToDraw = currentLine

            if (lineToDraw.contains("#@")) {
                isBoldStarted = true
            }

            if (isBoldStarted) {
                textPaint.typeface = Typeface.DEFAULT_BOLD
                textPaint.color = Color.WHITE
                lineToDraw = lineToDraw.replace("#@", "")
            }

            if (lineToDraw.contains("@#")) {
                isBoldStarted = false
                lineToDraw = lineToDraw.replace("@#", "")
            }

            canvas.drawText(lineToDraw, colIndex.toFloat(), rowIndex.toFloat(), textPaint)

            if (!isBoldStarted) {
                textPaint.typeface = Typeface.DEFAULT
                textPaint.color = Color.WHITE
            }
        }
    }

    private fun divideOriginalTextToStringLineList(originalText: String): List<String> {
        val resultList = mutableListOf<String>()
        val paragraphs = originalText.split("\n")

        var isStarted = false
        var isEnded = true

        for (paragraph in paragraphs) {
            val words = paragraph.split(" ")
            var currentLine = ""

            var i = 0
            while (i < words.size) {
                val testLine = if (currentLine.isEmpty()) words[i] else "$currentLine ${words[i]}"
                val testWidth = textPaint.measureText(testLine)

                when {
                    textAreaWidth.toFloat() == testWidth -> {
                        resultList.add(testLine)
                        currentLine = ""
                        i++
                    }

                    textAreaWidth.toFloat() < testWidth -> {
                        if (currentLine.trim().isEmpty()) {
                            // Single word is longer than area width, force add it
                            resultList.add(words[i])
                            i++
                            continue
                        }

                        if (currentLine.contains("#@")) isStarted = true
                        if (currentLine.contains("@#")) isEnded = false

                        if (isStarted && isEnded) {
                            resultList.add(currentLine)
                        } else {
                            val justifiedLine = justifyTextLine(currentLine.trim(), textAreaWidth)
                            resultList.add(justifiedLine)
                        }
                        currentLine = ""
                        // Word[i] will be processed in next iteration as currentLine is reset
                    }

                    else -> {
                        currentLine = testLine
                        if (i == words.size - 1) {
                            resultList.add(currentLine)
                            isEnded = false
                            currentLine = ""
                        }
                        i++
                    }
                }
            }
        }
        return resultList
    }

    private fun justifyTextLine(lineString: String, width: Int): String {
        var result = lineString
        var gapIndex = 0
        var lineWidth = textPaint.measureText(result)

        while (lineWidth < width && lineWidth > 0) {
            gapIndex = result.indexOf(" ", gapIndex + 2)
            if (gapIndex == -1) {
                gapIndex = result.indexOf(" ", 0 + 1)
                if (gapIndex == -1) return result
            }

            result = result.substring(0, gapIndex) + "  " + result.substring(gapIndex + 1)
            lineWidth = textPaint.measureText(result)
        }
        return result
    }

    private fun setMeasuredDimensions(lineListSize: Int, h: Int, lineSpace: Int) {
        val mHeight = lineListSize * (h + lineSpace) + lineSpace + paddingBottom + paddingTop
        measuredViewHeight = mHeight
        measuredViewWidth = width
    }

    private fun setTextSize(size: Float) {
        textPaint.textSize = size
        calculate()
        invalidate()
    }

    private fun setTextSize(unit: Int, size: Float) {
        val calculatedSize = TypedValue.applyDimension(
            unit, size, context.resources.displayMetrics
        )
        setTextSize(calculatedSize)
    }

    fun setTextColor(color: Int) {
        textPaint.color = color
        invalidate()
    }
}
