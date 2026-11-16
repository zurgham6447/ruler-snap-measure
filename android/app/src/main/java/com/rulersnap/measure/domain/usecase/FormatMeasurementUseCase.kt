package com.rulersnap.measure.domain.usecase

import com.rulersnap.measure.domain.model.MeasureUnit
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.roundToInt

class FormatMeasurementUseCase {

    fun value(millimetres: Float, unit: MeasureUnit): String = when (unit) {
        MeasureUnit.MM -> decimals(millimetres, 1)
        MeasureUnit.CM -> decimals(millimetres / 10f, 2)
        MeasureUnit.INCH -> decimals(millimetres / MM_PER_INCH, 2)
    }

    fun millimetres(millimetres: Float): String = decimals(millimetres, 1)

    fun centimetres(millimetres: Float): String = decimals(millimetres / 10f, 2)

    fun inches(millimetres: Float): String = decimals(millimetres / MM_PER_INCH, 2)

    fun inchFraction(millimetres: Float): String {
        val totalInches = millimetres / MM_PER_INCH
        val sixteenths = (totalInches * 16f).roundToInt()
        if (sixteenths <= 0) return ZERO
        val whole = sixteenths / 16
        var numerator = sixteenths % 16
        var denominator = 16
        while (numerator > 0 && numerator % 2 == 0) {
            numerator /= 2
            denominator /= 2
        }
        return when {
            numerator == 0 -> whole.toString()
            whole == 0 -> numerator.toString() + SLASH + denominator.toString()
            else -> whole.toString() + SPACE + numerator.toString() + SLASH + denominator.toString()
        }
    }

    fun scale(pxPerMm: Float): String = decimals(pxPerMm, 2)

    private fun decimals(value: Float, places: Int): String {
        val safe = if (value.isNaN() || value.isInfinite()) 0f else value
        val factor = when (places) {
            1 -> 10f
            2 -> 100f
            else -> 1f
        }
        val rounded = (safe * factor).roundToInt() / factor
        var whole = floor(abs(rounded).toDouble()).toInt()
        var fractionValue = ((abs(rounded) - whole) * factor).roundToInt()
        if (fractionValue >= factor.toInt()) {
            whole += 1
            fractionValue = 0
        }
        val fraction = fractionValue.toString().padStart(places, PAD_ZERO)
        val sign = if (rounded < 0f) MINUS else EMPTY
        return sign + whole.toString() + DOT + fraction
    }

    companion object {
        const val MM_PER_INCH = 25.4f
        private const val ZERO = "0"
        private const val SLASH = "/"
        private const val SPACE = " "
        private const val DOT = "."
        private const val MINUS = "-"
        private const val EMPTY = ""
        private const val PAD_ZERO = '0'
    }
}
