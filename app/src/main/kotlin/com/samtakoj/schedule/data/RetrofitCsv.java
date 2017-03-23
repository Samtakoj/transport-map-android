package com.samtakoj.schedule.data;

import com.google.common.collect.Lists;
import com.nytimes.android.external.store.base.Parser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okio.BufferedSource;

/**
 * Created by artsiom.chuiko on 21/03/2017.
 */

/**
 * An example of How_To_Use:
 *
 * StoreBuilder.<BarCode, BufferedSource, List<Stop>>parsedWithKey()
 *      .parser(RetrofitCsv.createSourceParser(Stop.class, true, ","))
 *      .open();
 *
 */
public final class RetrofitCsv {
    private RetrofitCsv() {}

    public static <Parsed> Parser<BufferedSource, List<Parsed>> createSourceParser(final Class<Parsed> clazz, final boolean skipHeaders, final String reges) {
        return new Parser<BufferedSource, List<Parsed>>() {
            @Override
            public List<Parsed> call(BufferedSource source) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(source.inputStream(), Charset.forName("UTF-8")))) {
                    String line = null;
                    boolean skip = skipHeaders;
                    final List<Parsed> parsed = Lists.newArrayList();
                    while ((line = reader.readLine()) != null) {
                        if (skip) skip = false;
                        else {
                            parsed.add(createObject(clazz, line.split(reges)));
                        }
                    }
                    return parsed;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    private static <Parsed> Parsed createObject(Class<Parsed> clazz, String...data) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        final Constructor<?> first = clazz.getConstructors()[0];
        final Object castedParams = stringTypeCast(first.getParameterTypes(), data);
        return (Parsed) first.newInstance(castedParams);
    }

    private static Object[] stringTypeCast(Class<?>[] paramTypes, String...data) {
        final List<Object> result = Lists.newArrayList();
        for (int i = 0; i < paramTypes.length; i++) {
            result.add(typeAdjusters.get(paramTypes[i]).adjust(data[i]));
        }
        return result.toArray();
    }

    private interface TypeAdjuster<Type> {
        Type adjust(String o);
    }

    private static final TypeAdjuster<String> stringAdjuster = new TypeAdjuster<String>() {
        @Override
        public String adjust(String o) {
            return o;
        }
    };

    private static final TypeAdjuster<Integer> integerAdjuster = new TypeAdjuster<Integer>() {
        @Override
        public Integer adjust(String o) {
            return Integer.parseInt(o);
        }
    };

    private static final TypeAdjuster<Double> doubleAdjuster = new TypeAdjuster<Double>() {
        @Override
        public Double adjust(String o) {
            return Double.parseDouble(o);
        }
    };

    private static final TypeAdjuster<Float> floatAdjuster = new TypeAdjuster<Float>() {
        @Override
        public Float adjust(String o) {
            return Float.parseFloat(o);
        }
    };

    private static final TypeAdjuster<Boolean> boolAdjuster = new TypeAdjuster<Boolean>() {
        @Override
        public Boolean adjust(String o) {
            return Boolean.parseBoolean(o);
        }
    };

    private static final TypeAdjuster<Long> longAdjuster = new TypeAdjuster<Long>() {
        @Override
        public Long adjust(String o) {
            return Long.parseLong(o);
        }
    };

    private static final Map<Class<?>, TypeAdjuster<?>> typeAdjusters = new HashMap<Class<?>, TypeAdjuster<?>>() {{
        put(Integer.class, integerAdjuster);
        put(int.class, integerAdjuster);
        put(Double.class, doubleAdjuster);
        put(double.class, doubleAdjuster);
        put(Float.class, floatAdjuster);
        put(float.class, floatAdjuster);
        put(Boolean.class, boolAdjuster);
        put(boolean.class, boolAdjuster);
        put(String.class, stringAdjuster);
        put(long.class, longAdjuster);
        put(Long.class, longAdjuster);
    }};
}

