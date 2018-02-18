package com.samtakoj.schedule.data

import io.objectbox.converter.PropertyConverter
import org.greenrobot.essentials.StringUtils
import java.util.*

/**
 * Created by artsiom.chuiko on 18/02/2018.
 */
class LongListConverter : PropertyConverter<List<Long>, String> {
    override fun convertToDatabaseValue(longs: List<Long>?): String? {
        if (longs == null || longs.isEmpty()) return null
        return StringUtils.join(longs, DELIMITER)
    }

    override fun convertToEntityProperty(s: String?): List<Long> {
        if (s == null || s.trim().isEmpty()) return Collections.emptyList()
        return s.split(DELIMITER).map { it.toLong() }
    }

    companion object {
        val DELIMITER = ","
    }
}
