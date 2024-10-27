package com.pennapps.labs.pennmobile.fitness.classes

import android.graphics.Canvas
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.buffer.BarBuffer
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.model.GradientColor
import com.github.mikephil.charting.renderer.BarChartRenderer
import com.github.mikephil.charting.utils.ViewPortHandler

class RoundedBarChartRenderer constructor(
    chart: BarChart,
    animator: ChartAnimator,
    vpHandler: ViewPortHandler,
    cornersDimen: Float,
) : BarChartRenderer(chart, animator, vpHandler) {
    private val mCornersDimen: Float

    init {
        mCornersDimen = cornersDimen
    }

    override fun drawDataSet(
        c: Canvas,
        dataSet: IBarDataSet,
        index: Int,
    ) {
        val trans: com.github.mikephil.charting.utils.Transformer =
            mChart.getTransformer(dataSet.getAxisDependency())

        mBarBorderPaint.setColor(dataSet.getBarBorderColor())
        mBarBorderPaint.setStrokeWidth(
            com.github.mikephil.charting.utils.Utils.convertDpToPixel(
                dataSet.getBarBorderWidth(),
            ),
        )

        val drawBorder: Boolean = dataSet.getBarBorderWidth() > 0f

        val phaseX: Float = mAnimator.getPhaseX()
        val phaseY: Float = mAnimator.getPhaseY()

        // initialize the buffer
        val buffer: BarBuffer = mBarBuffers.get(index)
        buffer.setPhases(phaseX, phaseY)
        buffer.setDataSet(index)
        buffer.setInverted(mChart.isInverted(dataSet.getAxisDependency()))
        buffer.setBarWidth(mChart.getBarData().getBarWidth())

        buffer.feed(dataSet)

        trans.pointValuesToPixel(buffer.buffer)

        val isSingleColor = dataSet.getColors().size == 1

        if (isSingleColor) {
            mRenderPaint.setColor(dataSet.getColor())
        }

        for (j in 0 until buffer.size() step 4) {
            if (!mViewPortHandler.isInBoundsLeft(buffer.buffer.get(j + 2))) continue

            if (!mViewPortHandler.isInBoundsRight(buffer.buffer.get(j))) break

            if (!isSingleColor) {
                // Set the color for the currently drawn value. If the index
                // is out of bounds, reuse colors.
                mRenderPaint.setColor(dataSet.getColor(j / 4))
            }

            if (dataSet.getGradientColor() != null) {
                val gradientColor: GradientColor = dataSet.getGradientColor()
                mRenderPaint.setShader(
                    android.graphics.LinearGradient(
                        buffer.buffer.get(j),
                        buffer.buffer.get(j + 3),
                        buffer.buffer.get(j),
                        buffer.buffer.get(j + 1),
                        gradientColor.getStartColor(),
                        gradientColor.getEndColor(),
                        android.graphics.Shader.TileMode.MIRROR,
                    ),
                )
            }

            if (dataSet.getGradientColors() != null) {
                mRenderPaint.setShader(
                    android.graphics.LinearGradient(
                        buffer.buffer.get(j),
                        buffer.buffer.get(j + 3),
                        buffer.buffer.get(j),
                        buffer.buffer.get(j + 1),
                        dataSet.getGradientColor(j / 4).getStartColor(),
                        dataSet.getGradientColor(j / 4).getEndColor(),
                        android.graphics.Shader.TileMode.MIRROR,
                    ),
                )
            }

            c.drawRoundRect(
                buffer.buffer[j],
                buffer.buffer[j + 1],
                buffer.buffer[j + 2],
                buffer.buffer[j + 3],
                mCornersDimen,
                mCornersDimen,
                mRenderPaint,
            )

            if (drawBorder) {
                c.drawRoundRect(
                    buffer.buffer[j],
                    buffer.buffer[j + 1],
                    buffer.buffer[j + 2],
                    buffer.buffer[j + 3],
                    mCornersDimen,
                    mCornersDimen,
                    mBarBorderPaint,
                )
            }
        }
    }

    override fun drawHighlighted(
        c: Canvas?,
        indices: Array<out Highlight>?,
    ) {
        val barData = mChart.barData

        for (high in indices!!) {
            val set = barData.getDataSetByIndex(high.dataSetIndex)
            if (set == null || !set.isHighlightEnabled) continue
            val e = set.getEntryForXValue(high.x, high.y)
            if (!isInBoundsX(e, set)) continue
            val trans = mChart.getTransformer(set.axisDependency)
            mHighlightPaint.color = set.highLightColor
            mHighlightPaint.alpha = set.highLightAlpha
            val isStack = high.stackIndex >= 0 && e.isStacked
            val y1: Float
            val y2: Float
            if (isStack) {
                if (mChart.isHighlightFullBarEnabled) {
                    y1 = e.positiveSum
                    y2 = -e.negativeSum
                } else {
                    val range = e.ranges[high.stackIndex]
                    y1 = range.from
                    y2 = range.to
                }
            } else {
                y1 = e.y
                y2 = 0f
            }
            prepareBarHighlight(e.x, y1, y2, barData.barWidth / 2f, trans)
            setHighlightDrawPos(high, mBarRect)
            c!!.drawRoundRect(mBarRect, mCornersDimen, mCornersDimen, mHighlightPaint)
        }
    }
}
