package com.samtakoj.shedule.model.converter;

import com.samtakoj.shedule.model.WorkDay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.objectbox.converter.PropertyConverter;

/**
 * Created by artsiom.chuiko on 17/04/2017.
 */

public class WorkDayConverter implements PropertyConverter<List<WorkDay>, String> {
    private static final String ITEM_DELIMITER = ";";
    private static final String VAL_DELIMITER = "-";

    @Override
    public List<WorkDay> convertToEntityProperty(String s) {
        if (s == null || s.trim().isEmpty()) return Collections.emptyList();
        final String[] items = s.split(ITEM_DELIMITER);
        final List<WorkDay> result = new ArrayList<>();
        for (String item : items) {
            final String[] vals = item.split(VAL_DELIMITER);
            if (vals.length == 2) {
                result.add(new WorkDay(vals[0], Integer.parseInt(vals[1])));
            }
        }
        return result;
    }

    @Override
    public String convertToDatabaseValue(List<WorkDay> workDays) {
        if (workDays == null || workDays.isEmpty()) return null;
        final StringBuilder builder = new StringBuilder();
        final WorkDay first = workDays.get(0);
        builder.append(first.getWeekDay()).append(VAL_DELIMITER).append(first.getCountInterval());
        for (int i = 1; i < workDays.size(); i++) {
            final WorkDay workDay = workDays.get(i);
            if (workDay != null) {
                builder.append(ITEM_DELIMITER)
                        .append(workDay.getWeekDay())
                        .append(VAL_DELIMITER)
                        .append(workDay.getCountInterval());
            }
        }
        return builder.toString();
    }
}
