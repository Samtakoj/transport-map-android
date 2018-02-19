package com.samtakoj.schedule.data

import com.samtakoj.schedule.model.WorkDay
import io.objectbox.converter.PropertyConverter
import java.util.*

/**
 * Created by artsiom.chuiko on 18/02/2018.
 */
class WorkDayConverter : PropertyConverter<List<WorkDay>, String> {
    override fun convertToDatabaseValue(days: List<WorkDay>?): String? {
        if (days == null || days.isEmpty()) return null
        return days.joinToString(ITEM_DELIMITER) { "${it.weekDay}$VAL_DELIMITER${it.countInterval}" }
    }

    override fun convertToEntityProperty(s: String?): List<WorkDay> {
        if (s == null || s.trim().isEmpty()) return Collections.emptyList()
        return s.split(ITEM_DELIMITER).map {
            val vals = it.split(VAL_DELIMITER)
            WorkDay(vals[0], vals[1].toInt())
        }
    }

    companion object {
        val ITEM_DELIMITER = ";"
        val VAL_DELIMITER = "-"
    }
}