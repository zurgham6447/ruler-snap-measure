package com.rulersnap.measure.data.local

import android.content.Context
import android.content.SharedPreferences
import com.rulersnap.measure.core.config.GameConfig
import com.rulersnap.measure.domain.model.Measurement

class MeasurePreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    val devicePxPerMm: Float = (context.resources.displayMetrics.xdpi / MM_PER_INCH)
        .coerceIn(GameConfig.MIN_PX_PER_MM, GameConfig.MAX_PX_PER_MM)

    var pxPerMm: Float
        get() = prefs.getFloat(KEY_PX_PER_MM, devicePxPerMm)
        set(value) = prefs.edit().putFloat(KEY_PX_PER_MM, value).apply()

    var isUserCalibrated: Boolean
        get() = prefs.getBoolean(KEY_USER_CALIBRATED, false)
        set(value) = prefs.edit().putBoolean(KEY_USER_CALIBRATED, value).apply()

    var unitKey: String
        get() = prefs.getString(KEY_UNIT, DEFAULT_UNIT) ?: DEFAULT_UNIT
        set(value) = prefs.edit().putString(KEY_UNIT, value).apply()

    var orientationKey: String
        get() = prefs.getString(KEY_ORIENTATION, DEFAULT_ORIENTATION) ?: DEFAULT_ORIENTATION
        set(value) = prefs.edit().putString(KEY_ORIENTATION, value).apply()

    var hapticsEnabled: Boolean
        get() = prefs.getBoolean(KEY_HAPTICS, true)
        set(value) = prefs.edit().putBoolean(KEY_HAPTICS, value).apply()

    var calibrationHintSeen: Boolean
        get() = prefs.getBoolean(KEY_HINT_SEEN, false)
        set(value) = prefs.edit().putBoolean(KEY_HINT_SEEN, value).apply()

    val isFirstLaunch: Boolean
        get() = !prefs.contains(KEY_HISTORY) && !prefs.getBoolean(KEY_HINT_SEEN, false)

    fun readHistory(): List<Measurement> {
        val raw = prefs.getString(KEY_HISTORY, EMPTY).orEmpty()
        if (raw.isBlank()) return emptyList()
        return raw.split(ENTRY_SEPARATOR).mapNotNull { decode(it) }
    }

    fun writeHistory(items: List<Measurement>) {
        val capped = items.take(GameConfig.MAX_HISTORY)
        val raw = capped.joinToString(ENTRY_SEPARATOR) { encode(it) }
        prefs.edit().putString(KEY_HISTORY, raw).apply()
    }

    private fun encode(item: Measurement): String {
        val safeLabel = item.label
            .replace(FIELD_SEPARATOR, SPACE)
            .replace(ENTRY_SEPARATOR, SPACE)
            .trim()
        return listOf(
            item.id.toString(),
            safeLabel,
            item.millimetres.toString(),
            item.createdAt.toString()
        ).joinToString(FIELD_SEPARATOR)
    }

    private fun decode(raw: String): Measurement? {
        val parts = raw.split(FIELD_SEPARATOR)
        if (parts.size < 4) return null
        val id = parts[0].toLongOrNull() ?: return null
        val millimetres = parts[2].toFloatOrNull() ?: return null
        val createdAt = parts[3].toLongOrNull() ?: id
        return Measurement(id = id, label = parts[1], millimetres = millimetres, createdAt = createdAt)
    }

    companion object {
        private const val FILE_NAME = "ruler_snap_prefs"
        private const val KEY_PX_PER_MM = "px_per_mm"
        private const val KEY_USER_CALIBRATED = "is_user_calibrated"
        private const val KEY_UNIT = "unit"
        private const val KEY_ORIENTATION = "orientation"
        private const val KEY_HAPTICS = "haptics"
        private const val KEY_HINT_SEEN = "calibration_hint_seen"
        private const val KEY_HISTORY = "history"
        private const val DEFAULT_UNIT = "MM"
        private const val DEFAULT_ORIENTATION = "VERTICAL"
        private const val ENTRY_SEPARATOR = ";"
        private const val FIELD_SEPARATOR = "|"
        private const val SPACE = " "
        private const val EMPTY = ""
        private const val MM_PER_INCH = 25.4f
    }
}
