package com.samtakoj.shedule.model.converter;

import org.greenrobot.essentials.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.objectbox.converter.PropertyConverter;

/**
 * Created by artsiom.chuiko on 17/04/2017.
 */

public class LongListConverter implements PropertyConverter<List<Long>, String> {
    private static final String DELIMITER = ",";

    @Override
    public List<Long> convertToEntityProperty(String s) {
        if (s == null || s.trim().isEmpty()) return Collections.emptyList();
        final String[] items = s.split(DELIMITER);
        final List<Long> result = new ArrayList<>();
        for (String item : items) {
            result.add(Long.parseLong(item));
        }
        return result;
    }

    @Override
    public String convertToDatabaseValue(List<Long> longs) {
        if (longs == null || longs.isEmpty()) return null;
        return StringUtils.join(longs, DELIMITER);
    }
}
